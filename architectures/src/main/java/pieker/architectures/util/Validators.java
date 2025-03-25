package pieker.architectures.util;

import java.net.URI;
import java.util.List;

/**
 * Utility class for validation methods.
 */
public final class Validators {

    private Validators() {}

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static boolean isValidURL(String url) {
        try {
            new URI(url).toURL();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean allValidURLs(String host, int port, List<String> endpoints) {
        final String actualPort = port > 0 ? String.valueOf(port) : "";
        return endpoints.stream()
                .map(e -> ("%s:%s/%s").formatted(host, actualPort, e))
                .allMatch(Validators::isValidURL);
    }
}
