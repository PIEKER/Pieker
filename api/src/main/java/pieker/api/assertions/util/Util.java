package pieker.api.assertions.util;

public class Util {

    private Util(){}

    public static String[] getArgumentsFromString(String s){
        String[] sarr = s.split("\\|");
        for (int i = 0; i < sarr.length; i++) {
            sarr[i] = sarr[i].trim();
        }
        return sarr;
    }

    public static boolean parseBoolean(String s){
        String lowerS = s.toLowerCase();
        if (lowerS.equals("true") || lowerS.equals("false")){
            return Boolean.parseBoolean(lowerS);
        } else {
            throw new IllegalArgumentException("invalid boolean argument detected.");
        }
    }
}
