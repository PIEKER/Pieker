package pieker.dsl.architecture.component;

import lombok.Setter;
import pieker.common.ConditionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Setter
public class DatabaseProxy extends ProxyComponent<DatabaseProxy> {


    public DatabaseProxy(String identifier) {
        super(identifier);
    }

    public DatabaseProxy(String identifier, List<ConditionTemplate> conditionList, boolean enableLogs) {
        super(identifier, conditionList, enableLogs);
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
        return new DatabaseProxy(this.getIdentifier(), new ArrayList<>(this.conditionList), this.enableLogs);
    }

    @Override
    public DatabaseProxy convertToScenarioInstance(String scenarioIdentifier, String stepIdentifier) {
        this.getStepToLog().put(stepIdentifier, this.enableLogs);
        this.addStepWithCondition(stepIdentifier, this.conditionList);
        this.scenarioIdentifier = scenarioIdentifier;
        return this;
    }

    @Override
    public String getSource() {
        return "";
    }

    @Override
    public String getTarget() {
        return this.getIdentifier();
    }

    @Override
    public String getName() {
        return PREFIX + this.getTarget().replace("-", "_");
    }
}
