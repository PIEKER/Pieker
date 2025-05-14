package pieker.architectures.description;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pieker.architectures.model.ComponentLink;

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
        @JsonSubTypes.Type(value = DescriptionInterface.JdbcInterface.class, name = "JDBC"),
        @JsonSubTypes.Type(value = DescriptionInterface.DatabaseInterface.class, name = "DATABASE")
})
public class DescriptionInterface {

    public ComponentLink.LinkType interfaceType;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class HttpApiInterface extends DescriptionInterface {
        private final ComponentLink.LinkType interfaceType = ComponentLink.LinkType.HTTP_API;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class JdbcInterface extends DescriptionInterface {
        private final ComponentLink.LinkType interfaceType = ComponentLink.LinkType.JDBC;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class DatabaseInterface extends DescriptionInterface {
        private final ComponentLink.LinkType interfaceType = ComponentLink.LinkType.DATABASE;
    }

}

