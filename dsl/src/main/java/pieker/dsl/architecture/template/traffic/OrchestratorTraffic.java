package pieker.dsl.architecture.template.traffic;

import pieker.common.ConditionTemplate;

import java.util.List;

public class OrchestratorTraffic extends Traffic{

    public OrchestratorTraffic(String identifier, TrafficType traffic) {
        super(identifier, traffic);
    }

    public OrchestratorTraffic(String identifier, TrafficType trafficType, List<ConditionTemplate> conditionList, boolean enableLogs) {
        super(identifier, trafficType, conditionList, enableLogs);
    }

    @Override
    public OrchestratorTraffic copy(){
        return new OrchestratorTraffic(this.getIdentifier(), this.getTrafficType(), this.getConditionList(), this.isEnableLogs());
    }
}