package pieker.log.eval;

import lombok.extern.slf4j.Slf4j;
import pieker.api.Evaluation;
import pieker.api.assertions.Assert;
import pieker.api.assertions.Bool;
import pieker.api.assertions.Equals;
import pieker.api.assertions.Null;

import java.util.List;

@Slf4j
public class LogEvaluation extends Assert {

    public LogEvaluation(){
        super("LogEvaluation");
    }

    public LogEvaluation(String identifier) {
        super(identifier);
    }

    @Override
    public void validate() {
        log.info("validate LogEvaluation");
    }

    @Override
    public void processAssert() {
        log.info("process LogEvaluation");
    }

    @Override
    protected void evaluateBoolNode(Bool bool, String[] args) {
        log.info("evaluate BoolNode in LogEvaluation");
    }

    @Override
    protected void evaluateEqualsNode(Equals equals, String[] args) {
        log.info("evaluate Equals in LogEvaluation");
    }

    @Override
    protected void evaluateNullNode(Null nuLL, String[] args) {
        log.info("evaluate Null in LogEvaluation");
    }

    @Override
    public void evaluate(String[] args) {

    }

    @Override
    public List<Evaluation> getEvaluation() {
        return List.of();
    }
}
