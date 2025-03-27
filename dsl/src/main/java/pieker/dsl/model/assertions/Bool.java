package pieker.dsl.model.assertions;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.jexl3.*;
import pieker.common.Evaluation;
import pieker.dsl.code.exception.ValidationException;
import pieker.dsl.util.Util;

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

    public void validate(){
        String[] args = Util.getArgumentsFromString(this.expression);
        if (args.length != 2){
            throw new ValidationException("invalid amount of arguments on an assertBool! " +
                    "args: " + args.length +
                    "expression: " + this.expression);
        }
        args = Util.getArgumentsFromString(this.value);
        if (args.length == 0 || args.length > 2){
            throw new ValidationException("invalid amount of arguments on an assertBool value! " +
                    "args: " + args.length +
                    "value: " + this.value);
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