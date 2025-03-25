package pieker.architectures.parser.exceptions;

import pieker.architectures.ArchitectureException;

/**
 * Exception thrown when an error occurs while parsing an architecture file.
 */
public class ArchitectureParserException extends ArchitectureException {

    public ArchitectureParserException(String message) {
        super(message);
    }

    public ArchitectureParserException(String message, Throwable cause) {
        super(message, cause);
    }

}

