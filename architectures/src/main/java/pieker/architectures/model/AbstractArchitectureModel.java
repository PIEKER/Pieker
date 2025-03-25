package pieker.architectures.model;

import lombok.Getter;

import java.util.*;

/**
 * Abstract class representing an architecture model.
 *
 * @param <C> type of component used in the architecture model
 */
@Getter
public abstract class AbstractArchitectureModel<C extends Component> implements ArchitectureModel<C> {

    /// List of the highest level components in the architecture model.
    private final List<C> components = new ArrayList<>();
    /// List of all links in the architecture model.
    private final List<Link<C>> links = new ArrayList<>();

    @Override
    public boolean validate() {
        return this.components.stream().allMatch(C::isValid) && this.links.stream().allMatch(Link::isValid);
    }

    @Override
    public Optional<C> getComponent(String name) {
        return this.components.stream().filter(component -> component.getName().equals(name)).findFirst();
    }

    @Override
    public List<C> getAllComponents() {
        List<C> allComponents = new ArrayList<>();
        for (C component : components) {
            collectComponents(component, allComponents);
        }
        return allComponents;
    }

    @SuppressWarnings("unchecked")
    private void collectComponents(C component, List<C> allComponents) {
        allComponents.add(component);
        if (component instanceof AbstractComponent) {
            for (Component child : component.getContainedComponents()) {
                collectComponents((C) child, allComponents);
            }
        }
    }

    @Override
    public List<Link<C>> getLinksForSource(C source) {
        return this.links.stream().filter(link -> link.getSourceComponent().equals(source)).toList();
    }

    @Override
    public List<Link<C>> getLinksForTarget(C target) {
        return this.links.stream().filter(link -> link.getTargetComponent().equals(target)).toList();
    }

    @Override
    public List<Link<C>> getLinksForSourceAndTarget(C source, C target) {
        return this.links.stream().filter(link -> link.getSourceComponent().equals(source) && link.getTargetComponent().equals(target)).toList();
    }

    @Override
    public void addComponent(C component) {
        this.components.add(component);
    }

    @Override
    public void addComponents(Collection<? extends C> components) {
        this.components.addAll(components);
    }

    @Override
    public C createComponent(String name) {
        if (!validateComponentName(name)) {
            throw new IllegalArgumentException("Component name already exists in the model");
        }
        C component = this.createComponentInstance(name);
        component.setGenerated(true);
        this.addComponent(component);
        return component;
    }

    protected abstract C createComponentInstance(String name);

    @Override
    public void addLink(Link<C> link) {
        this.links.add(link);
    }

    @Override
    public void addLinks(Collection<Link<C>> links) {
        this.links.addAll(links);
    }

    @Override
    public void removeComponent(C component) {
        this.components.remove(component);
    }

    @Override
    public void removeUnlinkedComponents() {
        Set<C> linkedComponents = new HashSet<>();
        for (Link<C> link : links) {
            if (link.getSourceComponent() != null) {
                linkedComponents.add(link.getSourceComponent());
            }
            if (link.getTargetComponent() != null) {
                linkedComponents.add(link.getTargetComponent());
            }
        }

        this.getAllComponents().stream().filter(component -> !linkedComponents.contains(component)).forEach(this::removeComponent);
    }

    @Override
    public void removeLink(Link<C> link) {
        this.links.remove(link);
    }

    private boolean validateComponentName(String name) {
        return this.components.stream().noneMatch(component -> component.getName().equals(name));
    }
}