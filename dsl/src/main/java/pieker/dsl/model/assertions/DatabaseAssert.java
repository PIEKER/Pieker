package pieker.dsl.model.assertions;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import pieker.common.Evaluation;
import pieker.dsl.code.Engine;
import pieker.dsl.code.component.SupervisorTraffic;
import pieker.dsl.code.exception.PiekerProcessingException;
import pieker.dsl.code.exception.ValidationException;
import pieker.dsl.code.template.architecture.Sql;
import pieker.dsl.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Setter
@Getter
@Slf4j
public class DatabaseAssert extends Assert {

    public static final String SUPERVISOR_TRAFFIC_PREFIX = "zzz-database-assert";
    private static final String SELECT = "SELECT ";
    private static final String FROM = " FROM ";
    private static final String WHERE = " WHERE ";
    private static final String VALUE = " value: ";
    private final String uuid = java.util.UUID.randomUUID().toString().replace("-","_");
    private final String assertableTableName = "assert_" + this.identifier+ "_" + this.uuid;
    protected final String assertableTableQuery = "CREATE TABLE " + this.assertableTableName + " AS ";

    protected final String dropAssertableTableQuery = "DROP TABLE " + this.assertableTableName;
    protected String tableSelect;

    public DatabaseAssert(String identifier) {
        super(identifier);
    }

    @Override
    public void validate(){
        this.getBoolList().forEach(bool -> {
            bool.validate();
            this.validateValue(bool.getValue());
        });
        this.getEqualsList().forEach(equals -> this.validateValue(equals.getValue()));
        this.getNullList().forEach(nullAssert ->this.validateValue(nullAssert.getValue()));
    }

    @Override
    public void processAssert() {
        Sql assertableTable = new Sql(this.identifier, this.assertableTableQuery + tableSelect);
        String identifier = SUPERVISOR_TRAFFIC_PREFIX + java.util.UUID.randomUUID();
        Engine.getCurrentStep().addStepComponent(identifier, new SupervisorTraffic(identifier, assertableTable));
    }

    private void validateValue(String value){
        String[] args = Util.getArgumentsFromString(value);
        if (args.length > 2){
            throw new ValidationException("invalid amount of arguments on an DatabaseAssert! " +
                    "args: " + args.length +
                    "value: " + value);
        }
    }

    @Override
    public void evaluate(String[] args) {
        if (args.length < 4){
            throw new PiekerProcessingException(" invalid amount of arguments on Database-Evaluation provided: was "+ args.length + "expected 4");
        }

        this.boolList.forEach(bool -> this.evaluateBoolNode(bool, args));
        this.equalsList.forEach(equals -> this.evaluateEqualsNode(equals, args));
        this.nullList.forEach(nuLL -> this.evaluateNullNode(nuLL, args));

        log.debug("drop assertable Table");
        Sql dropAssertableTable = new Sql(this.identifier, this.dropAssertableTableQuery);
        String response = dropAssertableTable.sendTraffic(new String[]{args[1], args[2], args[3]});
        log.debug(response);
    }

    @Override
    protected void evaluateBoolNode(Bool bool, String[] args) {
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

        String result = pieker.common.connection.Sql.send(this.identifier, args[1], args[2], args[3], query);
        String[] valueList = result.split("\\|" );
        Arrays.stream(valueList).forEach(bool::evaluate);
    }

    @Override
    protected void evaluateEqualsNode(Equals equals, String[] args){
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

        String result = pieker.common.connection.Sql.send(this.identifier, args[1], args[2], args[3], query);
        String[] valueList = result.split("\\|" );
        Arrays.stream(valueList).forEach(equals::evaluate);
    }

    @Override
    protected void evaluateNullNode(Null nuLL, String[] args){
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

        String result = pieker.common.connection.Sql.send(this.identifier, args[1], args[2], args[3], query);
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
}