package pieker.architectures.generator.exceptions;

import pieker.architectures.ArchitectureException;

/**
 * Exception thrown when an error occurs during the generation of an architecture model.
 */
public class ArchitectureGeneratorException extends ArchitectureException {

    public ArchitectureGeneratorException(String message) {
        super(message);
    }

    public ArchitectureGeneratorException(String message, Throwable cause) {
        super(message, cause);
    }

}
