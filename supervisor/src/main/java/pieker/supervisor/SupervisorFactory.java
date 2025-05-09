package pieker.supervisor;

import pieker.architectures.compose.model.ComposeArchitectureModel;
import pieker.architectures.model.ArchitectureModel;
import pieker.common.ScenarioTestPlan;
import pieker.supervisor.compose.ComposeSupervisor;

/**
 * Factory class for creating Supervisor instances based on the architecture model.
 */
public class SupervisorFactory {

    /**
     * Creates a Supervisor instance based on the provided architecture model.
     *
     * @param testPlan the test plan to be executed
     * @param model    the architecture model
     * @return a Supervisor instance
     */
    public static Supervisor<?> createSupervisor(ScenarioTestPlan testPlan, ArchitectureModel<?> model) {
        return switch (model) {
            case ComposeArchitectureModel composeModel -> new ComposeSupervisor(testPlan, composeModel);
            default -> throw new IllegalArgumentException("Unsupported architecture model type: " + model.getClass());
        };
    }

}
