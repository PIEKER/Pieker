package pieker.dsl.code.component;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
public class TrafficContainer extends TrafficComponent<TrafficContainer> {

    public TrafficContainer(String target) {
        super(target);
    }

    public void addTraffic(Traffic traffic){
        this.trafficList.add(traffic);
    }

    public TrafficContainer convertToScenarioInstance(String scenarioIdentifier, String stepIdentifier){
        this.addStepWithTraffic(stepIdentifier, this.trafficList);
        this.scenarioIdentifier = scenarioIdentifier;

        return this;
    }

    @Override
    public String getSource() {
        return "";
    }

    @Override
    public String getTarget() {
        return this.getIdentifier();
    }

    @Override
    public String getName() {
        return PREFIX + this.getIdentifier().replace("-", "_");
    }
}
