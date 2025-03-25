package pieker.architectures.model.exceptions;

import pieker.architectures.ArchitectureException;

/**
 * Exception for errors in the architecture model.
 */
public class ArchitectureModelException extends ArchitectureException {

    public ArchitectureModelException(String message) {
        super(message);
    }

    public ArchitectureModelException(String message, Throwable cause) {
        super(message, cause);
    }

}
