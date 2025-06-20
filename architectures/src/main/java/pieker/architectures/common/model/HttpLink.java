package pieker.architectures.common.model;

import lombok.Getter;
import lombok.Setter;
import pieker.architectures.model.Component;
import pieker.architectures.model.AbstractLink;
import pieker.architectures.model.Link;
import pieker.architectures.util.Validators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class HttpLink<C extends Component> extends AbstractLink<C> implements Link<C> {

    /**
     * Map of names of environment variables related to the API link in the source component. These are used to identify
     * which environment variables may need to be updated if a new Component/Link is added to the architecture model.
     *
     * <li>URL_VAR: Name of environment variable that sets the target URL</li>
     * <li>HOST_VAR: Name of environment variable that sets the target hostname</li>
     * <li>PORT_VAR: Name of environment variable that sets the target port</li>
     */
    public Map<String, String> sourceRelatedEnvironmentVariables;
    private String url;
    private String targetHost;
    private int targetPort;
    private List<String> targetEndpoints;

    public HttpLink(C source, C target) {
        this.setSourceComponent(source);
        this.setTargetComponent(target);
        this.setType(LinkType.HTTP);
        this.targetEndpoints = new ArrayList<>();
        this.sourceRelatedEnvironmentVariables = new HashMap<>();
        this.sourceRelatedEnvironmentVariables.put("HOST_VAR", null);
        this.sourceRelatedEnvironmentVariables.put("PORT_VAR", null);
        this.sourceRelatedEnvironmentVariables.put("URL_VAR", null);
    }

    public HttpLink(C source, C target, String url) {
        this(source, target);
        this.url = url;
    }

    public HttpLink(C source, C target, String targetHost, int targetPort) {
        this(source, target);
        this.targetHost = targetHost;
        this.targetPort = targetPort;
    }

    /**
     * Creates a new HttpLink with default values for a proxy component.
     *
     * @param proxy  Proxy component
     * @param target Target component
     * @return HttpLink between the proxy and target components
     */
    public static <C extends Component> HttpLink<C> createForProxy(C proxy, C target) {
        HttpLink<C> link = new HttpLink<>(proxy, target);
        link.sourceRelatedEnvironmentVariables.put("HOST_VAR", "TARGET-PROXY-HOST");
        link.sourceRelatedEnvironmentVariables.put("PORT_VAR", "TARGET-PROXY-PORT");
        link.sourceRelatedEnvironmentVariables.put("URL_VAR", "TARGET-PROXY-URL");
        return link;
    }

    @Override
    public boolean isValid() {
        return super.isValid() && (Validators.allValidURLs(this.targetHost, this.targetPort, this.targetEndpoints) || Validators.isValidURL(this.url));
    }

    public void addEndpoint(String endpoint) {
        if (!endpoint.isBlank()) {
            this.targetEndpoints.add(endpoint);
        }
    }

    public String getHostVarName() {
        return this.sourceRelatedEnvironmentVariables.get("HOST_VAR");
    }

    public String getPortVarName() {
        return this.sourceRelatedEnvironmentVariables.get("PORT_VAR");
    }

    public String getUrlVarName() {
        return this.sourceRelatedEnvironmentVariables.get("URL_VAR");
    }

}