package pieker.architectures.model;

import lombok.Getter;
import lombok.Setter;
import pieker.architectures.model.util.EnvironmentVariables;

import java.util.HashMap;
import java.util.Map;

/**
 * Class representing a service in the architecture model.
 */
@Getter
@Setter
public class Service extends AbstractComponent implements Component, EnvironmentVariables {

    /// Container Image
    private String image;
    /// Map of environment variables to values
    private Map<String, String> environment = new HashMap<>();

    @Override
    public String getEnvironmentValue(String key) {
        return this.environment.get(key);
    }

    @Override
    public boolean updateEnvironment(Map<String, String> envUpdates) {
        try {
            this.environment.putAll(envUpdates);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
