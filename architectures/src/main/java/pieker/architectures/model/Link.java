package pieker.architectures.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

public interface Link<C extends Component> extends Cloneable {

    /**
     * Checks if the link is valid.
     *
     * <p>A link is valid if the source and target components are not null and the type is set.
     *
     * @return true if link is valid, false otherwise
     */
    boolean isValid();

    /**
     * @return source component of this link
     */
    C getSourceComponent();

    void setSourceComponent(C sourceComponent);

    /**
     * @return target component of this link
     */
    C getTargetComponent();

    void setTargetComponent(C targetComponent);

    /**
     * @return type of this link
     */
    LinkType getType();

    /**
     * Clones this link.
     */
    Link<C> clone();

    /**
     * Enum representing the types of supported links.
     */
    @NoArgsConstructor
    enum LinkType {
        // Communication-related links
        HTTP,
        TCP,
        // Storage-related links
        DATABASE,
        JDBC(DATABASE),
        // Other
        UNSUPPORTED;

        @Getter
        private LinkType superType;

        LinkType(LinkType superType) {
            this.superType = superType;
        }

        public boolean hasSuperType() {
            return this.superType != null;
        }

        /**
         * Retrieves the list of subtypes for a given supertype.
         *
         * @param superType the supertype to search for
         * @return list of subtypes
         */
        public List<LinkType> getSubTypes(LinkType superType) {
            List<LinkType> subTypes = new ArrayList<>();
            for (LinkType type : LinkType.values()) {
                if (type.hasSuperType() && type.getSuperType() == superType) {
                    subTypes.add(type);
                }
            }
            return subTypes;
        }

    }

}
