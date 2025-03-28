package pieker.dsl.code.component;

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
    private final int assertAfter;
    private List<SupervisorTraffic> trafficList = new ArrayList<>();
    private List<SupervisorTraffic> evaluationPreparationList = new ArrayList<>();

    public SupervisorStep(String id, int assertAfter) {
        this.id = id;
        this.assertAfter = assertAfter;
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

    @JsonIgnore
    @Override
    public List<TrafficTemplate> getEvaluationPreparationSequence() {
        return List.of((TrafficTemplate) this.evaluationPreparationList);
    }
}
