package pieker.dsl.architecture.component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pieker.common.TestStep;
import pieker.common.TrafficTemplate;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class SupervisorStep implements TestStep {

    private String scenarioId;
    private final String id;
    private List<SupervisorTraffic> trafficList = new ArrayList<>();

    public SupervisorStep(String id) {
        this.id = id;
    }

    @JsonIgnore
    public boolean isEmpty(){
        return this.trafficList.isEmpty();
    }

    @JsonIgnore
    @Override
    public List<TrafficTemplate> getSequence() {
        return List.of((TrafficTemplate) this.trafficList);
    }
}
