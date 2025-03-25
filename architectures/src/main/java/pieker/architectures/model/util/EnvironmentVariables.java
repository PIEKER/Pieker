package pieker.architectures.model.util;

import java.util.Map;

/**
 * Interface for architecture model components that have environment variables.
 */
public interface EnvironmentVariables {

    void setEnvironment(Map<String, String> environment);

    Map<String, String> getEnvironment();

    /**
     * Returns the value of the environment variable with the given key.
     *
     * @param key key of the environment variable
     * @return value of the environment variable
     */
    String getEnvironmentValue(String key);

    /**
     * Updates the environment variables with the given updates.
     *
     * @param envUpdates map of environment variable updates
     * @return true if the environment variables were updated successfully, false otherwise
     */
    boolean updateEnvironment(Map<String, String> envUpdates);

}
