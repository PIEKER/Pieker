package pieker.architectures.description;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pieker.architectures.model.Link;

import java.util.List;

/**
 * PIEKER Interface Description link class. Each link resembles a link between two components in the architecture model.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DescriptionLink.HttpLink.class, name = "HTTP"),
        @JsonSubTypes.Type(value = DescriptionLink.JdbcLink.class, name = "JDBC")
})
public class DescriptionLink {

    private String target;  // Name of target component (must match the name of a component in the architecture model)
    private Link.LinkType type;  // Type of link

    @Getter
    @Setter
    @NoArgsConstructor
    public static class HttpLink extends DescriptionLink {
        private final Link.LinkType type = Link.LinkType.HTTP;
        private String targetUrlEnv;  // Name of environment variable for target URL
        private String targetHostEnv;  // Name of environment variable for target host
        private String targetPortEnv;  // Name of environment variable for target port
        private List<String> targetEndpoints;  // List of target API endpoints for this link
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class JdbcLink extends DescriptionLink {
        private final Link.LinkType type = Link.LinkType.JDBC;
        private String targetUrlEnv;  // Name of environment variable for target URL
        private String usernameEnv;  // Name of environment variable for username
        private String passwordEnv;  // Name of environment variable for password
    }

}
