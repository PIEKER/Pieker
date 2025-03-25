package pieker.dsl.code.strategy.condition;

import lombok.extern.slf4j.Slf4j;
import pieker.dsl.code.Engine;
import pieker.dsl.code.exception.ValidationException;
import pieker.dsl.code.preprocessor.Validator;
import pieker.dsl.code.strategy.KeywordStrategy;
import pieker.common.Template;
import pieker.dsl.code.template.condition.Times;
import pieker.dsl.util.Util;

import java.util.Arrays;

@Slf4j
public class TimesStrategy implements KeywordStrategy {
    @Override
    public void process(String[] args) {
        log.debug("processing TIMES node...");
        this.checkArguments(args);

        String[] identifiers = Util.convertStringToStringArray(args[0]);
        Template template = new Times(Integer.parseInt(args[1]));
        Engine.getCurrentStep().addTemplate(identifiers, template);
    }

    @Override
    public void validate(String[] args) {
        this.checkArguments(args);
        String[] identifiers = Util.convertStringToStringArray(args[0]);
        Arrays.stream(identifiers).forEach(Validator::isIdentifierPresent);
    }

    private void checkArguments(String[] args){
        if (args.length != 2){
            throw new ValidationException("Invalid amount of arguments on TIMES keyword.");
        }
    }
}
