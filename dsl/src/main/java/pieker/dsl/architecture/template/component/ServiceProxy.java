package pieker.dsl.architecture.template.component;

import lombok.Getter;
import lombok.Setter;
import pieker.common.ConditionTemplate;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class ServiceProxy extends ProxyComponent<ServiceProxy> {

    private final String service;

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

    @Override
    public StepComponent copy() {
        return new ServiceProxy(this.getIdentifier(), this.service, new ArrayList<>(this.conditionList), this.enableLogs);
    }

    @Override
    public ServiceProxy convertToScenarioInstance(String scenarioIdentifier, String stepIdentifier){
        this.getStepToLog().put(stepIdentifier, this.enableLogs);
        this.scenarioIdentifier = scenarioIdentifier;
        this.addStepWithCondition(stepIdentifier, this.conditionList);
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
