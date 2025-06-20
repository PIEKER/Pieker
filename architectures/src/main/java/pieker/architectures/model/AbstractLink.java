package pieker.architectures.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Class representing a link between components in the architecture model.
 */
@Getter
@Setter
public abstract class AbstractLink<C extends Component> implements Link<C>, Cloneable {

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
