package pieker.architectures.parser.exceptions;

import pieker.architectures.ArchitectureException;

public class UnsupportedFileTypeException extends ArchitectureException {

    public UnsupportedFileTypeException(String message) {
        super(message);
    }

    public UnsupportedFileTypeException(String message, Throwable cause) {
        super(message, cause);
    }

}
