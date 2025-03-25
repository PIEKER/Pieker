package pieker.architectures.description;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pieker.architectures.model.ComponentLink;

import java.util.List;

/**
 * Pieker Dependency Description link class. Each link resembles a link between two components in the architecture model.
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
        @JsonSubTypes.Type(value = DescriptionLink.HttpApiLink.class, name = "HTTP_API"),
        @JsonSubTypes.Type(value = DescriptionLink.StorageLink.class, name = "STORAGE")
})
public class DescriptionLink {

    private String target;  // Name of target component (must match the name of a component in the architecture model)
    private ComponentLink.LinkType type;  // Type of link

    @Getter
    @Setter
    @NoArgsConstructor
    public static class HttpApiLink extends DescriptionLink {
        private final ComponentLink.LinkType type = ComponentLink.LinkType.HTTP_API;
        private String targetUrlEnv;  // Name of environment variable for target URL
        private String targetHostEnv;  // Name of environment variable for target host
        private String targetPortEnv;  // Name of environment variable for target port
        private List<String> targetEndpoints;  // List of target API endpoints for this link
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class StorageLink extends DescriptionLink {
        private final ComponentLink.LinkType type = ComponentLink.LinkType.STORAGE;
        private String targetUrlEnv;  // Name of environment variable for target URL
        private String usernameEnv;  // Name of environment variable for username
        private String passwordEnv;  // Name of environment variable for password
    }

}
