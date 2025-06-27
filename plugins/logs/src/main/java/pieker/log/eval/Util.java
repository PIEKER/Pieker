package pieker.log.eval;

import lombok.extern.slf4j.Slf4j;
import pieker.api.Evaluation;

import java.util.regex.Pattern;

@Slf4j
public class Util {
    private Util(){}

    /**
     * evaluates an assertion type under the scope of a quantifier.
     *
     * @param key quantifier to be applied to the assertion.
     * @param evaluation assertion type
     * @param arg status string to be evaluated.
     * @return true if the evaluation is finished ahead of time, false otherwise.
     */
    public static boolean evaluateWithQuantifiers(CharacterKey key, Evaluation evaluation, String arg){
        switch (key){
            case EXISTS -> {
                evaluation.evaluate(arg);
                if (evaluation.isSuccess()) return true;
            }
            case FORALL -> {
                evaluation.evaluate(arg);
                if (!evaluation.isSuccess()) return true;
            }
            default -> log.warn("Invalid quantifier {} for status keyword.", key);
        }
        return false;
    }

    /**
     * <p>
     * Returns a Regex depending on the character key provided.</br></br>
     *      SUCCESS restricts to 2XX or 3XX responses
     *      FAILURE restricts to 4XX or 5XX responses
     * </p>
     *
     * @param key CharacterKey
     * @return Pattern
     */
    public static Pattern getMessageByKey(CharacterKey key){
        return switch (key){
            case SUCCESS -> Pattern.compile("REQUEST-STATUS\\{[1-3]\\d{2}}");
            case FAILURE -> Pattern.compile("REQUEST-STATUS\\{[4-5]\\d{2}}");
            default -> Pattern.compile("REQUEST-STATUS\\{[1-5]\\d{2}}");
        };
    }

    /**
     * Retrieves the first matching quantifier out of an array of arguments.
     *
     * @param args array of arguments
     * @return CharacterKey
     */
    public static CharacterKey getQuantifier(String[] args){
        for (String arg : args) {
            if (arg.equals("exists") || arg.equals("for-all")) {
                return CharacterKey.valueOf(arg.toUpperCase());
            }
        }
        return CharacterKey.EXISTS;
    }

    /**
     * Retrieves the first matching StatusKey out of an array of arguments.
     *
     * @param args array of arguments
     * @return CharacterKey
     */
    public static CharacterKey getStatusKey(String[] args){
        for (String arg : args) {
            if (arg.equals("success") || arg.equals("failure")) {
                return CharacterKey.valueOf(arg.toUpperCase());
            }
        }
        return CharacterKey.EMPTY;
    }
}
