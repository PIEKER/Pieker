package pieker.evaluator;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import pieker.api.Assertions;
import pieker.api.assertions.StubAssert;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class EvaluatorTest {

    List<Assertions> assertionList = new ArrayList<>();

    @Test
    void testEvaluator() {
        Evaluator evaluator = new Evaluator();
        createAssertionList();
        evaluator.run(this.assertionList, System.currentTimeMillis(), 60);
        this.assertionList.forEach(ass -> {
            ass.getEvaluation().forEach(evaluation -> assertTrue(evaluation.isSuccess()));
        });
    }

    void createAssertionList(){
        StubAssert stubAssert = new StubAssert("Assert A");
        this.assertionList.add(stubAssert);
        StubAssert stubAssert2 = new StubAssert("Assert B");
        this.assertionList.add(stubAssert2);
        StubAssert stubAssert3 = new StubAssert("Assert C");
        this.assertionList.add(stubAssert3);
        StubAssert stubAssert4 = new StubAssert("Assert D");
        this.assertionList.add(stubAssert4);
    }

}
