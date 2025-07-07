package pieker.architectures.compose.model;

import lombok.Getter;
import lombok.Setter;
import pieker.architectures.model.Network;

import java.util.HashMap;
import java.util.Map;

import static pieker.architectures.util.MapUtils.putIfNotNullOrEmpty;

/**
 * Represents a Docker Compose network in the Docker Compose architecture model.
 */
@Getter
@Setter
public class ComposeNetwork extends Network implements ComposeComponent {

    private String driver;
    private Object driverOpts = new HashMap<>();
    private boolean attachable;
    private boolean enableIpv4;
    private boolean enableIpv6;
    private boolean external;
    private Object ipam;
    private boolean internal;
    private Object labels;
    private String name;


    public ComposeNetwork(String name) {
        this.setComponentName(name);
    }

    public ComposeNetwork(String name, String driver) {
        this.setComponentName(name);
        this.driver = driver;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> attributeMap = new HashMap<>();
        putIfNotNullOrEmpty(attributeMap, "driver", this.getDriver());
        putIfNotNullOrEmpty(attributeMap, "driver_opts", this.getDriverOpts());
        putIfNotNullOrEmpty(attributeMap, "attachable", this.isAttachable());
        putIfNotNullOrEmpty(attributeMap, "enable_ipv4", this.isEnableIpv4());
        putIfNotNullOrEmpty(attributeMap, "enable_ipv6", this.isEnableIpv6());
        putIfNotNullOrEmpty(attributeMap, "external", this.isExternal());
        putIfNotNullOrEmpty(attributeMap, "ipam", this.getIpam());
        putIfNotNullOrEmpty(attributeMap, "internal", this.isInternal());
        putIfNotNullOrEmpty(attributeMap, "labels", this.getLabels());
        putIfNotNullOrEmpty(attributeMap, "name", this.getName());
        return attributeMap;
    }
}
