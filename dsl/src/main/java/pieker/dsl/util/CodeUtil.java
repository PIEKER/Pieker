package pieker.dsl.util;

import org.json.JSONObject;
import pieker.dsl.code.exception.VariableConfigurationException;
import pieker.dsl.code.preprocessor.VariableNode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeUtil {

    private CodeUtil(){}

    /**
     * Extracts all occurrences of variables in a provided string and injects them with values from
     * the scope map. If an unknown variable is detected a VariableConfigurationException is thrown.
     *
     * @param data String containing possible variables
     * @param scope variable to value map
     * @return injected map
     */
    public static String injectVariablesInString(String data, Map<String, VariableNode> scope){
        for (String variable: extractVariablesFromData(data)){
            if (scope.containsKey(variable)){
                data = data.replaceAll(Pattern.quote(variable), scope.get(variable).getValue());
            } else {
                throw new VariableConfigurationException("Unknown variable detected: '" + variable + "'.");
            }
        }
        return data;
    }

    /**
     * String utility method extracting, variable occurrences in a data string.
     *
     * @param data String containing possible variables.
     * @return Set of all variables found.
     */
    public static Set<String> extractVariablesFromData(String data){
        Set<String> variables = new HashSet<>();

        String regex = "\\$[a-zA-Z0-9_-]+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(data);

        while (matcher.find()) {
            variables.add(matcher.group().trim());
        }
        return variables;
    }

    public static Map<String, String> jsonToMap(String jsonString) {
        Map<String, String> map = new HashMap<>();
        if (jsonString == null || jsonString.isEmpty()) {
            return map;
        }
        JSONObject jsonObject = new JSONObject(jsonString);
        for (String key : jsonObject.keySet()) {
            map.put(key, jsonObject.getString(key));
        }
        return map;
    }
}
