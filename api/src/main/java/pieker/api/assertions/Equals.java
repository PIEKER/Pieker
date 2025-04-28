package pieker.api.assertions;

import lombok.Getter;
import lombok.Setter;
import pieker.api.Evaluation;

@Setter
@Getter
public class Equals implements Evaluation {

    private boolean isEqual;
    private String expected;
    private String value;
    private boolean success = false;
    private String errorMessage = "";

    public Equals(boolean isEqual, String expected, String value) {
        this.isEqual = isEqual;
        this.expected = expected;
        this.value = value;
    }

    @Override
    public String getAssertType() {
        return this.getClass().getSimpleName();
    }

    @Override
    public void evaluate(String arg) {
        this.success = this.isEqual == arg.trim().equals(this.expected.trim());
    }
}
