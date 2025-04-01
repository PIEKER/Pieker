package pieker.dsl.code.component;

import lombok.Getter;
import lombok.Setter;
import pieker.common.Template;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class LinkProxy extends ProxyComponent<LinkProxy> {

    private final String hostA;
    private final String hostB;

    public LinkProxy(String identifier, String hostA, String hostB) {
        super(identifier);
        this.hostA = hostA;
        this.hostB = hostB;
    }

    public LinkProxy(String identifier, String hostA, String hostB, List<Template> conditionList, boolean enableLogs) {
        super(identifier, conditionList, enableLogs);
        this.hostA = hostA;
        this.hostB = hostB;
    }

    @Override
    public void addCondition(Template condition) {
        this.conditionList.add(condition);
    }

    @Override
    public StepComponent copy() {
        return new LinkProxy(this.getIdentifier(), this.hostA, this.hostB, new ArrayList<>(this.conditionList), this.enableLogs);
    }

    @Override
    public LinkProxy convertToScenarioInstance(String scenarioIdentifier, String stepIdentifier){
        this.getStepToLog().put(stepIdentifier, this.enableLogs);
        this.addStepWithCondition(stepIdentifier, this.conditionList);
        this.scenarioIdentifier = scenarioIdentifier;
        return this;
    }

    @Override
    public String getSource() {
        return this.hostA;
    }

    @Override
    public String getTarget() {
        return this.hostB;
    }

    @Override
    public String getName() {
        return PREFIX + this.getIdentifier().replace("-", "_");
    }

}

