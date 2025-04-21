package pieker.log.eval;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import pieker.api.Evaluation;
import pieker.api.assertions.Assert;
import pieker.api.assertions.Bool;
import pieker.api.assertions.Equals;
import pieker.api.assertions.Null;

import java.util.List;

@Slf4j
public class LogAssert extends Assert {

    private static final String ASSERT_PLUGIN = "Log";

    public LogAssert(){
        super(ASSERT_PLUGIN);
    }

    public LogAssert(String identifier) {
        super(ASSERT_PLUGIN, identifier);
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
    protected void evaluateBoolNode(Bool bool) {
        log.info("evaluate BoolNode in LogEvaluation");
    }

    @Override
    protected void evaluateEqualsNode(Equals equals) {
        log.info("evaluate Equals in LogEvaluation");
    }

    @Override
    protected void evaluateNullNode(Null nuLL) {
        log.info("evaluate Null in LogEvaluation");
    }

    @Override
    public void evaluate() {
        log.info("evaluate LogEvaluation");
    }

    @Override
    public List<Evaluation> getEvaluation() {
        return List.of();
    }

    @Override
    public boolean requiresConnectionParam(){
        return false;
    }

    @Override
    public void setupConnectionParam(JSONObject cpJson) {
        log.info("setup ConnectionParam in LogEvaluation");
    }
}
