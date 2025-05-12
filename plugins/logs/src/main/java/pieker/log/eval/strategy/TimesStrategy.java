package pieker.log.eval.strategy;

import lombok.extern.slf4j.Slf4j;
import pieker.api.KeywordStrategy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class TimesStrategy implements KeywordStrategy {

    /**
     * Processes a value-string and returns assertable value
     *
     * @param args String
     * @return assertable String
     */
    @Override
    public String processValue(String[] args) {
        Pattern pattern = Pattern.compile("REQUEST-STATUS\\{[1-5]\\d{2}}");
        int counter = 0;
        for (String arg : args) {
            Matcher matcher = pattern.matcher(arg);
            if (matcher.find() && matcher.group().contains("200")) {
                counter += 1;
            }
        }
        return Integer.toString(counter);
    }
}
