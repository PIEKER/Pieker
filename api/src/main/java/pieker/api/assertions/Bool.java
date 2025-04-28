package pieker.api.assertions;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.jexl3.*;
import pieker.api.assertions.util.Util;
import pieker.api.exception.ValidationException;
import pieker.api.Evaluation;


@Setter
@Getter
public class Bool implements Evaluation {

    private final boolean boolType;
    private String expression;
    private String value;
    private boolean success = false;
    private String errorMessage = "";


    public Bool(boolean boolType, String expression, String value) {
        this.boolType = boolType;
        this.expression = expression;
        this.value = value;
    }

    public void validate(int line) throws ValidationException {
        String[] args = Util.getArgumentsFromString(this.expression);
        if (args.length != 1){
            throw new ValidationException("invalid amount of arguments on an assertBool!" +
                    " line: " + line +
                    " args: " + args.length +
                    " expression: " + this.expression);
        }
        args = Util.getArgumentsFromString(this.value);
        if (args.length == 0 || args.length > 2){
            throw new ValidationException("invalid amount of arguments on an assertBool value!" +
                    " line " + line +
                    " args: " + args.length +
                    " value: " + this.value);
        }
    }

    @Override
    public String getAssertType() {
        return this.getClass().getSimpleName();
    }

    public void evaluate(String arg){
        try{
            JexlEngine jexl = new JexlBuilder().create();
            JexlExpression jexlExpression = jexl.createExpression(arg + " " + expression);
            JexlContext context = new MapContext();
            boolean result = (Boolean) jexlExpression.evaluate(context);
            this.success = this.boolType == result;
        } catch (JexlException e){
            this.errorMessage = "Unable to evaluate boolean because of JexlException: " + e.getMessage();
        }
    }
}