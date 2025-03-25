package pieker.architectures.model;

public interface ComponentLink extends Cloneable {

    /**
     * Checks if the link is valid.
     *
     * <p>A link is valid if the source and target components are not null and the type is set.
     *
     * @return true if link is valid, false otherwise
     */
    boolean isValid();

    /**
     * Enum representing the types of supported links.
     */
    enum LinkType {
        // Communication-specific links
        HTTP_API,
        // Storage-related links
        STORAGE,
        // Other
        UNSUPPORTED
    }

}
