package pieker.architectures;

/**
 * Exception that is thrown when an error occurs which is related to the PIEKER Architectures Module.
 */
public class ArchitectureException extends RuntimeException {

    public ArchitectureException(String message) {
        super(message);
    }

    public ArchitectureException(String message, Throwable cause) {
        super(message, cause);
    }

}
