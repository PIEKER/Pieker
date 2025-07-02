package pieker.common;

import java.util.List;

public interface TestStep {

    /**
     * @return unique id
     */
    String getId();

    /**
     * @return action sequence, ordered by user input
     */
    List<TrafficTemplate> getSequence();

    /**
     * Performs any action required at the end of a test-step. Possibilities are cleanup or else.
     */
    void finish();
}
