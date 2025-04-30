package pieker.dsl.code.component;

import lombok.Getter;
import lombok.Setter;
import pieker.common.ConditionTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Setter
@Getter
public class ServiceProxy extends ProxyComponent<ServiceProxy> {

    private final String service;
    private List<String> urlList = new ArrayList<>();
    private Map<String, List<String>> stepToUrlList = new HashMap<>();

    public ServiceProxy(String identifier) {
        super(identifier);
        this.service = identifier;
    }

    public ServiceProxy(String identifier, String service) {
        super(identifier);
        this.service = service;
    }

    public ServiceProxy(String identifier, String service, List<ConditionTemplate> conditionList, boolean enableLogs) {
        super(identifier, conditionList, enableLogs);
        this.service = service;
    }

    public boolean proxyAll(String step){
        if (this.stepToUrlList.containsKey(step)){
            return this.stepToUrlList.get(step).isEmpty();
        } else {
            return true;
        }
    }

    @Override
    public StepComponent copy() {
        return new ServiceProxy(this.getIdentifier(), this.service, new ArrayList<>(this.conditionList), this.enableLogs);
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

    public void addStepWithCondition(String stepIdentifier, List<ConditionTemplate> conditionList, List<String> urlList){
        if (this.stepToUrlList.containsKey(stepIdentifier)){
            this.stepToUrlList.get(stepIdentifier).addAll(urlList);
        } else {
            this.stepToUrlList.put(stepIdentifier, urlList);
        }
        this.addStepWithCondition(stepIdentifier, conditionList);
    }

    @Override
    public ServiceProxy convertToScenarioInstance(String scenarioIdentifier, String stepIdentifier){
        this.getStepToLog().put(stepIdentifier, this.enableLogs);
        this.scenarioIdentifier = scenarioIdentifier;
        this.addStepWithCondition(stepIdentifier, this.conditionList, this.urlList);
        return this;
    }

    @Override
    public String getSource() {
        return "";
    }

    @Override
    public String getTarget() {
        return this.service;
    }

    @Override
    public String getName() {
        return PREFIX + this.getIdentifier().replace("-", "_");
    }
}
