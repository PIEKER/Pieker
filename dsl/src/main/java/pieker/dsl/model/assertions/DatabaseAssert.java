package pieker.dsl.model.assertions;

import lombok.Getter;
import lombok.Setter;
import pieker.dsl.code.Engine;
import pieker.dsl.code.component.SupervisorTraffic;
import pieker.dsl.code.exception.ValidationException;
import pieker.dsl.code.template.architecture.Sql;
import pieker.dsl.util.Util;

import java.util.UUID;

@Setter
@Getter
public class DatabaseAssert extends Assert {

    private String table;

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
        Sql assertableTable = new Sql(this.getIdentifier(), table);
        String identifier = "zzz-database-assert" + UUID.randomUUID();
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
}