package pieker.architectures.compose.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pieker.architectures.model.CompositeStorage;
import pieker.architectures.model.Storage;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a Docker Compose volume in the Docker Compose architecture model.
 * <p>
 * TODO: Add full support
 */
@Getter
@Setter
public class ComposeVolume extends Storage implements ComposeComponent {

    @Builder
    public ComposeVolume(String name) {
        this.setComponentName(name);
        this.setType(StorageType.FILE_SYSTEM);
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> attributeMap = new HashMap<>();

        return Map.of(this.getName(), attributeMap);
    }
}
