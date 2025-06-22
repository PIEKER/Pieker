package pieker.architectures.description;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pieker.architectures.model.Link;

@Getter
@Setter
@NoArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "interfaceType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DescriptionInterface.HttpInterface.class, name = "HTTP"),
        @JsonSubTypes.Type(value = DescriptionInterface.JdbcInterface.class, name = "JDBC"),
        @JsonSubTypes.Type(value = DescriptionInterface.DatabaseInterface.class, name = "DATABASE")
})
public class DescriptionInterface {

    public Link.LinkType interfaceType;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class HttpInterface extends DescriptionInterface {
        private final Link.LinkType interfaceType = Link.LinkType.HTTP;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class JdbcInterface extends DescriptionInterface {
        private final Link.LinkType interfaceType = Link.LinkType.JDBC;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class DatabaseInterface extends DescriptionInterface {
        private final Link.LinkType interfaceType = Link.LinkType.DATABASE;
    }

}

