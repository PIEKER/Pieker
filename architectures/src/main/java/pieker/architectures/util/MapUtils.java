package pieker.architectures.util;

import java.util.Collection;
import java.util.Map;

/**
 * Utility class for working with maps.
 */
public final class MapUtils {

    private MapUtils() {}

    /**
     * Puts the value into the map if it is not null or empty.
     *
     * <p>Supported value types:
     * <li> {@link Collection}
     * <li> {@link Map}
     * <li> {@link String}
     *
     * @param map   map to put the value into
     * @param key   key to use
     * @param value value to put
     * @param <K>   type of keys maintained by the map
     * @param <V>   type of mapped values
     */
    public static <K, V> void putIfNotNullOrEmpty(Map<K, V> map, K key, V value) {
        if (value != null) {
            switch (value) {
                case Collection<?> c when c.isEmpty() -> {
                    return;
                }
                case Map<?, ?> m when m.isEmpty() -> {
                    return;
                }
                case String s when s.isEmpty() -> {
                    return;
                }
                default -> {
                }
            }

            map.put(key, value);
        }
    }

}
