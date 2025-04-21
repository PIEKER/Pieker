package pieker.api.assertions;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import pieker.api.Evaluation;

import java.util.List;

@Slf4j
public class StubAssert extends Assert{
    
    private static final String ASSERT_PLUGIN = "";

    public StubAssert(){
        super(ASSERT_PLUGIN);
    }

    public StubAssert(String identifier) {
        super(ASSERT_PLUGIN, identifier);
    }

    @Override
    public void validate() {
        log.debug("validate StubAssert");
    }

    @Override
    public void processAssert() {
        log.debug("process StubAssert");
    }

    @Override
    protected void evaluateBoolNode(Bool bool) {
        log.debug("evaluate BoolNode in StubAssert");
    }

    @Override
    protected void evaluateEqualsNode(Equals equals) {
        log.debug("evaluate Equals in StubAssert");
    }

    @Override
    protected void evaluateNullNode(Null nuLL) {
        log.debug("evaluate Null in StubAssert");
    }

    @Override
    public void evaluate() {
        log.debug("evaluate StubAssert");
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
        log.debug("no setup required.");
    }
}
