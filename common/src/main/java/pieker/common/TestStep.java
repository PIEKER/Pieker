package pieker.common;

import java.util.List;

public interface TestStep {

    List<TrafficTemplate> getSequence();
    String getId();

}
