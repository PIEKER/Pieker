package pieker.api;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface Assertion {

    /**
     * Checks for the requirement of additional connection parameter.
     * @return true if required, false otherwise
     */
    boolean requiresConnectionParam();

    /**
     * @return true if a component is required for evaluation, false otherwise
     */
    boolean isRequiresComponent();
    /**
     * Creates a connection setup with required parameters provided in the JSONObject.
     *
     * @param gatewayUrl base-url of the gateway-service
     */
    void setConnectionParam(String gatewayUrl);

    /**
     * Sets a fileMap mapping a suffix to a map of filenames to file objects.
     * @param fileMap suffix -> filename -> file
     */
    void setFileMap(Map<String, Map<String, File>> fileMap);

    /**
     * Performed after finishing a test-step. Can be used to cache required data for evaluation.
     */
    void prepare();

    /**
     * Entrypoint for evaluating an assertion
     */
    void evaluate();

    /**
     * Returns all Evaluation objects. Necessary for result verification.
     *
     * @return List<Evaluation>
     */
    List<Evaluation> getEvaluation();

    /**
     * Returns the assertion identifier, the assertion is mapped to.
     * @return String
     */
    String getIdentifier();

    /**
     * Sets the stepId for the assertion. Mostly used to update before-each assertions.
     * @param stepId string
     */
    void setStepId(String stepId);

    /**
     * @return a new Assertion instance with different object reference.
     */
    Assertion copy();
}
