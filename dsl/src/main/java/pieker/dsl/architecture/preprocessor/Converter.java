package pieker.dsl.architecture.preprocessor;

import lombok.extern.slf4j.Slf4j;
import org.cornutum.regexpgen.RandomGen;
import org.cornutum.regexpgen.RegExpGen;
import org.cornutum.regexpgen.js.Provider;
import org.cornutum.regexpgen.random.RandomBoundsGen;
import pieker.api.assertions.Assert;
import pieker.dsl.PiekerDslException;
import pieker.dsl.architecture.Engine;
import pieker.dsl.architecture.exception.ValidationException;
import pieker.dsl.architecture.exception.VariableConfigurationException;
import pieker.dsl.Keyword;
import pieker.dsl.model.*;
import pieker.dsl.model.assertions.DatabaseAssert;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static pieker.dsl.util.ArchitectureUtil.injectVariablesInString;

@Slf4j
public class Converter {

    private static final Map<String, VariableNode> GLOBAL_SCOPE = new HashMap<>();
    private static final Random RANDOM = new Random();
    private static final RandomGen RANDOM_GEN = new RandomBoundsGen();

    private Converter() {}

    /**
     * Preprocesses the Pieker AST:
     *  -> initializes variable scope test-configuration
     *  -> replaces variable occurrences depending on further scopes (test-scenario, test-step).
     *  -> tbd
     *
     * @param feature root node
     */
    public static void run(Feature feature){
        createGlobalScope(feature.getBackground());
        feature.getScenarioList().forEach(Converter::injectVariablesInScenario);
    }

    /**
     * Creates global scope of variables given for the entire test-configuration.
     *
     * @param background node containing possible variable entries.
     */
    private static void createGlobalScope(Background background){
        if (background != null){
            List<Condition.Entry> variables = background.getEntryList();
            variables.forEach(Converter::loadFileFromEntry);
            variables.forEach(Converter::generateValuesInEntryData);

            updateScopeFromEntryList(variables, GLOBAL_SCOPE);
            log.debug("global scope set with a total of {} variables.", GLOBAL_SCOPE.size());
        } else {
            log.info("no global scope created due to empty background node.");
            return;
        }
        // eliminate variable references in variable values
        VariableNode.constructVariableGraph(GLOBAL_SCOPE);
        VariableNode.injectVariablesOfGraph(GLOBAL_SCOPE);
    }

    private static void createScenarioScope(Scenario scenario, Map<String, VariableNode> currentScope){
        List<Condition.Entry> variables = scenario.getEntryList();
        variables.forEach(Converter::loadFileFromEntry);
        variables.forEach(Converter::generateValuesInEntryData);

        updateScopeFromEntryList(variables, currentScope);
        // eliminate variable references in variable values
        VariableNode.constructVariableGraph(currentScope);
        VariableNode.injectVariablesOfGraph(currentScope);
    }

    /**
     * Retrieves local scope of variables given for a specific test-step.
     *
     * @param when node containing possible variable entries.
     */
    private static void createStepScope(When when, Map<String, VariableNode> currentScope){
        List<Condition.Entry> variables = when.getEntriesByKey(Keyword.DEF);
        variables.forEach(Converter::loadFileFromEntry);
        variables.forEach(Converter::generateValuesInEntryData);

        updateScopeFromEntryList(variables, currentScope);
        // eliminate variable references in variable values
        VariableNode.constructVariableGraph(currentScope);
        VariableNode.injectVariablesOfGraph(currentScope);
    }

    /*
        INJECT
     */

    /**
     * Orchestrates the variable injection of step nodes. If required, a local scope will be added to the current
     * scope-map. If processed successfully all valid variables contained in feature node are resolved.
     *
     * @param scenario root node.
     */
    private static void injectVariablesInScenario(Scenario scenario) {
            Map<String,VariableNode> currentScope = new HashMap<>(GLOBAL_SCOPE);

            // get possible variables from scenarioBackground
            createScenarioScope(scenario, currentScope);
            List<Step> stepList = new ArrayList<>(scenario.getStepList());
            Step beforeEach = scenario.getBeforeEach();
            if (beforeEach != null){
                stepList.add(beforeEach);
            }
            // inject variables in scenario-steps
            for (Step step: stepList){
                When when = step.getWhen();
                if (when != null){
                    createStepScope(when, currentScope);
                    injectVariablesInWhen(when, currentScope);
                }
                injectVariablesInGiven(step.getGiven(), currentScope);
                injectVariablesInThen(step.getThen(), currentScope);
            }
    }

    /**
     * Injects all variables of provided 'Given' node. Fails, when detecting an unknown variable.
     *
     * @param given node containing possible variable.
     * @param scope of variables known to the converter.
     */
    private static void injectVariablesInGiven(Given given, Map<String, VariableNode> scope) {
        if (given == null) return;

        for (Condition.Entry entry: given.getEntryList()){
            loadFileFromEntry(entry);
            generateValuesInEntryData(entry);

            String data = entry.getData();
            entry.setData(injectVariablesInString(data, scope));
        }
    }

    /**
     * Injects all variables of provided 'When' node. Fails, when detecting an unknown variable.
     *
     * @param when node containing possible variable.
     * @param scope of variables known to the converter.
     */
    private static void injectVariablesInWhen(When when, Map<String, VariableNode> scope) {
        if (when == null) return;

        for (Condition.Entry entry: when.getEntriesByExcludeKey(Keyword.DEF)){
            generateValuesInEntryData(entry);

            String data = entry.getData();
            entry.setData(injectVariablesInString(data, scope));
        }
    }

    /**
     * WIP
     *
     * @param then node containing possible variable.
     * @param scope of variables known to the converter.
     */
    private static void injectVariablesInThen(Then then, Map<String, VariableNode> scope) {
        // File possibilities: database-table node
        // Variable possibilities: every String
        if (then == null) return;

        // logAll
        then.setLogAllList(
                then.getLogAllList().stream().map(identifier -> {
                    if (scope.containsKey(identifier)){
                        identifier = scope.get(identifier).getValue();
                    }
                    return identifier;
                }).toList()
        );

        // assert
        for (Assert ass: then.getAssertList()){
            if (ass instanceof DatabaseAssert databaseAssert){
                databaseAssert.setTableSelect(loadFileFromString(databaseAssert.getTableSelect()));
                databaseAssert.setTableSelect(injectVariablesInString(databaseAssert.getTableSelect(), scope));
            }

            ass.getBoolList().forEach(assertBool -> {
                assertBool.setExpression(injectVariablesInString(assertBool.getExpression(), scope));
                assertBool.setValue(injectVariablesInString(assertBool.getValue(), scope));
            });

            ass.getEqualsList().forEach(assertEquals -> {
                assertEquals.setExpected(injectVariablesInString(assertEquals.getExpected(), scope));
                assertEquals.setValue(injectVariablesInString(assertEquals.getValue(), scope));
            });
            ass.getNullList().forEach(assertNull ->
                    assertNull.setValue(injectVariablesInString(assertNull.getValue(), scope)));
        }

    }

    /*
        Utility
     */

    /**
     * Processes variable assignments in 'Condition.Entry' instances and add/replaces variables found to the currentScope
     * argument.
     *
     * @param variables List of entries.
     * @param currentScope Map of currently known variables.
     */
    private static void updateScopeFromEntryList(List<Condition.Entry> variables,
                                                                    Map<String, VariableNode> currentScope){
        try{
            for (Condition.Entry varEntry : variables){
                String varAssignment = varEntry.getData();
                assert varAssignment != null && !varAssignment.isEmpty() ;
                String[] varTuple = varAssignment.split("=");
                assert varTuple.length == 2;
                String varIdent = "$" + varTuple[0].trim();
                String varValue = varTuple[1].trim();
                if (currentScope.containsKey(varIdent)){
                    currentScope.get(varIdent).setValue(varValue);
                    log.debug("replaced variable '{}' in current scope with value '{}'", varIdent, varValue);
                } else {
                    currentScope.put(varIdent, new VariableNode(varIdent, varValue));
                    log.debug("add new variable '{}' to current scope with value '{}'", varIdent, varValue);
                }
            }
        } catch (AssertionError e){
            throw new VariableConfigurationException("invalid variable initialization detected: " + e.getMessage());
        }
    }

    /**
     * Handles any automated value generation in the DSL. Supported are:
     * [FLOATV, INTV] for number generation as well as RSTART()REND for strings matching
     * a provided regular expression.
     *
     * @param entry instance containing possible generation operations
     */
    private static void generateValuesInEntryData(Condition.Entry entry){
        String floatPattern = ":FLOATV\\(\\s*\\d+\\.\\d+\\s*,\\s*\\d+\\.\\d+\\s*\\)";
        String intPattern = ":INTV\\(\\s*\\d+\\s*,\\s*\\d+\\s*\\)";
        String regexPattern = ":RSTART\\( (?:(?!\\)REND).)* \\)REND";

        generateValueFromPattern(entry, Float.class, floatPattern);
        generateValueFromPattern(entry, Integer.class, intPattern);

        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(entry.getData());

        while(matcher.find()) {
            String substring = matcher.group();
            substring = substring.substring(8).trim();
            substring = substring.substring(0, substring.length()-5).trim();
            RegExpGen generator = Provider.forEcmaScript().matchingExact(substring);
            entry.setData(matcher.replaceFirst(generator.generate(RANDOM_GEN)));
            matcher = pattern.matcher(entry.getData());
        }

    }

    private static void generateValueFromPattern(Condition.Entry entry, Class<?> type, String regex){
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(entry.getData());
        while(matcher.find()){
            String substring = matcher.group();
            String[] range = substring.split(",");
            if (range.length != 2) {
                throw new PiekerDslException("SyntaxError on float-interval detected. Probably invalid delimiter used.");
            }

            if (type == Float.class){
                range[0] = range[0].substring(8).trim(); // remove function identifier and white spaces
                range[1] = range[1].substring(0, range[1].length()-1).trim(); // remove bracket and white spaces

                float lower = Float.parseFloat(range[0]);
                float higher = Float.parseFloat(range[1]);
                if (higher < lower){
                    throw new ValidationException("invalid interval provided on :FLOATV(x,y). x <= y required.");
                }
                float random = RANDOM.nextFloat(lower, higher);
                entry.setData(matcher.replaceFirst(Float.toString(random)));
            } else if (type == Integer.class) {
                range[0] = range[0].substring(6).trim(); // remove function identifier and white spaces
                range[1] = range[1].substring(0, range[1].length()-1).trim(); // remove bracket and white spaces

                int lower = Integer.parseInt(range[0]);
                int higher = Integer.parseInt(range[1]);
                if (higher < lower){
                    throw new ValidationException("invalid interval provided on :INTV(x,y). x <= y required.");
                }
                int random = RANDOM.nextInt(lower, higher);
                entry.setData(matcher.replaceFirst(Integer.toString(random)));
            } else {
                throw new IllegalArgumentException("unknown type '" + type + "' used to generate intervall values.");
            }
            matcher = pattern.matcher(entry.getData());
        }
    }

    private static void loadFileFromEntry(Condition.Entry entry){
        entry.setData(loadFileFromString(entry.getData()));
    }

    private static String loadFileFromString(String data){
        Pattern pattern = Pattern.compile(":FILE\\(.*\\)");
        Matcher matcher = pattern.matcher(data);
        while(matcher.find()) {
            String substring = matcher.group();
            substring = substring.substring(6, substring.length()-1);
            String[] args = substring.split(",");
            String fileHash;
            if (args.length == 1){
                fileHash = Engine.getFileManager().loadFileAsString(substring, 0);
            } else if (args.length == 2) {
                fileHash = Engine.getFileManager().loadFileAsString(args[0], Integer.parseInt(args[1].trim()));
            } else {
                throw new ValidationException("invalid amount of arguments on :FILE function provided");
            }
            data = matcher.replaceFirst(FileManager.PREFIX + fileHash + FileManager.SUFFIX);
        }
        return data;
    }
}