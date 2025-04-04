package pieker.dsl.code.component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import pieker.common.ScenarioComponent;
import pieker.common.ScenarioProxyComponent;
import pieker.common.Template;

import java.util.*;

@Getter
public abstract class ProxyComponent<T extends ProxyComponent<T>> implements ScenarioProxyComponent, StepComponent, ScenarioComponent {
    @JsonIgnore
    protected static final String PREFIX = "PIEKER_PROXY_";

    private final String identifier;
    protected String scenarioIdentifier;

    @JsonIgnore
    protected boolean enableLogs = false;
    @JsonIgnore
    protected List<Template> conditionList = new ArrayList<>();

    private final Map<String, List<Template>> stepToConditionMap = new HashMap<>();
    private final Map<String, Boolean> stepToLog = new HashMap<>();

    protected ProxyComponent(String identifier) {
        this.identifier = identifier;
    }

    protected ProxyComponent(String identifier, List<Template> conditionList, boolean enableLogs) {
        this.identifier = identifier;
        this.conditionList = conditionList;
        this.enableLogs = enableLogs;
    }

    public void addStepWithCondition(String stepIdentifier, List<Template> conditionList){
        if (this.stepToConditionMap.containsKey(stepIdentifier)){
            this.stepToConditionMap.get(stepIdentifier).addAll(conditionList);
        } else {
            this.stepToConditionMap.put(stepIdentifier, conditionList);
        }
    }

    public void enableLogging(){
        this.enableLogs = true;
    }

    public abstract T convertToScenarioInstance(String scenarioIdentifier, String stepIdentifier);
}
