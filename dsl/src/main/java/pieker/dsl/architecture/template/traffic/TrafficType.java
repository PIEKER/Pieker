package pieker.dsl.architecture.template.traffic;

import org.apache.velocity.VelocityContext;
import pieker.common.connection.ResponseTuple;

public interface TrafficType {
    void addContextVariable(VelocityContext context);
    void translateParameters();

    String getTarget();
    ResponseTuple sendTraffic(String[] args);
}
