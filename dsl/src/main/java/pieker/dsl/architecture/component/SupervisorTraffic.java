package pieker.dsl.architecture.component;

import pieker.dsl.architecture.template.traffic.TrafficType;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SupervisorTraffic extends Traffic{

    public SupervisorTraffic(String identifier, TrafficType traffic) {
        super(identifier, traffic);
    }

}