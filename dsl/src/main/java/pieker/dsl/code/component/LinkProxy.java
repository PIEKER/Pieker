package pieker.dsl.code.component;

import lombok.Getter;
import lombok.Setter;
import pieker.common.ConditionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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

    public LinkProxy(String identifier, String hostA, String hostB, List<ConditionTemplate> conditionList, boolean enableLogs) {
        super(identifier, conditionList, enableLogs);
        this.hostA = hostA;
        this.hostB = hostB;
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

