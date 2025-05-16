package pieker.architectures.compose.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import pieker.architectures.common.model.JdbcLink;
import pieker.architectures.model.AbstractArchitectureModel;
import pieker.architectures.model.ArchitectureModel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Represents a Docker Compose architecture model.
 * <p>
 * TODO: Add configs, secrets
 */
@Slf4j
@Getter
@Setter
@NoArgsConstructor
public class ComposeArchitectureModel extends AbstractArchitectureModel<ComposeComponent> implements ArchitectureModel<ComposeComponent> {

    private String version;

    public ComposeArchitectureModel(String version) {
        this.version = version;
    }

    public List<ComposeService> getComposeServices() {
        return filterComponents(ComposeService.class);
    }

    public List<ComposeVolume> getComposeVolumes() {
        return filterComponents(ComposeVolume.class);
    }

    public List<ComposeNetwork> getComposeNetworks() {
        return filterComponents(ComposeNetwork.class);
    }

    /**
     * Filters the components of the Docker Compose architecture model by the given class.
     *
     * @param clazz class to filter by
     * @param <T>   type of the class
     * @return list of components of the given class
     */
    private <T extends ComposeComponent> List<T> filterComponents(Class<T> clazz) {
        return this.getComponents().stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .toList();
    }

    /**
     * Writes the architecture model to a YAML file at the given path.
     *
     * @param path path to the YAML file
     */
    public void toYamlFile(String path) {
        try {
            Files.writeString(Paths.get(path), this.toYaml());
        } catch (IOException e) {
            log.error("Failed to write Docker Compose YAML file", e);
        }
    }

    /**
     * Converts the architecture model to a YAML string.
     *
     * @return YAML string
     */
    public String toYaml() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);

        Yaml yaml = new Yaml(options);

        return yaml.dump(this.toMap());
    }

    /**
     * Converts the architecture model to a map.
     *
     * @return map of the architecture model and its components
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        if (this.version != null && !this.version.isBlank()) map.put("version", this.version);
        putIfNotEmpty(map, "services", this.getComposeServices());
        putIfNotEmpty(map, "volumes", this.getComposeVolumes());
        putIfNotEmpty(map, "networks", this.getComposeNetworks());
        return map;
    }

    /**
     * Puts the given components into the map if the list is not empty.
     *
     * @param map        map to put the components into
     * @param key        key of the map
     * @param components list of items
     */
    private <T extends ComposeComponent> void putIfNotEmpty(Map<String, Object> map, String key, List<T> components) {
        Map<String, Object> result = components.stream()
                .map(ComposeComponent::toMap)
                .reduce(new HashMap<>(), (a, b) -> {
                    a.putAll(b);
                    return a;
                });

        if (!result.isEmpty()) {
            map.put(key, result);
        }
    }

    @Override
    public ComposeService createComponent(String name) {
        return (ComposeService) super.createComponent(name);
    }

    @Override
    public void saveToFile(String filePath) {
        this.toYamlFile(filePath);
    }

    @Override
    protected ComposeService createComponentInstance(String name) {
        return new ComposeService(name);
    }

    public Optional<Map<String, String>> getSupervisorDatabaseInfo() {
        JdbcLink<ComposeComponent> databaseLink = getLinks().stream()
                .filter(link -> link instanceof JdbcLink)
                .map(link -> (JdbcLink<ComposeComponent>) link)
                .findFirst()
                .orElse(null);
        if (databaseLink == null) {
            return Optional.empty();
        }
        Map<String, String> databaseInfo = new HashMap<>();
        databaseInfo.put("DB_USER", databaseLink.getUsername());
        databaseInfo.put("DB_PASS", databaseLink.getPassword());
        databaseInfo.put("DB_HOST", databaseLink.getJdbcHost());
        databaseInfo.put("DB_PORT", databaseLink.getJdbcPort());

        return Optional.of(databaseInfo);
    }
}
