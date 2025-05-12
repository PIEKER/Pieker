package pieker.dsl.architecture.component;

import pieker.dsl.architecture.template.traffic.TrafficType;

public class SupervisorTraffic extends Traffic{

    public SupervisorTraffic(String identifier, TrafficType traffic) {
        super(identifier, traffic);
    }

}