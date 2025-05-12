package pieker.log.eval;

import pieker.api.KeywordStrategy;
import pieker.log.eval.strategy.ResponseStrategy;
import pieker.log.eval.strategy.StatusStrategy;
import pieker.log.eval.strategy.TimesStrategy;

public enum Keyword {

    TIMES("times", new TimesStrategy()),

    STATUS("status", new StatusStrategy()),

    RESPONSE("response", new ResponseStrategy()),

    EMPTY("", null);

    private final String key;
    private final KeywordStrategy strategy;

    Keyword(String key, KeywordStrategy strategy) {
        this.key = key;
        this.strategy = strategy;
    }

    public String processValue(String[] args) {
        return strategy.processValue(args);
    }

    @Override
    public String toString(){
        return this.key;
    }
}
