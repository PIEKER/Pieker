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
     * Executes a test step with the given id and duration.
     *
     * @param testStepId id of the test step to be executed
     * @param duration   duration of the test step in milliseconds
     */
    void executeTestStep(String testStepId, long duration);

    /**
     * Stops all components in the test environment.
     */
    void stopTestEnvironment();

    /**
     * Starts all components in the test environment.
     *
     * @param testPlan          the test plan to be executed
     * @param architectureModel the architecture model containing the components to be started
     */
    void startComponents(ScenarioTestPlan testPlan, ArchitectureModel<?> architectureModel);

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
        STOPPED,
        SHUTDOWN,
        ERROR,
        FINISHED,
        NONE
    }

}
