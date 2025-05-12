package pieker.dsl.architecture.strategy.general;

import lombok.extern.slf4j.Slf4j;
import pieker.dsl.architecture.strategy.KeywordStrategy;

@Slf4j
public class DefStrategy implements KeywordStrategy {
    @Override
    public void process(String[] args) {
        // no processing required. DEF keywords are handled as variables of the Converter.java.
    }

    @Override
    public void validate(String[] args) {
        // no processing required. DEF keywords are handled as variables of the Converter.java.
    }
}
