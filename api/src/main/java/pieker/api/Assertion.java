package pieker.api;

import org.json.JSONObject;

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
     * Creates a connection setup with required parameters provided in the JSONObject.
     *
     * @param cpJson JSONObject
     */
    void setConnectionParam(JSONObject cpJson);

    /**
     * Sets a fileMap mapping a suffix to a map of filenames to file objects.
     * @param fileMap suffix -> filename -> file
     */
    void setFileMap(Map<String, Map<String, File>> fileMap);

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
     * @param stepId
     */
    void setStepId(String stepId);
}
