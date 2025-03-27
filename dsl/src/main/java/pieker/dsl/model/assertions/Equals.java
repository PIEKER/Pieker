package pieker.dsl.model.assertions;

import lombok.Getter;
import lombok.Setter;
import pieker.common.Evaluation;

@Setter
@Getter
public class Equals implements Evaluation {

    private String expected;
    private String value;
    private boolean success = false;
    private String errorMessage = "";

    public Equals(String expected, String value) {
        this.expected = expected;
        this.value = value;
    }

    @Override
    public String getAssertType() {
        return this.getClass().getSimpleName();
    }

    @Override
    public void evaluate(String[] args) {
        //todo
    }
}
