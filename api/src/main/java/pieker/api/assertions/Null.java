package pieker.api.assertions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pieker.api.Evaluation;

@Setter
@Getter
public class Null implements Evaluation {

    @JsonIgnore
    private final boolean isNull;
    private boolean success = false;
    private String errorMessage = "";
    @JsonIgnore
    private String value;

    public Null(boolean isNull, String value) {
        this.isNull = isNull;
        this.value = value;

    }

    @Override
    public String getAssertType() {
        return "assert" + (this.isNull? "" : "Not") + "Equals";
    }

    @Override
    public String getAssertExpression() {
        return "(" + this.value + ") == " + (isNull ? "null" : "not null");
    }

    @Override
    public void evaluate(String arg) {
        this.success = isNull == (arg == null || arg.isBlank() || arg.equals("null"));
        if (!this.success){
            this.errorMessage = "Expected: " + (isNull ? "null" : "not null") + " but got: " + arg;
        }
    }
}
