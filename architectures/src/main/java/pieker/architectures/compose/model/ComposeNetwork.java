package pieker.architectures.compose.model;

import lombok.Getter;
import lombok.Setter;
import pieker.architectures.model.CompositeNetwork;
import pieker.architectures.model.Network;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a Docker Compose network in the Docker Compose architecture model.
 * TODO: Add full support
 */
@Getter
@Setter
public class ComposeNetwork extends Network implements ComposeComponent {

    private String driver;

    public ComposeNetwork(String name) {
        this.setName(name);
    }

    public ComposeNetwork(String name, String driver) {
        this.setName(name);
        this.driver = driver;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> attributeMap = new HashMap<>();

        attributeMap.put("driver", this.driver);

        return Map.of(this.getName(), attributeMap);
    }
}
