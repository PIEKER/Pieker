package pieker.dsl.code.strategy.condition;

import lombok.extern.slf4j.Slf4j;
import pieker.common.ConditionTemplate;
import pieker.dsl.code.Engine;
import pieker.dsl.code.exception.ValidationException;
import pieker.dsl.code.preprocessor.Validator;
import pieker.dsl.code.strategy.KeywordStrategy;
import pieker.dsl.code.template.condition.Timeout;
import pieker.dsl.util.Util;

import java.util.Arrays;

@Slf4j
public class TimeoutStrategy implements KeywordStrategy {
    @Override
    public void process(String[] args) {
        log.debug("processing TIMEOUT node...");
        this.checkArguments(args);

        String[] identifiers = Util.convertStringToStringArray(args[0]);
        ConditionTemplate template;
        if (args[0].contains(".")){
            template = new Timeout(Float.parseFloat(args[0]));
        } else {
            template = new Timeout(Integer.parseInt(args[1]));
        }
        Engine.getCurrentStep().addConditionTemplate(identifiers, template);
    }

    @Override
    public void validate(String[] args) {
        this.checkArguments(args);
        String[] identifiers = Util.convertStringToStringArray(args[0]);
        Arrays.stream(identifiers).forEach(Validator::isIdentifierPresent);
    }

    private void checkArguments(String[] args){
        if (args.length != 2){
            throw new ValidationException("Invalid amount of arguments on TIMEOUT keyword.");
        }
    }
}
