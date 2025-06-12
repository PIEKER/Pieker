package pieker.log.eval.strategy;

import lombok.extern.slf4j.Slf4j;
import pieker.api.Evaluation;
import pieker.api.KeywordStrategy;
import pieker.log.eval.CharacterKey;
import pieker.log.eval.Util;

import java.util.regex.Pattern;

@Slf4j
public class TimesStrategy implements KeywordStrategy {

    /**
     * Validates the provided arguments.
     * @param args String[]
     */
    @Override
    public void validate(String[] args){
        for(String arg : args){
            try {
                switch (CharacterKey.valueOf(arg)){
                    case SUCCESS, FAILURE, FOR_ALL -> {
                        // character-keys have semantic impact
                    }
                    default -> log.warn("Argument {} has no semantic impact on times keyword.", arg);

                }
            } catch (IllegalArgumentException _) {
                log.warn("Invalid argument {} for keyword times.", arg);
            }
        }
    }

    /**
     * Processes a value-string and returns assertable value
     *
     * @param args String
     */
    @Override
    public void processValue(Evaluation evaluation, String[] args, String[] logs) {

        Pattern pattern = (args.length != 0)?
                Util.getMessageByKey(CharacterKey.valueOf(args[0])):
                Util.getMessageByKey(CharacterKey.EMPTY);

        int counter = 0;
        for (String log : logs) {
            if (pattern.matcher(log).find()) {
                counter += 1;
            }
        }
        evaluation.evaluate(Integer.toString(counter));
    }
}
