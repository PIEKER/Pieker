package pieker.log.eval.strategy;

import lombok.extern.slf4j.Slf4j;
import pieker.api.Evaluation;
import pieker.api.KeywordStrategy;
import pieker.log.eval.CharacterKey;
import pieker.log.eval.Util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class StatusStrategy implements KeywordStrategy {
    /**
     * Validates the provided arguments.
     *
     * @param args String[]
     */
    @Override
    public void validate(String[] args) {
        for(String arg : args){
            try {
                switch (CharacterKey.valueOf(arg)){
                    case EXISTS, FOR_ALL -> {
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
        Pattern pattern = Pattern.compile("REQUEST-STATUS\\{[1-5]\\d{2}}");
        CharacterKey key = Util.getStatusKey(args);
        for (String log : logs) {
            Matcher matcher = pattern.matcher(log);
            if (matcher.find()) {
                String status = matcher.group();
                status = status.substring(15, 18);

                if (Util.evaluateWithQuantifiers(key, evaluation, status)) break;
            }
        }
    }
}
