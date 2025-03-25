package pieker.common;

import java.util.List;
import java.util.Map;

public interface ScenarioProxyComponent extends ScenarioComponent{

    Map<String, List<Template>> getStepToConditionMap();

    Map<String, Boolean> getStepToLog();
}
