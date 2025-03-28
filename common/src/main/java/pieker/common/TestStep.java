package pieker.common;

import java.util.List;

public interface TestStep {

    String getId();
    List<TrafficTemplate> getSequence();
    List<TrafficTemplate> getEvaluationPreparationSequence();
    int getAssertAfter();

}
