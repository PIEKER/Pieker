package pieker.architectures.injector;

import lombok.Getter;
import pieker.architectures.model.ArchitectureModel;
import pieker.architectures.model.Component;
import pieker.architectures.model.Link;
import pieker.architectures.model.util.EnvironmentVariables;

import java.util.Map;

/**
 * Abstract class for a component injector that injects components into an architecture model of type <code>M</code>.
 *
 * @param <M> type of the architecture model
 */
@Getter
public abstract class AbstractComponentInjector<M extends ArchitectureModel<C>, C extends Component> implements ComponentInjector<M, C> {

    protected M model;

    @Override
    public void setModel(M model) {
        this.model = model;
    }

    @Override
    public void injectProxy(Link<C> link, C proxy, Map<String, String> envUpdatesSource) {
        final C source = link.getSourceComponent();

        Link<C> newLink = link.clone();
        newLink.setSourceComponent(source);
        newLink.setTargetComponent(proxy);
        link.setSourceComponent(proxy);

        if (source instanceof EnvironmentVariables envSource && envUpdatesSource != null) {
            envSource.updateEnvironment(envUpdatesSource);
        }

        this.model.addRootComponent(proxy);
        this.model.addLink(newLink);
    }

}
