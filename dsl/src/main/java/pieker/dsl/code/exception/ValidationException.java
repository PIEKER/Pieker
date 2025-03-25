package pieker.dsl.code.exception;

import pieker.dsl.PiekerDslException;

public class ValidationException extends PiekerDslException {

    public ValidationException(String message) {
        super(message);
    }
}
