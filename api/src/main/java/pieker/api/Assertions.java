package pieker.api;

import org.json.JSONObject;

import java.util.List;

public interface Assertions {

    /**
     * Checks for the requirement of additional connection parameter.
     * @return true if required, false otherwise
     */
    boolean requiresConnectionParam();

    /**
     * Creates a connection setup with required parameters provided in the JSONObject.
     *
     * @param cpJson JSONObject
     */
    void setupConnectionParam(JSONObject cpJson);

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
}
