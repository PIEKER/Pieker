package pieker.api;

public interface KeywordStrategy {

    /**
     * Validates the provided arguments.
     * @param args String[]
     */
    void validate(String[] args);

    /**
     * Processes a value-string and returns assertable value
     *
     * @param args String
     */
    void processValue(Evaluation evaluation, String[] args, String[] logs);

}
