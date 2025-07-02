package pieker.api.assertions;

import lombok.extern.slf4j.Slf4j;
import pieker.api.Assertion;
import pieker.api.Evaluation;

import java.util.List;

@Slf4j
public class StubAssert extends Assert{
    
    private static final String ASSERT_PLUGIN = "";
    private final List<Evaluation> evaluationList = List.of(new StubEvaluation(), new StubEvaluation());

    public StubAssert(){
        super(ASSERT_PLUGIN);
    }

    public StubAssert(String identifier) {
        super(ASSERT_PLUGIN, identifier);
    }

    private StubAssert(StubAssert stubAssert){
        super(stubAssert);
    }

    @Override
    public void validate(int line) {
        log.debug("validate StubAssert {}", this.identifier);
    }

    @Override
    public void processAssert() {
        log.debug("process StubAssert {}", this.identifier);
    }

    @Override
    protected void evaluateBoolNode(Bool bool) {
        log.debug("evaluate BoolNode in StubAssert {}", this.identifier);
    }

    @Override
    protected void evaluateEqualsNode(Equals equals) {
        log.debug("evaluate Equals in StubAssert {}", this.identifier);
    }

    @Override
    protected void evaluateNullNode(Null nuLL) {
        log.debug("evaluate Null in StubAssert {}", this.identifier);
    }

    @Override
    public void evaluate() {
        log.debug("evaluate StubAssert {}", this.identifier);
        this.evaluationList.forEach(evaluation -> evaluation.evaluate("no-args"));
    }

    @Override
    public List<Evaluation> getEvaluation() {
        return this.evaluationList;
    }

    @Override
    public Assertion copy() {
        return new StubAssert(this);
    }

    @Override
    public boolean requiresConnectionParam(){
        return false;
    }

    @Override
    public void setConnectionParam(String gatewayUrl) {
        log.debug("no setup required.");
    }

    @Override
    public void prepare() {
        log.debug("no preparation required.");
    }

    private static class StubEvaluation implements Evaluation{

        private boolean evaluated = false;

        @Override
        public String getAssertType() {
            return "Stub";
        }
        @Override
        public String getAssertExpression() {
            return "no expression possible on stub";
        }

        @Override
        public boolean isSuccess() {
            return evaluated;
        }

        @Override
        public String getErrorMessage() {
            return "no error possible on stub";
        }

        @Override
        public void setErrorMessage(String errorMessage){
            //no action required
        }

        @Override
        public Evaluation copy() {
            return new StubEvaluation();
        }

        @Override
        public void evaluate(String arg) {
            this.evaluated = true;
        }
    }
}
