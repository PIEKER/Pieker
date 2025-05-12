package pieker.dsl.architecture.component;

import pieker.common.ConditionTemplate;

public interface StepComponent {
    void addCondition(ConditionTemplate condition);
    StepComponent copy();
    void enableLogging();
}
