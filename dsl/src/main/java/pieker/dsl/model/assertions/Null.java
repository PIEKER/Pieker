package pieker.dsl.model.assertions;

import lombok.Getter;
import lombok.Setter;
import pieker.common.Evaluation;

@Setter
@Getter
public class Null implements Evaluation {

    private final boolean isNull;
    private String value;
    private boolean success = false;
    private String errorMessage = "";

    public Null(boolean isNull, String value) {
        this.isNull = isNull;
        this.value = value;

    }

    @Override
    public String getAssertType() {
        return this.getClass().getSimpleName();
    }

    @Override
    public void evaluate(String arg) {
        this.success = isNull == (arg == null || arg.isBlank());
    }
}
