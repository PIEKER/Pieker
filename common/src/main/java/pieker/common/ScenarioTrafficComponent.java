package pieker.common;

import java.util.List;
import java.util.Map;

public interface ScenarioTrafficComponent extends ScenarioComponent{

    Map<String, List<TrafficTemplate>> getStepToTrafficMap();

}
