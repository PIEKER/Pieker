package pieker.architectures.compose;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pieker.architectures.common.model.HttpLink;
import pieker.architectures.common.model.JdbcLink;
import pieker.architectures.compose.model.ComposeArchitectureModel;
import pieker.architectures.compose.model.ComposeComponent;
import pieker.architectures.compose.model.ComposeService;
import pieker.architectures.injector.AbstractComponentInjector;
import pieker.architectures.injector.ComponentInjector;
import pieker.architectures.injector.exceptions.ComponentInjectionException;
import pieker.architectures.model.Link;
import pieker.common.ScenarioComponent;
import pieker.common.ScenarioTestPlan;
import pieker.common.ScenarioTrafficComponent;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Component injector for the Docker Compose architecture model.
 */
@Slf4j
@NoArgsConstructor
public class ComposeComponentInjector extends AbstractComponentInjector<ComposeArchitectureModel, ComposeComponent>
        implements ComponentInjector<ComposeArchitectureModel, ComposeComponent> {

    private static final String EXECUTION_NAME = System.getProperty("scenarioName");
    private static final String PROJECT_ROOT = System.getProperty("projectRoot");
    private static final String GEN_DIR = PROJECT_ROOT + System.getProperty("genDir", "../.gen/");
    private static final String LOG_DIR = GEN_DIR + EXECUTION_NAME + File.separator + "logs" + File.separator;

    public ComposeComponentInjector(ComposeArchitectureModel model) {
        this.setModel(model);
    }

    @Override
    public void injectComponents(ScenarioTestPlan testPlan) throws ComponentInjectionException {

        // Add Orchestrator Proxy Component
        ComposeService orchestratorGateway = this.model.createComponent("PIEKER_GATEWAY");
        orchestratorGateway.setImage("pieker-gateway:test");
        final String orchestratorPort = System.getProperty("orchestratorPort", "42690");
        orchestratorGateway.addPortMapping(orchestratorPort, orchestratorPort);
        orchestratorGateway.updateEnvironment(this.model.getOrchestratorDatabaseInfo().orElse(Map.of()));

        // Add Test Components
        for (ScenarioComponent scenarioComponent : testPlan.getComponents()) {
            ComposeComponent targetComponent = this.model.getComponent(scenarioComponent.getTarget()).orElseThrow(()
                    -> new NoSuchElementException("Target component not found: '%s' Please check component names in the DSL file!".formatted(scenarioComponent.getTarget())));

            ComposeService newComponent = this.model.createComponent(scenarioComponent.getName());
            newComponent.addVolume(LOG_DIR, "/tmp/logs");

            List<Link<ComposeComponent>> linksToUpdate = new ArrayList<>();
            if (scenarioComponent.getSource() == null || scenarioComponent.getSource().isBlank()) {
                linksToUpdate.addAll(this.model.getLinksForTarget(targetComponent));
            } else {
                ComposeComponent sourceComponent = this.model.getComponent(scenarioComponent.getSource()).orElseThrow();
                linksToUpdate.addAll(this.model.getLinksForSourceAndTarget(sourceComponent, targetComponent));
            }
            linksToUpdate.removeIf(link -> link.getSourceComponent().isGenerated());

            if (linksToUpdate.isEmpty() || scenarioComponent instanceof ScenarioTrafficComponent) {
                // Prepend component to target and add new link
                prependComponent(newComponent, targetComponent);
                continue;
            }

            // Connect proxy to target
            switch (linksToUpdate.getFirst().getType()) {
                case HTTP -> connectHttpApiProxyToTarget(
                        newComponent,
                        (ComposeService) targetComponent,
                        (HttpLink<ComposeComponent>) linksToUpdate.getFirst()
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
                    case HTTP -> injectHttpApiProxy(newComponent, (HttpLink<ComposeComponent>) link);
                    case JDBC -> injectJdbcProxy(newComponent, (JdbcLink<ComposeComponent>) link);
                    case TCP, UNSUPPORTED -> {/*TODO*/}
                }
            }
        }

        if (!this.model.validate()) {
            throw new ComponentInjectionException("Model is invalid after injecting components. Check inputs.");
        }
    }

    @Override
    public <T extends ComposeComponent> void prependComponent(T proxy, ComposeComponent targetComponent) throws ComponentInjectionException {
        Link.LinkType interfaceType = targetComponent.getProvidedInterfaceType();
        if (interfaceType == null) {
            throw new ComponentInjectionException("Provided component type is null for target component: %s".formatted(targetComponent.getName()));
        }
        switch (interfaceType) {
            case HTTP -> {
                ComposeService targetService = (ComposeService) targetComponent;
                ((ComposeService) proxy).setImage(proxy.getName().toLowerCase() + ":" + System.getProperty("scenarioName", "latest").toLowerCase());
                ((ComposeService) proxy).setEnvironment(Map.of(
                        "SERVICE_BASE_URL", "http://" + targetComponent.getName() + ":" + targetService.getPorts().values().iterator().next()
                ));
                this.model.addLink(HttpLink.createForProxy(proxy, targetComponent));
            }
            case DATABASE, JDBC -> {
                // TODO: Handle different protocols for link-supertypes independently
                List<Link<ComposeComponent>> links = this.model.getLinksForTarget(targetComponent);
                if (links.isEmpty()) {
                    throw new ComponentInjectionException("No links found for target component: %s".formatted(targetComponent.getName()));
                }
                JdbcLink<ComposeComponent> existingLink = (JdbcLink<ComposeComponent>) links.getFirst();
                ((ComposeService) proxy).setImage(proxy.getName().toLowerCase() + ":" + System.getProperty("scenarioName", "latest").toLowerCase());
                ((ComposeService) proxy).setEnvironment(Map.of(
                        "DB_URL", getJdbcBaseUrl(existingLink.getJdbcUrl()),
                        "DB_USER", existingLink.getUsername(),
                        "DB_PASS", existingLink.getPassword()
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
                                             HttpLink<ComposeComponent> existingLink) {
        proxyComponent.setImage(proxyComponent.getName().toLowerCase() + ":" + System.getProperty("scenarioName", "latest").toLowerCase());
        HttpLink<ComposeComponent> proxyToTargetLink = HttpLink.createForProxy(proxyComponent, targetComponent);
        this.model.addLink(proxyToTargetLink);
        String sourcePortVarValue = ((ComposeService) existingLink.getSourceComponent()).getEnvironmentValue(existingLink.getPortVarName());
        final String targetUrlValue = ((ComposeService) existingLink.getSourceComponent()).getEnvironmentValue(existingLink.getUrlVarName());

        if (sourcePortVarValue == null && targetUrlValue != null) {
            sourcePortVarValue = getHostAndPort(targetUrlValue)[1];
        }
        proxyComponent.updateEnvironment(Map.of(
                "TARGET-PROXY-HOST", targetComponent.getName(),
                "TARGET-PROXY-PORT", String.valueOf(sourcePortVarValue)
        ));
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
        proxyToTargetLink.setUsername(existingLink.getUsername());
        proxyToTargetLink.setPassword(existingLink.getPassword());
        this.model.addLink(proxyToTargetLink);

        if (existingLink.getUrlVarName() != null) {
            proxyComponent.updateEnvironment(Map.of(proxyToTargetLink.getUrlVarName(), existingLink.getJdbcHost() + ":" + existingLink.getJdbcPort()));
        }
    }

    /**
     * Injects a proxy component into an existing HTTP API link.
     *
     * @param proxyComponent Proxy component
     * @param existingLink   Existing HTTP API link
     */
    private void injectHttpApiProxy(ComposeService proxyComponent, HttpLink<ComposeComponent> existingLink) {
        ComposeService sourceComponent = (ComposeService) existingLink.getSourceComponent();
        if (existingLink.getHostVarName() != null && existingLink.getPortVarName() != null) {
            sourceComponent.updateEnvironment(Map.of(existingLink.getHostVarName(), proxyComponent.getName()));
        }
        if (existingLink.getPortVarName() != null) {
            sourceComponent.updateEnvironment(Map.of(existingLink.getPortVarName(), "80"));
        }
        if (existingLink.getUrlVarName() != null) {
            sourceComponent.updateEnvironment(Map.of(
                            existingLink.getUrlVarName(), "http://" + proxyComponent.getName() + ":80" + getUrlPath(sourceComponent.getEnvironmentValue(existingLink.getUrlVarName()))
                    )
            );
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

    /**
     * Extracts the base URL from a JDBC URL (remove DB name).
     *
     * @param jdbcUrl The raw JDBC URL
     * @return The base URL (protocol + host + port)
     */
    private String getJdbcBaseUrl(String jdbcUrl) {
        if (jdbcUrl == null || jdbcUrl.isBlank()) {
            return "";
        }
        int index_prefix = jdbcUrl.indexOf("://");
        if (index_prefix == -1) {
            return jdbcUrl; // No protocol found, return as is
        }
        String prefix = jdbcUrl.substring(0, index_prefix + 3);
        String suffix = jdbcUrl.substring(index_prefix + 3);
        int index_host_end = suffix.indexOf('/');
        return prefix + suffix.substring(0, index_host_end);
    }

    /**
     * Returns the host and port of the URL.
     * If url is set, it is parsed to extract the host and port.
     *
     * @return String array containing host and port, or null if not set
     */
    public String[] getHostAndPort(String url) {
        if (url != null) {
            try {
                URI uri = new URI(url);
                String host = uri.getHost();
                int port = uri.getPort() != -1 ? uri.getPort() : 80;
                return new String[]{host, String.valueOf(port)};
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException("Invalid URL format: " + url, e);
            }
        }
        return null;
    }

    /**
     * Returns the URL path of the given URL string.
     * If the URL is malformed or empty, an empty string is returned.
     *
     * @param urlString The URL string to parse
     * @return The URL path, query, and fragment as a single string
     */
    public String getUrlPath(String urlString) {
        if (urlString == null || urlString.isBlank()) {
            return "";
        }
        try {
            URL url = new URI(urlString).toURL();

            // Get everything after host:port
            String path = url.getPath();          // "/api/v1/users"
            String query = url.getQuery();        // "id=123"
            String fragment = url.getRef();       // "section"

            // Reconstruct the full path after host:port
            StringBuilder fullPath = new StringBuilder(path);
            if (query != null) {
                fullPath.append("?").append(query);
            }
            if (fragment != null) {
                fullPath.append("#").append(fragment);
            }
            return fullPath.toString();
        } catch (MalformedURLException | URISyntaxException e) {
            log.error("Malformed URL: {}", urlString, e);
            return "";
        }
    }

}
