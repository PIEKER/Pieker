package pieker.log.eval;

import lombok.extern.slf4j.Slf4j;
import pieker.api.Evaluation;
import pieker.api.KeywordStrategy;
import pieker.log.eval.strategy.ResponseStrategy;
import pieker.log.eval.strategy.StatusStrategy;
import pieker.log.eval.strategy.TimesStrategy;

@Slf4j
public enum Keyword {

    EMPTY("", null),

    TIMES("times", new TimesStrategy()),

    STATUS("status", new StatusStrategy()),

    RESPONSE("response", new ResponseStrategy());


    private final String key;
    private final KeywordStrategy strategy;

    Keyword(String key, KeywordStrategy strategy) {
        this.key = key;
        this.strategy = strategy;
    }

    public void processValue(Evaluation evaluation, String[] args, String[] logs) {
        if(this.strategy == null){
            log.warn("processValue called on empty keyword ({}). Returning empty string (\"\").", this.key);
            return;
        }
        strategy.processValue(evaluation, args, logs);
    }

    public void validate(String[] args){
        if(this.strategy == null){
            log.warn("validation called on empty keyword.");
            return;
        }
        strategy.validate(args);
    }

    @Override
    public String toString(){
        return this.key;
    }
}
