package pieker.dsl.code.component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pieker.common.ScenarioComponent;
import pieker.common.ScenarioTrafficComponent;
import pieker.common.TrafficTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public abstract class TrafficComponent <T extends TrafficComponent<T>> implements ScenarioTrafficComponent, ScenarioComponent {

    @JsonIgnore
    protected static final String PREFIX = "PIEKER_TRAFFIC_";

    private final String identifier;
    protected String scenarioIdentifier;

    @JsonIgnore
    protected List<TrafficTemplate> trafficList = new ArrayList<>();
    protected Map<String,List<TrafficTemplate>> stepToTrafficMap = new HashMap<>();

    protected TrafficComponent(String identifier) {
        this.identifier = identifier;
    }

    public void addStepWithTraffic(String stepIdentifier, List<TrafficTemplate> trafficList){

        if (this.stepToTrafficMap.containsKey(stepIdentifier)){
            this.stepToTrafficMap.get(stepIdentifier).addAll(trafficList);
        } else {
            this.stepToTrafficMap.put(stepIdentifier, trafficList);
        }
    }

    public abstract T convertToScenarioInstance(String scenarioIdentifier, String stepIdentifier);
}
