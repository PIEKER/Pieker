package pieker.dsl.architecture.template.traffic;

import org.apache.velocity.VelocityContext;

public interface TrafficType {
    String sendTraffic(String[] args);

    String getTarget();
    void addContextVariable(VelocityContext context);
    void translateParameters();
}
