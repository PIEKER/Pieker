package pieker.architectures.injector;

import pieker.architectures.injector.exceptions.ComponentInjectionException;
import pieker.architectures.model.ArchitectureModel;
import pieker.architectures.model.Component;
import pieker.architectures.model.Link;
import pieker.common.ScenarioTestPlan;

import java.util.Map;

/**
 * Interface for a component injector that injects components into an architecture model.
 *
 * @param <M> type of the architecture model
 * @param <C> type of the component
 */
public interface ComponentInjector<M extends ArchitectureModel<C>, C extends Component> {

    /**
     * Sets the architecture model to inject components into.
     *
     * @param model architecture model
     */
    void setModel(M model);

    /**
     * Injects a proxy component into the set architecture model inbetween the source and target components of the given
     * link. The environment variables of the source component are updated with the given updates, set null if not
     * applicable.
     *
     * @param link             original link between source and target components
     * @param proxy            component to inject between source and target components
     * @param envUpdatesSource the environment updates of the source service, null if not applicable
     */
    void injectProxy(Link<C> link, C proxy, Map<String, String> envUpdatesSource);

    /**
     * Injects the given components into existing links of the set architecture model.
     *
     * @param testPlan test plan containing the components to inject
     * @throws ComponentInjectionException if the model is invalid after component injection
     */
    void injectComponents(ScenarioTestPlan testPlan) throws ComponentInjectionException;

    /**
     * Add a component to the architecture model that is not injected into an existing link but prepended before another
     * component.
     *
     * @param proxy           component to prepend before the target component
     * @param targetComponent component to prepend the new component before
     * @throws ComponentInjectionException if the model is invalid after component addition
     */
    <T extends C> void prependComponent(T proxy, C targetComponent) throws ComponentInjectionException;

}
