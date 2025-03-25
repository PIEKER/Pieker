package pieker.common;


public interface TrafficTemplate extends Template{

    String getIdentifier();
    boolean isEnableLogs();
    void startTraffic(String[] args);
}
