package pieker.architectures.common.model;


import lombok.Getter;
import lombok.Setter;
import pieker.architectures.model.Component;
import pieker.architectures.model.ComponentLink;
import pieker.architectures.model.Link;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class JdbcLink<C extends Component> extends Link<C> implements ComponentLink {

    /**
     * Map of names of environment variables related to the JDBC link in the source component. These are used to identify
     * which environment variables may need to be updated if a new Component/Link is added to the architecture model.
     *
     * <li>URL_VAR: Name of environment variable that sets the target JDBC-URL</li>
     * <li>USER_VAR: Name of environment variable that sets the target username</li>
     * <li>PASS_VAR: Name of environment variable that sets the target password</li>
     */
    public Map<String, String> sourceRelatedEnvironmentVariables;
    private String jdbcUrl;
    private String username;
    private String password;
    private String databaseName;

    public JdbcLink(C source, C target) {
        this.setSourceComponent(source);
        this.setTargetComponent(target);
        this.setType(LinkType.JDBC);
        this.sourceRelatedEnvironmentVariables = new HashMap<>();
        this.sourceRelatedEnvironmentVariables.put("URL_VAR", null);
        this.sourceRelatedEnvironmentVariables.put("USER_VAR", null);
        this.sourceRelatedEnvironmentVariables.put("PASS_VAR", null);
    }

    /**
     * Creates a new JdbcLink with default values for a proxy component.
     *
     * @param proxy  Proxy component
     * @param target Target component
     * @return JdbcLink between the proxy and target components
     */
    public static <C extends Component> JdbcLink<C> createForProxy(C proxy, C target) {
        JdbcLink<C> link = new JdbcLink<>(proxy, target);
        link.sourceRelatedEnvironmentVariables.put("URL_VAR", "JDBC_PROXY_TARGET");
        link.sourceRelatedEnvironmentVariables.put("USER_VAR", "DB_USER");
        link.sourceRelatedEnvironmentVariables.put("PASS_VAR", "DB_PASS");
        return link;
    }

    @Override
    public boolean isValid() {
        return super.isValid();
    }

    public String getUrlVarName() {
        return this.sourceRelatedEnvironmentVariables.get("URL_VAR");
    }

    public String getUserVarName() {
        return this.sourceRelatedEnvironmentVariables.get("USER_VAR");
    }

    public String getPassVarName() {
        return this.sourceRelatedEnvironmentVariables.get("PASS_VAR");
    }

    public String getJdbcHost() {
        return getHostAndPort(this.jdbcUrl)[0];
    }

    public String getJdbcPort() {
        return getHostAndPort(this.jdbcUrl)[1];
    }

    /**
     * Extracts the host and port from a JDBC URL.
     *
     * @param jdbcUrl The JDBC URL to extract the host and port from.
     * @return An array containing the host and port as strings.
     */
    private static String[] getHostAndPort(String jdbcUrl) {
        try {
            String strippedUrl = jdbcUrl.replaceFirst("^jdbc:\\w+://", "");
            URI uri = new URI("http://" + strippedUrl);
            String host = uri.getHost();
            int port = uri.getPort() != -1 ? uri.getPort() : getDefaultPort(jdbcUrl);

            return new String[]{host, String.valueOf(port)};
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid JDBC URL format: " + jdbcUrl, e);
        }
    }

    private static int getDefaultPort(String jdbcUrl) {
        if (jdbcUrl.startsWith("jdbc:postgresql:")) {
            return 5432;
        } else if (jdbcUrl.startsWith("jdbc:mysql:")) {
            return 3306;
        } else if (jdbcUrl.startsWith("jdbc:sqlserver:")) {
            return 1433;
        }
        return -1;
    }

}
