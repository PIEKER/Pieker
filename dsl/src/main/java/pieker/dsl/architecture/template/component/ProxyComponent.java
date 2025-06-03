package pieker.dsl.architecture.template.component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import pieker.common.ConditionTemplate;
import pieker.common.ScenarioComponent;
import pieker.common.ScenarioProxyComponent;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
public abstract class ProxyComponent<T extends ProxyComponent<T>> implements ScenarioProxyComponent, StepComponent, ScenarioComponent {
    @JsonIgnore
    protected static final String PREFIX = "PIEKER_PROXY_";

    private final String identifier;
    protected String scenarioIdentifier;

    @JsonIgnore
    protected boolean enableLogs = false;
    @JsonIgnore
    protected List<ConditionTemplate> conditionList = new ArrayList<>();

    private final Map<String, List<ConditionTemplate>> stepToConditionMap = new HashMap<>();
    private final Map<String, Boolean> stepToLog = new HashMap<>();

    protected ProxyComponent(String identifier) {
        this.identifier = identifier;
    }

    protected ProxyComponent(String identifier, List<ConditionTemplate> conditionList, boolean enableLogs) {
        this.identifier = identifier;
        this.conditionList = conditionList;
        this.enableLogs = enableLogs;
    }

    @Override
    public void addCondition(ConditionTemplate condition) {
        List<ConditionTemplate> newConditionList = new ArrayList<>();
        AtomicBoolean updated = new AtomicBoolean(false);
        this.conditionList.forEach(t -> {
            if (t.getName().equals(condition.getName())){
                newConditionList.add(condition);
                updated.set(true);
            } else {
                newConditionList.add(t);
            }
        });

        if(!updated.get()) newConditionList.add(condition);

        this.conditionList = newConditionList;
    }

    public void addStepWithCondition(String stepIdentifier, List<ConditionTemplate> conditionList){
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
