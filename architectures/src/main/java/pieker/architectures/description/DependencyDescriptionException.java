package pieker.architectures.description;

/**
 * Exception thrown when an error occurs while parsing an architecture description file.
 */
public class DependencyDescriptionException extends RuntimeException {

    public DependencyDescriptionException(String message) {
        super(message);
    }

    public DependencyDescriptionException(String message, Throwable cause) {
        super(message, cause);
    }

}
