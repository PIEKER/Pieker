package pieker.architectures.compose;

import lombok.extern.slf4j.Slf4j;
import pieker.architectures.common.model.HttpApiLink;
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
                if (descriptionComponent.getDependencies() != null && !descriptionComponent.getDependencies().isEmpty()) {
                    // ... and for each link in the Interface Description of this component ...
                    for (DescriptionLink descriptionLink : descriptionComponent.getDependencies()) {
                        // ... find the corresponding target component in the ComposeArchitectureModel ...
                        model.getComponent(descriptionLink.getTarget()).ifPresent(target -> {
                            // ... and create a new link between the source and target components of the corresponding type
                            switch (descriptionLink) {
                                case DescriptionLink.HttpApiLink apiDesc:
                                    HttpApiLink<ComposeComponent> httpApiLink = new HttpApiLink<>(source, target);
                                    putIfNotNullOrEmpty(httpApiLink.sourceRelatedEnvironmentVariables, "URL_VAR", apiDesc.getTargetUrlEnv());
                                    putIfNotNullOrEmpty(httpApiLink.sourceRelatedEnvironmentVariables, "HOST_VAR", apiDesc.getTargetHostEnv());
                                    putIfNotNullOrEmpty(httpApiLink.sourceRelatedEnvironmentVariables, "PORT_VAR", apiDesc.getTargetPortEnv());
                                    links.add(httpApiLink);
                                    break;
                                case DescriptionLink.JdbcLink jdbcDesc:
                                    ComposeService sourceService = (ComposeService) source;
                                    JdbcLink<ComposeComponent> jdbcLink = new JdbcLink<>(source, target);
                                    putIfNotNullOrEmpty(jdbcLink.sourceRelatedEnvironmentVariables, "URL_VAR", jdbcDesc.getTargetUrlEnv());
                                    jdbcLink.setJdbcUrl(sourceService.getEnvironmentValue(jdbcDesc.getTargetUrlEnv()));
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
        // TODO: Expand support for storages, networks, etc.
        this.constructStorages((Map<String, Object>) data.getOrDefault("volumes", Map.of()));
        this.modelNetworks = new ArrayList<>();
        // ...

    }

    @SuppressWarnings("unchecked")
    private void constructServices(Map<String, Object> data) {
        this.modelServices = new HashMap<>();
        data.forEach((serviceName, rawServiceData) -> {
            Map<String, Object> serviceData = (Map<String, Object>) rawServiceData;

            ComposeService service = new ComposeService(serviceName);
            service.setImage((String) serviceData.get("image"));
            service.setBuild((String) serviceData.get("build"));
            service.setEnvironment((Map<String, String>) serviceData.getOrDefault("environment", new HashMap<>()));
            service.setDependsOn((List<String>) serviceData.getOrDefault("depends_on", new ArrayList<>()));
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
        data.forEach((storageName, rawStorageData) -> {
            Map<String, Object> storageData = (Map<String, Object>) rawStorageData;

            // TODO: Implement storage construction
        });
    }

}
