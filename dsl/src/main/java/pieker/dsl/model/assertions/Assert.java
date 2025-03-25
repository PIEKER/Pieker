package pieker.dsl.model.assertions;

import lombok.Getter;
import lombok.Setter;
import pieker.dsl.code.exception.ValidationException;
import pieker.dsl.util.Util;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public abstract class Assert {

    @Setter
    @Getter
    public static class Bool{

        private final boolean boolType;
        private String expression;
        private String value;


        public Bool(boolean boolType, String expression, String value) {
            this.boolType = boolType;
            this.expression = expression;
            this.value = value;
        }

        public void validate(){
            String[] args = Util.getArgumentsFromString(this.expression);
            if (args.length != 2){
                throw new ValidationException("invalid amount of arguments on an assertBool! " +
                        "args: " + args.length +
                        "expression: " + this.expression);
            }
        }
    }

    @Setter
    @Getter
    public static class Equals{
        private String expected;
        private String value;

        public Equals(String expected, String value) {
            this.expected = expected;
            this.value = value;
        }
    }

    @Setter
    @Getter
    public static class Null{
        private final boolean isNull;
        private String value;

        public Null(boolean isNull, String value) {
            this.isNull = isNull;
            this.value = value;
        }
    }

    private final String identifier;

    private final List<Bool> boolList = new ArrayList<>();
    private final List<Equals> equalsList = new ArrayList<>();
    private final List<Null> nullList = new ArrayList<>();

    protected Assert(String identifier){
        this.identifier = identifier;
    }

    public void addBoolAssertion(String boolType, String expression, String value){
        this.boolList.add(new Bool(Util.parseBoolean(boolType), expression, value));
    }

    public void addEqualsAssertion(String expected, String value){
        this.equalsList.add(new Equals(expected, value));
    }

    public void addNullAssertion(String isNull, String value){
        this.nullList.add(new Null(Util.parseBoolean(isNull), value));
    }

    public abstract void validate();
    public abstract void processAssert();
}