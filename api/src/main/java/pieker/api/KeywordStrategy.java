package pieker.api;

public interface KeywordStrategy {

    /**
     * Processes a value-string and returns assertable value
     *
     * @param args String
     * @return assertable String
     */
    String processValue(String[] args);
}
