package pieker.architectures.description;

/**
 * Exception thrown when an error occurs while parsing an architecture description file.
 */
public class InterfaceDescriptionException extends RuntimeException {

    public InterfaceDescriptionException(String message) {
        super(message);
    }

    public InterfaceDescriptionException(String message, Throwable cause) {
        super(message, cause);
    }

}
