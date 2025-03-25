package pieker.architectures.model;

import java.util.Collection;

public interface Component {

    /**
     * Checks if the component is valid.
     *
     * <p> A component is valid if:
     * <ul>
     * <li> it has a name which is not null or empty <br>
     * <li> it has no contained components or all contained components are valid <br>
     * <li> contained components are free of circular references <br>
     * </ul>
     *
     * @return true if the component is valid, false otherwise
     */
    boolean isValid();

    /**
     * @return true if the component is generated by PIEKER, false otherwise
     */
    boolean isGenerated();

    /**
     * Sets the component as generated by PIEKER.
     */
    void setGenerated(boolean isGenerated);

    /**
     * Adds a contained component to this component.
     *
     * @param component component to be added
     */
    void addContainedComponent(Component component);

    /**
     * Removes a contained component from this component.
     *
     * @param component component to be removed
     */
    void removeContainedComponent(Component component);

    /**
     * Retrieves the contained components of this component.
     *
     * @return collection of contained components
     */
    Collection<Component> getContainedComponents();

    /**
     * Retrieves the name of the component.
     *
     * @return name of the component
     */
    String getName();

    /**
     * Retrieves the type of the interface the component provides.
     *
     * @return LinkType of the provided interface
     */
    ComponentLink.LinkType getProvidedInterfaceType();

    /**
     * Sets the type of the interface the component provides.
     */
    void setProvidedInterfaceType(ComponentLink.LinkType providedInterfaceType);

}