package pieker.dsl.code.strategy.condition;

import lombok.extern.slf4j.Slf4j;
import pieker.common.ConditionTemplate;
import pieker.dsl.code.Engine;
import pieker.dsl.code.exception.ValidationException;
import pieker.dsl.code.preprocessor.Validator;
import pieker.dsl.code.strategy.KeywordStrategy;
import pieker.common.Template;
import pieker.dsl.code.template.condition.Dropout;
import pieker.dsl.util.Util;

import java.util.Arrays;

@Slf4j
public class DropoutStrategy implements KeywordStrategy {
    @Override
    public void process(String[] args) {
        log.debug("processing DROPOUT node...");
        this.checkArguments(args);
        String[] identifiers = Util.convertStringToStringArray(args[0]);
        ConditionTemplate template;
        if (args[1].contains(".")){
            template = new Dropout(Float.parseFloat(args[1]));
        } else {
            template = new Dropout(Integer.parseInt(args[1]));
        }
        Engine.getCurrentStep().addConditionTemplate(identifiers, template);
    }

    @Override
    public void validate(String[] args) {
        this.checkArguments(args);

        if (args[1].contains(".")){
            float value = Float.parseFloat(args[1]);
            if (value > 1 || value < 0){
                throw new ValidationException("invalid value range of dropoutPercentage: " + value);
            }
        } else {
            int value = Integer.parseInt(args[1]);
            if (value > 1 || value < 0){
                throw new ValidationException("invalid value range of dropoutPercentage: " + value);
            }
        }

        String[] identifiers = Util.convertStringToStringArray(args[0]);
        Arrays.stream(identifiers).forEach(Validator::isIdentifierPresent);
    }

    private void checkArguments(String[] args){
        if (args.length != 2){
            throw new ValidationException("Invalid amount of arguments on DROPOUT keyword.");
        }
    }
}
