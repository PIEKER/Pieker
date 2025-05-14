package pieker.architectures.compose;

import lombok.NoArgsConstructor;
import pieker.architectures.common.model.HttpApiLink;
import pieker.architectures.common.model.JdbcLink;
import pieker.architectures.compose.model.ComposeArchitectureModel;
import pieker.architectures.compose.model.ComposeComponent;
import pieker.architectures.compose.model.ComposeService;
import pieker.architectures.injector.AbstractComponentInjector;
import pieker.architectures.injector.ComponentInjector;
import pieker.architectures.injector.exceptions.ComponentInjectionException;
import pieker.architectures.model.ComponentLink;
import pieker.architectures.model.Link;
import pieker.common.ScenarioComponent;
import pieker.common.ScenarioTestPlan;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Component injector for the Docker Compose architecture model.
 */
@NoArgsConstructor
public class ComposeComponentInjector extends AbstractComponentInjector<ComposeArchitectureModel, ComposeComponent>
        implements ComponentInjector<ComposeArchitectureModel, ComposeComponent> {

    public ComposeComponentInjector(ComposeArchitectureModel model) {
        this.setModel(model);
    }

    @Override
    public void injectComponents(ScenarioTestPlan testPlan) throws ComponentInjectionException {
        for (ScenarioComponent scenarioComponent : testPlan.getComponents()) {
            ComposeComponent targetComponent = this.model.getComponent(scenarioComponent.getTarget()).orElseThrow();

            ComposeService newComponent = this.model.createComponent(scenarioComponent.getName());

            List<Link<ComposeComponent>> linksToUpdate = new ArrayList<>();
            if (scenarioComponent.getSource() == null || scenarioComponent.getSource().isBlank()) {
                linksToUpdate.addAll(this.model.getLinksForTarget(targetComponent));
            } else {
                ComposeComponent sourceComponent = this.model.getComponent(scenarioComponent.getSource()).orElseThrow();
                linksToUpdate.addAll(this.model.getLinksForSourceAndTarget(sourceComponent, targetComponent));
            }
            linksToUpdate.removeIf(link -> link.getSourceComponent().isGenerated());

            if (linksToUpdate.isEmpty()) {
                // Prepend component to target and add new link
                prependComponent(newComponent, targetComponent);
                continue;
            }

            // Connect proxy to target
            switch (linksToUpdate.getFirst().getType()) {
                case HTTP_API -> connectHttpApiProxyToTarget(
                        newComponent,
                        (ComposeService) targetComponent,
                        (HttpApiLink<ComposeComponent>) linksToUpdate.getFirst()
                );
                case JDBC -> connectJdbcProxyToTarget(
                        newComponent,
                        (ComposeService) targetComponent,
                        (JdbcLink<ComposeComponent>) linksToUpdate.getFirst()
                );
                case TCP, UNSUPPORTED -> {/*TODO*/}
            }
            // Connect sources to proxy
            for (Link<ComposeComponent> link : linksToUpdate) {
                switch (link.getType()) {
                    case HTTP_API -> injectHttpApiProxy(newComponent, (HttpApiLink<ComposeComponent>) link);
                    case JDBC -> injectJdbcProxy(newComponent, (JdbcLink<ComposeComponent>) link);
                    case TCP, UNSUPPORTED -> {/*TODO*/}
                }
            }
        }

        // Add Supervisor Proxy Component
        ComposeService supervisorProxy = this.model.createComponent("PIEKER_PROXY_SUPERVISOR");
        supervisorProxy.setImage("supervisor-proxy:test");
        final String supervisorProxyPort = System.getProperty("supervisorPort", "42690");
        supervisorProxy.addPortMapping(supervisorProxyPort, supervisorProxyPort);

        if (!this.model.validate()) {
            throw new ComponentInjectionException("Model is invalid after injecting components. Check inputs.");
        }
    }

    @Override
    public <T extends ComposeComponent> void prependComponent(T proxy, ComposeComponent targetComponent) throws ComponentInjectionException {
        ComponentLink.LinkType interfaceType = targetComponent.getProvidedInterfaceType();
        if (interfaceType == null) {
            throw new ComponentInjectionException("Provided component type is null for target component: %s".formatted(targetComponent.getName()));
        }
        switch (interfaceType) {
            case HTTP_API -> {
                ComposeService targetService = (ComposeService) targetComponent;
                ((ComposeService) proxy).setImage(proxy.getName().toLowerCase() + ":" + System.getProperty("scenarioName", "latest").toLowerCase());
                ((ComposeService) proxy).setEnvironment(Map.of(
                        "SERVICE_BASE_URL", "http://" + targetComponent.getName() + ":" + targetService.getPorts().values().iterator().next()
                ));
                this.model.addLink(HttpApiLink.createForProxy(proxy, targetComponent));
            }
            case DATABASE, JDBC -> {
                // TODO: Handle different protocols for link-supertypes independently
                ComposeService targetService = (ComposeService) targetComponent;
                List<Link<ComposeComponent>> links = this.model.getLinksForTarget(targetComponent);
                if (links.isEmpty()) {
                    throw new ComponentInjectionException("No links found for target component: %s".formatted(targetComponent.getName()));
                }
                JdbcLink<ComposeComponent> existingLink = (JdbcLink<ComposeComponent>) links.getFirst();
                ((ComposeService) proxy).setImage(proxy.getName().toLowerCase() + ":" + System.getProperty("scenarioName", "latest").toLowerCase());
                ((ComposeService) proxy).setEnvironment(Map.of(
                        "JDBC_PROXY_TARGET", existingLink.getJdbcHost() + ":" + existingLink.getJdbcPort()
                ));
                this.model.addLink(JdbcLink.createForProxy(proxy, targetComponent));
            }
            case TCP, UNSUPPORTED -> {/*TODO*/}
        }
    }

    /**
     * Connects the new HTTP proxy component to the existing target component.
     *
     * @param proxyComponent  Proxy component
     * @param targetComponent Target component
     * @param existingLink    Arbitrary existing link to the target component that shall be proxied
     */
    private void connectHttpApiProxyToTarget(ComposeService proxyComponent, ComposeService targetComponent,
                                             HttpApiLink<ComposeComponent> existingLink) {
        proxyComponent.setImage(proxyComponent.getName().toLowerCase() + ":" + System.getProperty("scenarioName", "latest").toLowerCase());
        HttpApiLink<ComposeComponent> proxyToTargetLink = HttpApiLink.createForProxy(proxyComponent, targetComponent);
        this.model.addLink(proxyToTargetLink);
        final String sourcePortVarValue = ((ComposeService) existingLink.getSourceComponent()).getEnvironmentValue(existingLink.getPortVarName());
        final String targetUrlValue = ((ComposeService) existingLink.getSourceComponent()).getEnvironmentValue(existingLink.getUrlVarName());

        if (existingLink.getPortVarName() != null && sourcePortVarValue != null) {
            proxyComponent.updateEnvironment(Map.of(proxyToTargetLink.getPortVarName(), sourcePortVarValue));
        }
        if (existingLink.getHostVarName() != null && targetComponent.getName() != null) {
            proxyComponent.updateEnvironment(Map.of(proxyToTargetLink.getHostVarName(), targetComponent.getName()));
        }
        if (existingLink.getUrlVarName() != null && targetUrlValue != null) {
            proxyComponent.updateEnvironment(Map.of(proxyToTargetLink.getUrlVarName(), targetUrlValue));
        }
    }

    /**
     * Connects the new JDBC proxy component to the existing target component.
     *
     * @param proxyComponent  Proxy component
     * @param targetComponent Target component
     * @param existingLink    Arbitrary existing link to the target component that shall be proxied
     */
    private void connectJdbcProxyToTarget(ComposeService proxyComponent, ComposeService targetComponent,
                                          JdbcLink<ComposeComponent> existingLink) {
        proxyComponent.setImage(proxyComponent.getName().toLowerCase() + ":" + System.getProperty("scenarioName", "latest").toLowerCase());
        JdbcLink<ComposeComponent> proxyToTargetLink = JdbcLink.createForProxy(proxyComponent, targetComponent);
        proxyToTargetLink.setJdbcUrl(existingLink.getJdbcUrl());
        this.model.addLink(proxyToTargetLink);
        final String targetUrlValue = ((ComposeService) existingLink.getSourceComponent()).getEnvironmentValue(existingLink.getUrlVarName());

        if (existingLink.getUrlVarName() != null && targetUrlValue != null) {
            proxyComponent.updateEnvironment(Map.of(proxyToTargetLink.getUrlVarName(), targetUrlValue));
        }
    }

    /**
     * Injects a proxy component into an existing HTTP API link.
     *
     * @param proxyComponent Proxy component
     * @param existingLink   Existing HTTP API link
     */
    private void injectHttpApiProxy(ComposeService proxyComponent, HttpApiLink<ComposeComponent> existingLink) {
        ComposeService sourceComponent = (ComposeService) existingLink.getSourceComponent();
        if (existingLink.getHostVarName() != null && existingLink.getPortVarName() != null) {
            sourceComponent.updateEnvironment(Map.of(existingLink.getHostVarName(), proxyComponent.getName()));
        }
        if (existingLink.getPortVarName() != null) {
            sourceComponent.updateEnvironment(Map.of(existingLink.getPortVarName(), "80"));
        }
        if (existingLink.getUrlVarName() != null) {
            sourceComponent.updateEnvironment(Map.of(existingLink.getUrlVarName(), "http://" + proxyComponent.getName() + ":80"));
        }
        existingLink.setTargetComponent(proxyComponent);
    }

    /**
     * Injects a proxy component into an existing JDBC link.
     *
     * @param proxyComponent Proxy component
     * @param existingLink   Existing JDBC link
     */
    private void injectJdbcProxy(ComposeService proxyComponent, JdbcLink<ComposeComponent> existingLink) {
        ComposeService sourceComponent = (ComposeService) existingLink.getSourceComponent();
        if (existingLink.getUrlVarName() != null) {
            sourceComponent.updateEnvironment(Map.of(
                    existingLink.getUrlVarName(), updateJdbcUrl(existingLink.getJdbcUrl(), existingLink.getJdbcHost(), existingLink.getJdbcPort(), proxyComponent.getName())
            ));
        }
        existingLink.setTargetComponent(proxyComponent);
    }

    /**
     * Updates the JDBC URL to point to the new host.
     *
     * @param jdbcUrl The original JDBC URL
     * @param oldHost The old host
     * @param oldPort The old port
     * @param newHost The new host
     * @return The updated JDBC URL
     */
    private String updateJdbcUrl(String jdbcUrl, String oldHost, String oldPort, String newHost) {
        return updateJdbcUrl(jdbcUrl, oldHost, oldPort, newHost, oldPort);
    }

    /**
     * Updates the JDBC URL to point to the new host and port.
     *
     * @param jdbcUrl The original JDBC URL
     * @param oldHost The old host
     * @param oldPort The old port
     * @param newHost The new host
     * @param newPort The new port
     * @return The updated JDBC URL
     */
    private String updateJdbcUrl(String jdbcUrl, String oldHost, String oldPort, String newHost, String newPort) {
        if (oldPort == null || oldPort.isBlank()) {
            oldPort = "";
        } else {
            oldPort = ":" + oldPort;
        }
        if (newPort == null || newPort.isBlank()) {
            newPort = "";
        } else {
            newPort = ":" + newPort;
        }
        return jdbcUrl.replace(oldHost + oldPort, newHost + newPort);
    }

}
