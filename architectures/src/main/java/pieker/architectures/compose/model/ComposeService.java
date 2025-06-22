package pieker.architectures.compose.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pieker.architectures.model.CompositeService;
import pieker.architectures.model.Service;
import pieker.architectures.model.util.EnvironmentVariables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pieker.architectures.util.MapUtils.mapToList;
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

    /* ---- Relationships ---- */
    private List<String> dependsOn;
    private Map<String, String> ports = new HashMap<>();
    private Map<String, String> volumes = new HashMap<>(); // TODO: Implement as ComposeVolume

    /* ---- Core Runtime ---- */
    private String image;
    private String build;
    private String command;
    private String entrypoint;
    private String workingDir;
    private String restart;
    private String user;
    private Boolean tty;
    private Boolean stdinOpen;
    private Boolean privileged;

    /* ---- Environment ---- */
    private Map<String, String> environment = new HashMap<>();

    /* ---- Networking ---- */
    private List<String> networks = new ArrayList<>(); // TODO: Implement as ComposeNetwork

    /* ---- Misc ---- */
    private String containerName;


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
        putIfNotNullOrEmpty(attributeMap, "build", this.getBuild());
        putIfNotNullOrEmpty(attributeMap, "command", this.getCommand());
        putIfNotNullOrEmpty(attributeMap, "entrypoint", this.getEntrypoint());
        putIfNotNullOrEmpty(attributeMap, "container_name", this.getContainerName());
        putIfNotNullOrEmpty(attributeMap, "working_dir", this.getWorkingDir());
        putIfNotNullOrEmpty(attributeMap, "restart", this.getRestart());
        putIfNotNullOrEmpty(attributeMap, "user", this.getUser());
        putIfNotNullOrEmpty(attributeMap, "tty", this.getTty());
        putIfNotNullOrEmpty(attributeMap, "stdin_open", this.getStdinOpen());
        putIfNotNullOrEmpty(attributeMap, "privileged", this.getPrivileged());

        putIfNotNullOrEmpty(attributeMap, "depends_on", this.dependsOn);
        putIfNotNullOrEmpty(attributeMap, "ports", mapToList(this.ports));
        putIfNotNullOrEmpty(attributeMap, "volumes", mapToList(this.volumes));
        putIfNotNullOrEmpty(attributeMap, "networks", this.networks);
        putIfNotNullOrEmpty(attributeMap, "environment", this.getEnvironment());

        return Map.of(this.getName(), attributeMap);
    }

    public void addPortMapping(String port, String targetPort) {
        this.ports.put(port, targetPort);
    }

    public void addPortMappings(Map<String, String> mappings) {
        this.ports.putAll(mappings);
    }

    public void addVolume(String localDir, String containerDir) {
        this.volumes.put(localDir, containerDir);
    }
}
