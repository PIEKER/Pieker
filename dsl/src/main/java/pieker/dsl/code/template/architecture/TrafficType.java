package pieker.dsl.code.template.architecture;

import org.apache.velocity.VelocityContext;

public interface TrafficType {
    String sendTraffic(String[] args);

    String getTarget();
    void addContextVariable(VelocityContext context);
    void translateParameters();
}
