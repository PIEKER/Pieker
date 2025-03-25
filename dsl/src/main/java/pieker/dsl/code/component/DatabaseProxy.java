package pieker.dsl.code.component;

import lombok.Setter;
import pieker.common.Template;

import java.util.ArrayList;
import java.util.List;

@Setter
public class DatabaseProxy extends ProxyComponent<DatabaseProxy> {


    public DatabaseProxy(String identifier) {
        super(identifier);
    }

    public DatabaseProxy(String identifier, List<Template> conditionList, boolean enableLogs) {
        super(identifier, conditionList, enableLogs);
    }

    @Override
    public void addCondition(Template condition) {
        this.conditionList.add(condition);
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
        return PREFIX + this.getTarget();
    }
}
