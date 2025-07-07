package pieker.architectures.compose.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pieker.architectures.model.Storage;

import java.util.HashMap;
import java.util.Map;

import static pieker.architectures.util.MapUtils.mapToList;
import static pieker.architectures.util.MapUtils.putIfNotNullOrEmpty;

/**
 * Represents a Docker Compose volume in the Docker Compose architecture model.
 */
@Getter
@Setter
public class ComposeVolume extends Storage implements ComposeComponent {

    private String driver;
    private Map<String, String> driverOpts;
    private boolean external;
    private Map<String, String> labels = new HashMap<>();
    private String name;

    @Builder
    public ComposeVolume(String name) {
        this.setComponentName(name);
        this.setType(StorageType.FILE_SYSTEM);
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> attributeMap = new HashMap<>();
        putIfNotNullOrEmpty(attributeMap, "driver", this.getDriver());
        putIfNotNullOrEmpty(attributeMap, "driver_opts", mapToList(this.getDriverOpts()));
        putIfNotNullOrEmpty(attributeMap, "external", this.isExternal());
        putIfNotNullOrEmpty(attributeMap, "labels", mapToList(this.getLabels()));
        putIfNotNullOrEmpty(attributeMap, "name", this.getName());
        return attributeMap;
    }
}
