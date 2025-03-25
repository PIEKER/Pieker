package pieker.dsl.code.component;

import pieker.common.Template;

public interface StepComponent {
    void addCondition(Template condition);
    StepComponent copy();
    void enableLogging();
}
