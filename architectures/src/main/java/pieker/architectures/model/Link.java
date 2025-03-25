package pieker.architectures.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Class representing a link between components in the architecture model.
 */
@Getter
@Setter
public class Link<C extends Component> implements ComponentLink, Cloneable {

    private C sourceComponent;
    private C targetComponent;
    private LinkType type;


    @Override
    public boolean isValid() {
        return sourceComponent != null && targetComponent != null && type != null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Link<C> clone() {
        try {
            return (Link<C>) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
