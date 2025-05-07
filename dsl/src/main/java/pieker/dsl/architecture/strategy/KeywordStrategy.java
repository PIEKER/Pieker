package pieker.dsl.architecture.strategy;

/**
 * Interface for the Strategy Pattern used for keyword processing.
 */
public interface KeywordStrategy {

    void process(String[] args);
    void validate(String[] args);
}
