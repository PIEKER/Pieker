package pieker.dsl.model.assertions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import pieker.api.Assertion;
import pieker.api.assertions.Assert;
import pieker.api.assertions.Bool;
import pieker.api.assertions.Equals;
import pieker.api.assertions.Null;
import pieker.api.Evaluation;
import pieker.common.connection.Http;
import pieker.dsl.PiekerDslException;
import pieker.dsl.architecture.Engine;
import pieker.dsl.architecture.exception.ValidationException;
import pieker.dsl.architecture.preprocessor.FileManager;
import pieker.dsl.util.Util;

import java.util.*;

@Setter
@Getter
@Slf4j
public class DatabaseAssert extends Assert {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String ASSERT_PLUGIN = "Database";
    private static final String SELECT = "SELECT ";
    private static final String FROM = " FROM ";
    private static final String WHERE = " WHERE ";
    private static final String VALUE = " value: ";

    private final String uuid = java.util.UUID.randomUUID().toString().replace("-","_");
    private final String assertableTableName = "assert_" + this.identifier+ "_" + this.uuid;
    protected final String assertableTableQuery = "CREATE TABLE " + this.assertableTableName + " AS ";
    protected final String dropAssertableTableQuery = "DROP TABLE " + this.assertableTableName;

    private String database;
    protected String tableSelect;
    private String jdbcUrl;
    private String username;
    private String password;

    public DatabaseAssert(String arguments) {
        super(ASSERT_PLUGIN);

        String[] args = Util.getArgumentsFromString(arguments);
        if (args.length != 3){
            throw new PiekerDslException("invalid amount of arguments on an DatabaseAssert!" +
                    " args: " + args.length +
                    VALUE + arguments);
        }
        this.identifier = args[0];
        this.database = args[1];
        this.tableSelect = args[2];
        this.jdbcUrl = "http://"
                + System.getProperty("orchestratorHost", "127.0.0.1") + ":"
                + System.getProperty("orchestratorPort", "42690");

        this.setRequiredComponent(this.identifier); // enable component reboot
    }

    private DatabaseAssert(DatabaseAssert databaseAssert){
        super(databaseAssert);
        this.database = databaseAssert.database;
        this.tableSelect = databaseAssert.tableSelect;
        this.jdbcUrl = databaseAssert.jdbcUrl;
        this.username = databaseAssert.username;
        this.password = databaseAssert.password;
    }

    @Override
    public void validate(int line){
        this.getBoolList().forEach(bool -> {
            bool.validate(line);
            this.validateValue(line, bool.getValue());
        });
        this.getEqualsList().forEach(equals -> this.validateValue(line, equals.getValue()));
        this.getNullList().forEach(nullAssert ->this.validateValue(line, nullAssert.getValue()));
    }

    @Override
    public void processAssert() {
        // no processing required
    }

    private void validateValue(int line, String value){
        String[] args = Util.getArgumentsFromString(value);
        if (args.length > 2){
            throw new ValidationException("invalid amount of arguments on an DatabaseAssert!" +
                    " line" + line +
                    " args: " + args.length +
                    VALUE + value);
        }
    }

    @Override
    public void prepare() {
        log.debug("creating assertable Table");
        this.tableSelect = this.getParameter(this.tableSelect);
        String response = this.sendQuery(this.assertableTableQuery + this.tableSelect);
        try {
            JsonNode rootNode = MAPPER.readTree(response);

            JsonNode errorNode = rootNode.get("error");
            if (errorNode != null){
                String message = "unable to create temporary table: " + errorNode + ". Invalidating evaluation ...";
                log.error(message);
                this.invalidateAssert(message);
                this.skip = true;
            }
        } catch (JsonProcessingException e){
            log.error("unable to parse gateway-response: {}. Errors possible while evaluating due to missing table.", e.getMessage());
        }
    }

    @Override
    public void evaluate() {
        if (this.skip) return;

        this.boolList.forEach(this::evaluateBoolNode);
        this.equalsList.forEach(this::evaluateEqualsNode);
        this.nullList.forEach(this::evaluateNullNode);

        log.debug("drop assertable Table");
        String response = this.sendQuery(this.dropAssertableTableQuery);
        log.debug(response);
    }

    @Override
    protected void evaluateBoolNode(Bool bool) {
        String[] values = Util.getArgumentsFromString(bool.getValue());
        if (values.length == 0 || values.length > 2){
            String error = "invalid amount of arguments on an assertBool value! args: " + values.length + VALUE + bool.getValue();
            log.error(error);
            bool.setErrorMessage(error);
            return;
        }
        String query = SELECT + values[0] + FROM + this.assertableTableName;
        if (values.length == 2){
            query += WHERE + values[1];
        }

        String response = this.sendQuery(query);
        this.evaluateQueryResponse(bool, response);
    }

    @Override
    protected void evaluateEqualsNode(Equals equals){
        String[] values = Util.getArgumentsFromString(equals.getValue());
        if (values.length == 0 || values.length > 2){
            String error = "invalid amount of arguments on an assertEquals value! args: " + values.length + VALUE + equals.getValue();
            log.error(error);
            equals.setErrorMessage(error);
            return;
        }
        String query = SELECT + values[0] + FROM + this.assertableTableName;
        if (values.length == 2){
            query += WHERE + values[1];
        }

        String response = this.sendQuery(query);
        this.evaluateQueryResponse(equals, response);
    }

    @Override
    protected void evaluateNullNode(Null nuLL){
        String[] values = Util.getArgumentsFromString(nuLL.getValue());
        if (values.length == 0 || values.length > 2){
            String error = "invalid amount of arguments on an assertNull value! args: " + values.length + VALUE + nuLL.getValue();
            log.error(error);
            nuLL.setErrorMessage(error);
            return;
        }
        String query = SELECT + values[0] + FROM + this.assertableTableName;
        if (values.length == 2){
            query += WHERE + values[1];
        }

        String response = this.sendQuery(query);
        this.evaluateQueryResponse(nuLL, response);
    }

    @Override
    public List<Evaluation> getEvaluation() {
        List<Evaluation> evaluationList = new ArrayList<>();
        evaluationList.addAll(this.boolList);
        evaluationList.addAll(this.equalsList);
        evaluationList.addAll(this.nullList);
        return evaluationList;
    }

    @Override
    public Assertion copy() {
        return new DatabaseAssert(this);
    }

    @Override
    public boolean requiresConnectionParam(){
        return false;
    }

    @Override
    public void setConnectionParam(String gatewayUrl) {
        // GATEWAY is a global property -> assigned during constructor call.
    }

    private String sendQuery(String query){
        String endpointUrl = "/db/" + this.database + "/query";
        String body = "{\"query\":\"" + query + "\"}";
        return Http.send(this.identifier, this.jdbcUrl + endpointUrl, "POST", 3000, 30000, "", body, "json");
    }

    private void evaluateQueryResponse(Evaluation evaluation, String response){
        try{

            JsonNode rootNode = MAPPER.readTree(response);

            JsonNode errorNode = rootNode.get("error");
            if (errorNode != null){
                evaluation.setErrorMessage(errorNode.asText());
                return;
            }

            JsonNode resultNode = rootNode.get("result");
            if (resultNode != null && resultNode.isObject()) {
                ObjectNode resultObject = (ObjectNode) resultNode;

                if (evaluation instanceof Null nuLL && resultObject.properties().isEmpty()){
                    nuLL.setSuccess(true);
                    return;
                }

                resultObject.properties().forEach(entry -> {
                    JsonNode valueNode = entry.getValue();

                    if (valueNode.isArray()) {
                        ArrayNode arrayNode = (ArrayNode) valueNode;
                        List<String> valueList = new ArrayList<>();

                        arrayNode.forEach(item -> valueList.add(item.asText()));
                        valueList.forEach(evaluation::evaluate);
                    } else {
                        // Handle non-array values if necessary
                        String singleValue = valueNode.asText();
                        evaluation.evaluate(singleValue);
                    }
                });
            } else {
                log.warn("result node is missing or not an object");
            }

        } catch (JsonProcessingException e){
            log.error("unable to parse gateway-response: {}", e.getMessage());
        }
    }

    /**
     * Resolves parameter from a stored String value, including possible file referencing.
     * @return query string
     */
    public String getParameter(String parameter){
        if (parameter.startsWith(FileManager.PREFIX)) {
            return Engine.getFileManager().getDataFromFileHash(parameter);
        }
        return parameter;
    }
}