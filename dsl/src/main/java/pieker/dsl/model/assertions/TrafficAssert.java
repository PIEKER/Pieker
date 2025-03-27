package pieker.dsl.model.assertions;

import lombok.extern.slf4j.Slf4j;
import pieker.common.Evaluation;
import pieker.dsl.code.Engine;
import pieker.dsl.code.component.StepComponent;

import java.util.List;

@Slf4j
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

    @Override
    protected void evaluateBoolNode(Bool bool, String[] args) {
        // todo
    }

    @Override
    protected void evaluateEqualsNode(Equals equals, String[] args) {
        // todo
    }

    @Override
    protected void evaluateNullNode(Null nuLL, String[] args) {
        // todo
    }

    @Override
    public void evaluate(String[] args) {
        log.info("not yet implemented");
    }

    @Override
    public List<Evaluation> getEvaluation() {
        return List.of();
    }
}
