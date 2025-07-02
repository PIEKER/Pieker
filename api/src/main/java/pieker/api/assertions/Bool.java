package pieker.api.assertions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.jexl3.*;
import pieker.api.assertions.util.Util;
import pieker.api.exception.ValidationException;
import pieker.api.Evaluation;


@Setter
@Getter
public class Bool implements Evaluation {

    @JsonIgnore
    private final boolean boolType;
    @JsonIgnore
    private String expression;
    @JsonIgnore
    private String value;
    private boolean success = false;
    private String errorMessage = "";


    public Bool(boolean boolType, String expression, String value) {
        this.boolType = boolType;
        this.expression = expression;
        this.value = value;
    }

    private Bool(Bool bool){
        this.boolType = bool.boolType;
        this.expression = bool.expression;
        this.value = bool.value;
        this.success = bool.success;
        this.errorMessage = bool.errorMessage;
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
        return "assert" + (this.boolType ? "True" : "False");
    }

    public void evaluate(String arg){
        try{
            JexlEngine jexl = new JexlBuilder().create();
            JexlExpression jexlExpression = jexl.createExpression(arg + " " + expression);
            JexlContext context = new MapContext();
            boolean result = (Boolean) jexlExpression.evaluate(context);
            this.success = this.boolType == result;
            if (!this.success){
                this.errorMessage = "Expression failed: " + this.boolType + " != " + result + " for expression: " + arg + " " + expression;
            }
        } catch (JexlException e){
            this.errorMessage = "Unable to evaluate boolean because of JexlException: " + e.getMessage();
        }
    }

    @Override
    public Evaluation copy() {
        return new Bool(this);
    }

    /**
     * @return the DSL value and expression into a single String.
     */
    public String getAssertExpression(){
        // only used for result JSON
        return "(" + this.value + ") " + this.expression;
    }
}