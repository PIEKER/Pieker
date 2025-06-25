package pieker.api.assertions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pieker.api.Evaluation;

@Setter
@Getter
public class Equals implements Evaluation {

    @JsonIgnore
    private boolean isEqual;
    private boolean success = false;
    private String errorMessage = "";
    @JsonIgnore
    private String expected;
    @JsonIgnore
    private String value;

    public Equals(boolean isEqual, String expected, String value) {
        this.isEqual = isEqual;
        this.expected = expected;
        this.value = value;
    }

    @Override
    public String getAssertType() {
        return "assert" + (this.isEqual? "" : "Not") + "Equals";
    }
    @Override
    public String getAssertExpression() {
        String operator = this.isEqual ? "==" : "!=";
        return "(" + this.value + ") " + operator + " " + this.expected;
    }

    @Override
    public void evaluate(String arg) {
        this.success = this.isEqual == arg.trim().equals(this.expected.trim());
        if (!this.success){
            this.errorMessage = "Expected: " + this.expected + " but got: " + arg;
        }
    }
}
