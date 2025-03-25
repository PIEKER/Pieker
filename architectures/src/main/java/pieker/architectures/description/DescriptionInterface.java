package pieker.architectures.description;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pieker.architectures.model.ComponentLink;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "interfaceType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DescriptionInterface.HttpApiInterface.class, name = "HTTP_API"),
        @JsonSubTypes.Type(value = DescriptionInterface.StorageInterface.class, name = "STORAGE")
})
public class DescriptionInterface {

    private ComponentLink.LinkType interfaceType;
    private String host;
    private int port;
    private String url;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class HttpApiInterface extends DescriptionInterface {
        private final ComponentLink.LinkType interfaceType = ComponentLink.LinkType.HTTP_API;
        private List<String> endpoints;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class StorageInterface extends DescriptionInterface {
        private final ComponentLink.LinkType interfaceType = ComponentLink.LinkType.STORAGE;
    }

}

