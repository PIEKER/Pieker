package pieker.log.eval.strategy;

import lombok.extern.slf4j.Slf4j;
import pieker.api.Evaluation;
import pieker.api.KeywordStrategy;
import pieker.log.eval.CharacterKey;
import pieker.log.eval.Util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class ResponseStrategy implements KeywordStrategy {
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
                    case EXISTS, FOR_ALL, SUCCESS, FAILURE -> {
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
        Pattern statusPattern = Util.getMessageByKey(Util.getStatusKey(args));
        Pattern msgPattern = Pattern.compile("REQUEST-MESSAGE\\{.*}");
        for (String l : logs) {
            if (statusPattern.matcher(l).find()) {
                Matcher matcher = msgPattern.matcher(l);
                if (matcher.find()){
                    String msg = matcher.group();
                    msg = msg.substring(16, msg.length() - 1);

                    if (Util.evaluateWithQuantifiers(Util.getQuantifier(args), evaluation, msg)) break;

                } else {
                    log.warn("no message found in log entry: {}", l.replace("\n", ""));
                }
            }
        }
    }
}
