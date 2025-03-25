package pieker.dsl.code.preprocessor;

import lombok.extern.slf4j.Slf4j;
import pieker.dsl.code.exception.ValidationException;
import pieker.dsl.model.*;

import java.util.HashSet;
import java.util.Set;

@Slf4j
public class Validator {

    private static Set<String> identifierSet = new HashSet<>();
    private static Set<String> componentSet = new HashSet<>();

    private Validator(){}

    /**
     * Validates semantics of PMT nodes.
     * @param feature PMT root node
     */
    public static void validate(Feature feature){
        // feature constraints

        // background: validation only required if background contains other information than variables
        feature.getScenarioList().forEach(Validator::validateScenario);
    }

    private static void validateScenario(Scenario scenario){
        // scenario constraints

        // before all
        if (scenario.getBeforeEach() != null){
            Validator.validateStep(scenario.getBeforeEach(), true);
        }

        scenario.getStepList().forEach(step -> Validator.validateStep(step, false));

        // guarantee scenario scope
        identifierSet.clear();
        componentSet.clear();

    }

    private static void validateStep(Step step, boolean isBeforeEach){
        Set<String> scenarioIdentifierBefore = Set.copyOf(identifierSet);
        Set<String> scenarioComponentBefore = Set.copyOf(componentSet);

        // validate given
        Given given = step.getGiven();
        log.debug("validating Given at: {}", given.getLine());
        given.validateEntryList();

        When when = step.getWhen();
        if(when != null){
            log.debug("validating When at: {}", when.getLine());
            when.validateEntryList();
        }

        Then then = step.getThen();
        if(then != null){
            log.debug("validating Then at: {}", then.getLine());
            then.validateLogAllList();
            then.validateAssertList();
        }
        if (!isBeforeEach){
            identifierSet = new HashSet<>(scenarioIdentifierBefore); // guarantee step scope
            componentSet = new HashSet<>(scenarioComponentBefore); // guarantee step scope
        }
    }

    public static void updateIdentifierSet(String s){
        if(!identifierSet.add(s)){
            throw new ValidationException("identifier '" + s + "' already taken.");
        }
    }

    public static void isIdentifierPresent(String s){
        if(!identifierSet.contains(s)){
            throw new ValidationException("identifier '" + s + "' not present.");
        }
    }
    public static void updateComponentSet(String s){
        if(!componentSet.add(s)){
            throw new ValidationException("component/connection '" + s + "' already proxied.");
        }
    }
}
