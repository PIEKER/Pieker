package pieker.architectures.model;

import java.util.Collection;
import java.util.Optional;

/**
 * Interface for an architecture model.
 *
 * @param <C> type of component used in the architecture model
 */
public interface ArchitectureModel<C extends Component> {

    /**
     * Validates the components and links contained in the architecture model.
     *
     * @return true if all entities are valid, false otherwise
     */
    boolean validate();

    /**
     * Adds a component to the architecture model.
     *
     * @param component component to be added
     */
    void addComponent(C component);

    /**
     * Adds a collection of components to the architecture model.
     *
     * @param components components to be added
     */
    void addComponents(Collection<? extends C> components);

    /**
     * Creates a new component with the given name and adds it to the architecture model.
     *
     * @param name name of the component (must be unique
     * @return created component
     */
    C createComponent(String name);

    /**
     * Removes a component from the architecture model.
     *
     * @param component component to be removed
     */
    void removeComponent(C component);

    /**
     * Removes all components that are not linked to any other component.
     */
    void removeUnlinkedComponents();

    /**
     * Adds a link to the architecture model.
     *
     * @param link link to be added
     */
    void addLink(Link<C> link);

    /**
     * Adds a collection of links to the architecture model.
     *
     * @param links links to be added
     */
    void addLinks(Collection<Link<C>> links);

    /**
     * Removes a link from the architecture model.
     *
     * @param link link to be removed
     */
    void removeLink(Link<C> link);

    /**
     * Returns a component with the given name or nothing if none exists.
     *
     * @return Optional Component
     */
    Optional<C> getComponent(String name);

    /**
     * Retrieves the list of all components in the architecture model.
     *
     * @return list of components
     */
    Collection<C> getAllComponents();

    /**
     * Retrieves the list of links with the given component as source.
     *
     * @param source source component
     * @return list of links with component as source
     */
    Collection<Link<C>> getLinksForSource(C source);

    /**
     * Retrieves the list of links with the given component as target.
     *
     * @param target target component
     * @return list of links with component as target
     */
    Collection<Link<C>> getLinksForTarget(C target);

    /**
     * Retrieves the list of links with the given component as source and other as target.
     *
     * @param source source component
     * @param target target component
     * @return list of links with component as source and target
     */
    Collection<Link<C>> getLinksForSourceAndTarget(C source, C target);

}