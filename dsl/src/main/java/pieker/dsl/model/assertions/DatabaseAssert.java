package pieker.dsl.model.assertions;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import pieker.api.assertions.Assert;
import pieker.api.assertions.Bool;
import pieker.api.assertions.Equals;
import pieker.api.assertions.Null;
import pieker.api.Evaluation;
import pieker.dsl.PiekerDslException;
import pieker.dsl.architecture.exception.ValidationException;
import pieker.dsl.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Setter
@Getter
@Slf4j
public class DatabaseAssert extends Assert {

    private static final String ASSERT_PLUGIN = "Database";
    private static final String SELECT = "SELECT ";
    private static final String FROM = " FROM ";
    private static final String WHERE = " WHERE ";
    private static final String VALUE = " value: ";

    private final String uuid = java.util.UUID.randomUUID().toString().replace("-","_");
    private final String assertableTableName = "assert_" + this.identifier+ "_" + this.uuid;
    protected final String assertableTableQuery = "CREATE TABLE " + this.assertableTableName + " AS ";
    protected final String dropAssertableTableQuery = "DROP TABLE " + this.assertableTableName;

    protected String tableSelect;
    private String jdbcUrl;
    private String username;
    private String password;

    public DatabaseAssert(String arguments) {
        super(ASSERT_PLUGIN);

        String[] args = Util.getArgumentsFromString(arguments);
        if (args.length != 2){
            throw new PiekerDslException("invalid amount of arguments on an DatabaseAssert!" +
                    " args: " + args.length +
                    VALUE + arguments);
        }
        this.identifier = args[0];
        this.tableSelect = args[1];
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
    public void evaluate() {
        log.debug("creating assertable Table");
        String response = this.sendQuery(this.assertableTableQuery + tableSelect);
        log.debug(response);

        this.boolList.forEach(this::evaluateBoolNode);
        this.equalsList.forEach(this::evaluateEqualsNode);
        this.nullList.forEach(this::evaluateNullNode);

        log.debug("drop assertable Table");
        response = this.sendQuery(this.dropAssertableTableQuery);
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

        String result = this.sendQuery(query);
        String[] valueList = result.split("\\|" );
        Arrays.stream(valueList).forEach(bool::evaluate);
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

        String result = this.sendQuery(query);
        String[] valueList = result.split("\\|" );
        Arrays.stream(valueList).forEach(equals::evaluate);
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

        String result = this.sendQuery(query);
        String[] valueList = result.split("\\|" );
        if (valueList.length == 0) nuLL.setSuccess(nuLL.isNull());
        Arrays.stream(valueList).forEach(nuLL::evaluate);
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
    public boolean requiresConnectionParam(){
        return true;
    }

    @Override
    public void setConnectionParam(JSONObject cpJson) {
        this.jdbcUrl = cpJson.getString("targetUrlEnv");
        this.username = cpJson.getString("usernameEnv");
        this.password = cpJson.getString("passwordEnv");
    }

    private String sendQuery(String query){
        return pieker.common.connection.Sql.send(this.identifier, this.jdbcUrl, this.username, this.password, query);
    }
}