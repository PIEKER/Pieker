package pieker.supervisor;

import pieker.architectures.model.ArchitectureModel;
import pieker.architectures.model.Component;
import pieker.common.ScenarioTestPlan;

/**
 * Interface for the PIEKER Supervisor.
 * <p>
 * The Supervisor is responsible for setting up the test environment, executing the test plan by sending requests or
 * data to the components, and shutting down the test environment.
 */
public interface Supervisor<M extends ArchitectureModel<? extends Component>> {

    /**
     * Sets up the test environment.
     */
    void setupTestEnvironment();

    /**
     * Executes the set test plan for the set architecture.
     */
    void executeTests();

    /**
     * @param testStepId id of the test step to be executed
     */
    void executeTestStep(String testStepId);

    /**
     * Shuts down the test environment.
     */
    void destroyTestEnvironment();

    /**
     * @return the test plan
     */
    ScenarioTestPlan getTestPlan();

    /**
     * @return current status of the supervisor
     */
    Status getStatus();

    /**
     * @return the architecture model
     */
    M getModel();

    /**
     * Status the supervisor can be in.
     */
    enum Status {
        SETUP,
        RUNNING,
        SHUTDOWN,
        ERROR,
        FINISHED,
        NONE
    }

}
