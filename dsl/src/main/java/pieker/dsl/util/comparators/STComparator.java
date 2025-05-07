package pieker.dsl.util.comparators;

import pieker.dsl.architecture.component.SupervisorTraffic;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class STComparator implements Comparator<SupervisorTraffic> {

    @Override
    public int compare(SupervisorTraffic s1, SupervisorTraffic s2) {
        String regex = "^\\d+";
        Pattern pattern = Pattern.compile(regex);
        String s1Identifier = s1.getIdentifier();
        String s2Identifier = s2.getIdentifier();

        Matcher matcherS1 = pattern.matcher(s1Identifier);
        Matcher matcherS2 = pattern.matcher(s2Identifier);

        if (matcherS1.find() && matcherS2.find()) {
            int s1order = Integer.parseInt(matcherS1.group());
            int s2order = Integer.parseInt(matcherS2.group());
            String substringS1 = s1Identifier.substring(matcherS1.group().length());
            String substringS2 = s2Identifier.substring(matcherS2.group().length());

            return (s1 != s2) ? Integer.compare(s1order, s2order) : substringS1.compareTo(substringS2);
        } else if (matcherS1.find()) {
            return -1;
        } else if (matcherS2.find()) {
            return 1;
        } else {
            return s1Identifier.compareTo(s2Identifier);
        }
    }
}
