package pieker.dsl;

import pieker.dsl.architecture.strategy.KeywordStrategy;
import pieker.dsl.architecture.strategy.component.*;
import pieker.dsl.architecture.strategy.condition.*;
import pieker.dsl.architecture.strategy.general.DefStrategy;

/**
 * This enum matches a grammar key of PIEKER DSL.
 * Due to the strategy pattern each processing can called on the keyword directly.
 */
public enum Keyword {

    // -- General
    /**
     * <p>
     * Indicates a variable declaration.
     * </p>
     * <i>Example</i>: @def variable = value
     */
    DEF("def", new DefStrategy()),

    // Architecture
    /**
     * <p>
     * Indicates a link between to instances.<br>
     * Expects two identifier as arguments.
     * </p>
     * <i>Example</i>: @link identifier s-a s-b | s-a s-b
     */
    LINK("link", new LinkStrategy()),

    /**
     * <p>
     * Indicates an REST API request for a service.<br>
     * Expects identifier and json containing request specifications (url, method, body...) as arguments.
     * </p>
     * <i>Example</i>: @request identifier | s-a | $request-json
     */
    REQUEST("request", new RequestStrategy()),

    /**
     * <p>
     * Indicates an SQL request for a database.<br>
     * Expects identifier and a query as arguments.
     * </p>
     * <i>Example</i>: @sql identifier | db-a | $sql-statement
     */
    SQL("sql", new SqlStrategy()),

    /**
     * Indicates a non-orchestrated traffic.
     * <i>Example</i>: @passive REQUEST/DATABASE
     */
    PASSIVE("passive", new PassiveStrategy()),

    /**
     * <p>
     * Indicates a proxy for all connections of referenced service.<br>
     * Expects identifier as argument.
     * </p>
     * <i>Example</i>: @service s-a
     */
    SERVICE("service", new ServiceStrategy()),

    /**
     * <p>
     * Indicates a proxy for all connections of referenced database.<br>
     * Expects identifier as argument.
     * </p>
     * <i>Example</i>: @database db
     */
    DATABASE("database", new DatabaseStrategy()),

    // -- Conditions
    /**
     * <p>
     * Delays any functionalities of a component in the beginning of the TestStep.
     * <br> Expects integer or double as argument.
     * </p>
     * <i>Example</i>: @after identifier | $seconds
     */
    AFTER("after", new AfterStrategy()),

    /**
     * <p>
     * Traffic-Keyword: Indicates a re-send duration.
     * <br> Expects integer or doubles as argument.
     * </p>
     * <i>Example</i>: @retry identifier | $seconds
     */
    RETRY("retry", new RetryStrategy()),

    /**
     * <p>
     *Traffic-Keyword: Indicates a re-send a specific amount of times
     * <br> Expects integer or doubles as argument.
     * </p>
     * <i>Example</i>: @times retry | $seconds
     */
    TIMES("times", new TimesStrategy()),

    /**
     * <p>
     * Indicates a 'time in-between requests'.
     * <br> Expects integer or doubles as argument.
     * </p>
     * <i>Example</i>: @delay identifier | $seconds
     */
    DELAY("delay", new DelayStrategy()),

    /**
     * <p>
     * Traffic-Keyword: Indicates a 'percentage of package loss'.
     * <br> Expects integer or doubles as argument.
     * </p>
     * <i>Example</i>: @dropout identifier | $loss-percentage
     */
    DROPOUT("dropout", new DropoutStrategy()),

    /**
     * <p>
     * Specifies a certain amount of time before a component is blocked.
     * </p>
     * <i>Example</i>: @timeout identifier | $seconds
     */
    TIMEOUT("timeout", new TimeoutStrategy()),

    EMPTY("", null);


    private final String key;
    private final KeywordStrategy strategy;

    Keyword(String key, KeywordStrategy strategy) {
        this.key = key;
        this.strategy = strategy;
    }

    public void process(String[] args){
        this.strategy.process(args);
    }

    public void validate(String[] args){
        this.strategy.validate(args);
    }
    @Override
    public String toString(){
        return this.key;
    }
}