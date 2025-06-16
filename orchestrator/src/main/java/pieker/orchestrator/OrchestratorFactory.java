package pieker.orchestrator;

import pieker.architectures.compose.model.ComposeArchitectureModel;
import pieker.architectures.model.ArchitectureModel;
import pieker.common.ScenarioTestPlan;
import pieker.orchestrator.compose.ComposeOrchestrator;

/**
 * Factory class for creating PIEKER Orchestrator instances based on the architecture model.
 */
public class OrchestratorFactory {

    /**
     * Creates an Orchestrator instance based on the provided architecture model.
     *
     * @param testPlan the test plan to be executed
     * @param model    the architecture model
     * @return an Orchestrator instance
     */
    public static Orchestrator<?> createOrchestrator(ScenarioTestPlan testPlan, ArchitectureModel<?> model) {
        return switch (model) {
            case ComposeArchitectureModel composeModel -> new ComposeOrchestrator(testPlan, composeModel);
            default -> throw new IllegalArgumentException("Unsupported architecture model type: " + model.getClass());
        };
    }

}
