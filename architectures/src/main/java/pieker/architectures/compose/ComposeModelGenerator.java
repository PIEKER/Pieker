package pieker.architectures.compose;

import lombok.extern.slf4j.Slf4j;
import pieker.architectures.common.model.HttpLink;
import pieker.architectures.common.model.JdbcLink;
import pieker.architectures.compose.model.*;
import pieker.architectures.description.InterfaceDescription;
import pieker.architectures.description.DescriptionComponent;
import pieker.architectures.description.DescriptionLink;
import pieker.architectures.generator.AbstractModelGenerator;
import pieker.architectures.generator.exceptions.ArchitectureGeneratorException;
import pieker.architectures.model.Link;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pieker.architectures.util.MapUtils.putIfNotNullOrEmpty;

/**
 * Class for generating Docker Compose architecture models from parsed data.
 */
@Slf4j
public class ComposeModelGenerator extends AbstractModelGenerator<ComposeArchitectureModel, ComposeComponent> {

    private Map<String, ComposeService> modelServices;
    private Map<String, ComposeVolume> modelStorages;
    private List<ComposeNetwork> modelNetworks;
    private ComposeArchitectureModel model;

    @Override
    protected <T> ComposeArchitectureModel constructModel(T parsedData) {
        log.info("Constructing ComposeArchitectureModel...");
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) parsedData;

            this.constructModelEntities(data);

            ComposeArchitectureModel model = new ComposeArchitectureModel();
            model.setVersion((String) data.get("version"));
            model.addComponents(this.modelServices.values());
            model.addComponents(this.modelStorages.values());
            model.addComponents(this.modelNetworks);
            this.model = model;

            log.info("Successfully constructed ComposeArchitectureModel");
            return model;
        } catch (ClassCastException e) {
            final String errorMessage = "Error while constructing ComposeArchitectureModel: %s".formatted(e.getMessage());
            log.error(errorMessage);
            throw new ArchitectureGeneratorException(errorMessage);
        }
    }

    @Override
    protected List<Link<ComposeComponent>> constructLinks(InterfaceDescription description) {
        final ArrayList<Link<ComposeComponent>> links = new ArrayList<>();
        // For each component in the PIEKER Interface Description ...
        for (DescriptionComponent descriptionComponent : description.getComponents()) {
            // ... find the corresponding component in the ComposeArchitectureModel ...
            model.getComponent(descriptionComponent.getName()).ifPresent(source -> {
                if (descriptionComponent.getRequires() != null && !descriptionComponent.getRequires().isEmpty()) {
                    // ... and for each link in the Interface Description of this component ...
                    for (DescriptionLink descriptionLink : descriptionComponent.getRequires()) {
                        // ... find the corresponding target component in the ComposeArchitectureModel ...
                        model.getComponent(descriptionLink.getTarget()).ifPresent(target -> {
                            // ... and create a new link between the source and target components of the corresponding type
                            switch (descriptionLink) {
                                case DescriptionLink.HttpLink httpDesc:
                                    HttpLink<ComposeComponent> httpLink = new HttpLink<>(source, target);
                                    putIfNotNullOrEmpty(httpLink.sourceRelatedEnvironmentVariables, "URL_VAR", httpDesc.getTargetUrlEnv());
                                    putIfNotNullOrEmpty(httpLink.sourceRelatedEnvironmentVariables, "HOST_VAR", httpDesc.getTargetHostEnv());
                                    putIfNotNullOrEmpty(httpLink.sourceRelatedEnvironmentVariables, "PORT_VAR", httpDesc.getTargetPortEnv());
                                    links.add(httpLink);
                                    break;
                                case DescriptionLink.JdbcLink jdbcDesc:
                                    ComposeService sourceService = (ComposeService) source;
                                    JdbcLink<ComposeComponent> jdbcLink = new JdbcLink<>(source, target);
                                    putIfNotNullOrEmpty(jdbcLink.sourceRelatedEnvironmentVariables, "URL_VAR", jdbcDesc.getTargetUrlEnv());
                                    putIfNotNullOrEmpty(jdbcLink.sourceRelatedEnvironmentVariables, "USER_VAR", jdbcDesc.getUsernameEnv());
                                    putIfNotNullOrEmpty(jdbcLink.sourceRelatedEnvironmentVariables, "PASS_VAR", jdbcDesc.getPasswordEnv());
                                    jdbcLink.setJdbcUrl(sourceService.getEnvironmentValue(jdbcDesc.getTargetUrlEnv()));
                                    jdbcLink.setUsername(sourceService.getEnvironmentValue(jdbcDesc.getUsernameEnv()));
                                    jdbcLink.setPassword(sourceService.getEnvironmentValue(jdbcDesc.getPasswordEnv()));
                                    links.add(jdbcLink);
                                    break;
                                default:
                                    log.warn("Unsupported link type will be ignored: {}", descriptionLink.getClass().getSimpleName());
                            }
                        });
                    }
                }
            });
        }
        return links;
    }

    @SuppressWarnings("unchecked")
    private void constructModelEntities(Map<String, Object> data) {
        this.constructServices((Map<String, Object>) data.getOrDefault("services", Map.of()));
        this.constructStorages((Map<String, Object>) data.getOrDefault("volumes", Map.of()));
        this.constructNetworks((Map<String, Object>) data.getOrDefault("networks", Map.of()));
        // TODO: Expand support for other entities like Configs or Secrets
        // ...

    }

    @SuppressWarnings("unchecked")
    private void constructServices(Map<String, Object> data) {
        this.modelServices = new HashMap<>();
        data.forEach((serviceName, rawServiceData) -> {
            Map<String, Object> serviceData = (Map<String, Object>) rawServiceData;
            ComposeService service = new ComposeService(serviceName);

            service.setImage((String) serviceData.getOrDefault("image", null));
            service.setBuild((String) serviceData.getOrDefault("build", null));
            service.setEnvironment((Map<String, String>) serviceData.getOrDefault("environment", new HashMap<>()));
            service.setDependsOn((List<String>) serviceData.getOrDefault("depends_on", new ArrayList<>()));

            service.setCommand((String) serviceData.getOrDefault("command", null));
            service.setEntrypoint((String) serviceData.getOrDefault("entrypoint", null));
            service.setContainerName((String) serviceData.getOrDefault("container_name", null));
            service.setWorkingDir((String) serviceData.getOrDefault("working_dir", null));
            service.setRestart((String) serviceData.getOrDefault("restart", null));
            service.setUser((String) serviceData.getOrDefault("user", null));
            service.setTty((Boolean) serviceData.getOrDefault("tty", null));
            service.setStdinOpen((Boolean) serviceData.getOrDefault("stdin_open", null));
            service.setPrivileged((Boolean) serviceData.getOrDefault("privileged", null));

            try {
                service.setVolumes((List<String>) serviceData.getOrDefault("volumes", new ArrayList<>()));
            } catch (Exception e) {
                log.warn("One or more volumes for service '{}' are not in short syntax, ignoring all.", serviceName);
            }

            HashMap<String, String> ports = new HashMap<>();
            ((ArrayList<String>) serviceData.getOrDefault("ports", List.of())).forEach(port -> {
                String[] portMapping = port.split(":");
                ports.put(portMapping[0], portMapping[1]);
            });
            service.setPorts(ports);

            this.modelServices.put(serviceName, service);
        });
    }

    @SuppressWarnings("unchecked")
    private void constructStorages(Map<String, Object> data) {
        this.modelStorages = new HashMap<>();
        data.forEach((volumeName, rawStorageData) -> {
            Map<String, Object> volumeData = (Map<String, Object>) rawStorageData;
            ComposeVolume volume = new ComposeVolume(volumeName);

            volume.setDriver((String) volumeData.getOrDefault("driver", null));
            volume.setDriverOpts((Map<String, String>) volumeData.getOrDefault("driver_opts", new HashMap<>()));
            volume.setExternal((Boolean) volumeData.getOrDefault("external", false));
            volume.setLabels((Map<String, String>) volumeData.getOrDefault("labels", new HashMap<>()));
            volume.setName((String) volumeData.getOrDefault("name", null));

            this.modelStorages.put(volumeName, volume);
        });
    }

    @SuppressWarnings("unchecked")
    private void constructNetworks(Map<String, Object> data) {
        this.modelNetworks = new ArrayList<>();
        data.forEach((networkName, rawNetworkData) -> {
            Map<String, Object> networkData = (Map<String, Object>) rawNetworkData;
            ComposeNetwork network = new ComposeNetwork(networkName);

            network.setDriver((String) networkData.getOrDefault("driver", null));
            network.setDriverOpts(networkData.getOrDefault("driver_opts", new HashMap<>()));
            network.setAttachable((Boolean) networkData.getOrDefault("attachable", false));
            network.setEnableIpv4((Boolean) networkData.getOrDefault("enable_ipv4", false));
            network.setEnableIpv6((Boolean) networkData.getOrDefault("enable_ipv6", false));
            network.setExternal((Boolean) networkData.getOrDefault("external", false));
            network.setIpam(networkData.getOrDefault("ipam", new HashMap<>()));
            network.setInternal((Boolean) networkData.getOrDefault("internal", false));
            network.setLabels(networkData.getOrDefault("labels", new HashMap<>()));
            network.setName((String) networkData.getOrDefault("name", null));

            this.modelNetworks.add(network);
        });
    }

}
