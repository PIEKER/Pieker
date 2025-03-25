package pieker.architectures.injector.exceptions;

import pieker.architectures.ArchitectureException;

/**
 * Exception thrown when an error occurs during component injection.
 */
public class ComponentInjectionException extends ArchitectureException {

    public ComponentInjectionException(String message) {
        super(message);
    }

    public ComponentInjectionException(String message, Throwable cause) {
        super(message, cause);
    }

}
