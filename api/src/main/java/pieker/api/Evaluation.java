package pieker.api;

public interface Evaluation {

    /**
     * @return assert string depending on the DSL configuration
     */
    String getAssertType();

    /**
     * @return the DSL value and expression into a single String.
     */
    String getAssertExpression();

    /**
     * @return error message, if evaluation fails
     */
    String getErrorMessage();

    /**
     * @return true if evaluation was successful, false otherwise
     */
    boolean isSuccess();

    /**
     * Entrypoint for triggering an evaluation.
     * @param arg processed value, ready for testing
     */
    void evaluate(String arg);

    /**
     * Sets a string as error-message
     * @param errorMessage string
     */
    void setErrorMessage(String errorMessage);

    /**
     * @return  a new Evaluation instance with different object reference.
     */
    Evaluation copy();
}
