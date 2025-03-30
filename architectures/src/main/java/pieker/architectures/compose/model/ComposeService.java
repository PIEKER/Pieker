package pieker.architectures.compose.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pieker.architectures.model.Service;
import pieker.architectures.model.util.EnvironmentVariables;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pieker.architectures.util.MapUtils.putIfNotNullOrEmpty;

/**
 * Represents a Docker Compose service in the Docker Compose architecture model.
 * <p>
 * TODO: Expand support
 */
@Getter
@Setter
@AllArgsConstructor
public class ComposeService extends Service implements ComposeComponent, EnvironmentVariables {

    private List<String> dependsOn;
    private Map<String, String> ports = new HashMap<>();
    private String build;
    private List<String> networks;
    private List<String> configs;
    private List<String> secrets;

    public ComposeService(String name) {
        this.setName(name);
    }

    public ComposeService(String name, String image, Map<String, String> environment) {
        this.setName(name);
        this.setImage(image);
        this.setEnvironment(environment);
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> attributeMap = new HashMap<>();

        putIfNotNullOrEmpty(attributeMap, "image", this.getImage());
        putIfNotNullOrEmpty(attributeMap, "depends_on", this.dependsOn);
        putIfNotNullOrEmpty(attributeMap, "ports", this.ports.entrySet().stream()
                .map(e -> e.getKey() + ":" + e.getValue())
                .toList());
        putIfNotNullOrEmpty(attributeMap, "build", this.build);
        putIfNotNullOrEmpty(attributeMap, "networks", this.networks);
        putIfNotNullOrEmpty(attributeMap, "configs", this.configs);
        putIfNotNullOrEmpty(attributeMap, "secrets", this.secrets);
        putIfNotNullOrEmpty(attributeMap, "environment", this.getEnvironment());

        return Map.of(this.getName(), attributeMap);
    }

    public void addPortMapping(String port, String targetPort) {
        this.ports.put(port, targetPort);
    }

    public void addPortMappings(Map<String, String> mappings) {
        this.ports.putAll(mappings);
    }
}
