package pieker.dsl.model.assertions;

import pieker.dsl.code.Engine;
import pieker.dsl.code.component.StepComponent;

public class TrafficAssert extends Assert{

    public TrafficAssert(String identifier) {
        super(identifier);
    }

    @Override
    public void validate() {
        this.getBoolList().forEach(bool -> {
            bool.validate();
            this.validateValue(bool.getValue());
        });
        this.getEqualsList().forEach(equals -> this.validateValue(equals.getValue()));
        this.getNullList().forEach(nullAssert ->this.validateValue(nullAssert.getValue()));
    }

    private void validateValue(String value){
        // not implemented yet
    }

    @Override
    public void processAssert() {
        StepComponent component = Engine.getCurrentStep().getTestComponentMap().get(this.getIdentifier());
        if (component != null) component.enableLogging();
    }
}
