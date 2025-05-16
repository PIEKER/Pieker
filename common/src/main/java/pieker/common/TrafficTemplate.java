package pieker.common;


import java.util.Collection;

public interface TrafficTemplate extends Template{

    String getIdentifier();
    String getTarget();
    boolean isEnableLogs();
    Collection<ConditionTemplate> getConditionList();
    void startTraffic(String[] args);
}
