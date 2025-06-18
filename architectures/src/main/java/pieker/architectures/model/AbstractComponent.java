package pieker.architectures.model;

public abstract class AbstractComponent extends AbstractCompositeComponent implements Component {

    public AbstractComponent() {
        super();
    }

    @Override
    public void addContainedComponent(Component c) {
        throw new UnsupportedOperationException("Leaf cannot have children");
    }

    @Override
    public void removeContainedComponent(Component c) {
        throw new UnsupportedOperationException("Leaf cannot have children");
    }

}
