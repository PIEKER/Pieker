package pieker.evaluator;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import pieker.api.Assertions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Slf4j
public class Evaluator {

    private Map<String, JSONObject> componentMap = new HashMap<>();

    public Evaluator(){}

    public Evaluator(Map<String, JSONObject> componentMap){
        this.componentMap = componentMap;
    }

    /**
     * Adds a JSONObject storing connection attributes mapped to a component identifier.
     *
     * @param identifier of component
     * @param component JSONObject
     */
    public void addComponent(String identifier, JSONObject component){
        this.componentMap.put(identifier, component);
    }

    /**
     * Runs an evaluation on a provided List of steps mapped to a list of evaluation objects.
     *
     * @param stepToEvaluationMap TestSteps mapped to evaluation lists.
     */
    public void run(Map<String, List<Assertions>> stepToEvaluationMap){
        stepToEvaluationMap.forEach(this::evaluateStep);
    }

    private void evaluateStep(String s, List<Assertions> evaluations){
        evaluations.forEach(ev -> {
            if (ev.requiresConnectionParam()) {
                ev.setupConnectionParam(this.componentMap.get(ev.getIdentifier()));
            }
            ev.evaluate();
        });
    }
}
