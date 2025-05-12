package pieker.dsl.architecture.template.traffic;

import org.apache.velocity.VelocityContext;

public interface TrafficType {
    void addContextVariable(VelocityContext context);
    void translateParameters();

    String getTarget();
    String sendTraffic(String[] args);
}
