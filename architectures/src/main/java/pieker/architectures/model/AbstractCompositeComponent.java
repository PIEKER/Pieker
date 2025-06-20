package pieker.architectures.model;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * Abstract class representing a component in the architecture model.
 */
@Getter
@Setter
public abstract class AbstractCompositeComponent implements Component {

    private String name;
    private boolean isGenerated = false;
    private Link.LinkType providedInterfaceType;
    private List<Component> containedComponents = new ArrayList<>();

    @Override
    public boolean isValid() {
        return attributesAreValid() && containedComponentsAreValid() && !hasCircularReference();
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException if adding the component creates a circular reference
     */
    @Override
    public void addContainedComponent(Component component) {
        if (this == component) {
            throw new IllegalArgumentException("Adding this component creates a circular reference!");
        }
        if (this.containedComponents.contains(component)) {
            return;
        }
        this.containedComponents.add(component);
    }

    @Override
    public void removeContainedComponent(Component component) {
        this.containedComponents.remove(component);
    }

    /**
     * Checks if there is a circular reference starting from the current component.
     *
     * @return true if there is a circular reference, false otherwise
     */
    private boolean hasCircularReference() {
        Set<Component> visited = new HashSet<>();
        Deque<Component> toVisit = new ArrayDeque<>();
        toVisit.push(this);

        while (!toVisit.isEmpty()) {
            Component current = toVisit.pop();
            if (!visited.add(current)) {
                return true;
            }

            for (Component child : current.getContainedComponents()) {
                toVisit.push(child);
            }
        }

        return false;
    }

    /**
     * Checks if the attributes of the component are valid.
     *
     * @return true if all attributes are valid, false otherwise
     */
    private boolean attributesAreValid() {
        return this.name != null && !this.name.isBlank();
    }

    /**
     * Checks if all contained components are valid.
     *
     * @return true if all contained components are valid, false otherwise
     */
    private boolean containedComponentsAreValid() {
        return this.containedComponents.stream()
                .map(c -> ((AbstractCompositeComponent) c).attributesAreValid())
                .reduce(true, (a, b) -> a && b);
    }

}
