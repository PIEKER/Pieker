package pieker.dsl.util;

import pieker.dsl.PiekerDslException;

public class Util {
    private Util() {}

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
            throw new PiekerDslException("invalid boolean argument detected.");
        }
    }

    public static String[] convertStringToStringArray(String array){
        array = array.trim();
        String[] result;
        if (array.startsWith("[") && array.endsWith("]")){
            result = array.substring(1, array.length() - 1).split(",");
        } else {
            result = new String[]{array};
        }
        for (int i = 0; i < result.length; i++) {
            result[i] = result[i].trim();
        }
        return result;
    }
}
