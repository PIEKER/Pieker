package pieker.architectures;

/**
 * Enum representing the supported file types for architecture files.
 */
public enum SupportedFileType {

    DOCKER_COMPOSE,
    UNSUPPORTED;

    /**
     * Returns the SupportedFileType that matches the given file name or UNSUPPORTED if not found.
     *
     * @param fileName file name to match
     * @return the matching SupportedFileType, or UNSUPPORTED if not found
     */
    public static SupportedFileType fromFileName(String fileName) {
        return switch (fileName) {
            case "docker-compose.yml", "docker-compose.yaml" -> DOCKER_COMPOSE;
            default -> UNSUPPORTED;
        };
    }
}
