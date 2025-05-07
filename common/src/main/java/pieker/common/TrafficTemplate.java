package pieker.common;


public interface TrafficTemplate extends Template{

    String getTarget();
    boolean isEnableLogs();
    void startTraffic(String[] args);
}
