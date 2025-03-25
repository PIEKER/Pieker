package pieker.dsl.model;

import lombok.Getter;
import lombok.Setter;
import pieker.common.ScenarioTestPlan;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Feature {

    private final String fileName;
    private final String resourceDirectory;
    private int line;
    private String name;
    private String description;
    private Background background;
    private List<Scenario> scenarioList = new ArrayList<>();

    public Feature(String fileName, String resourceDirectory){
        this.fileName = fileName;
        this.resourceDirectory = resourceDirectory;
    }

    public void addScenario(Scenario scenario){
        scenario.setIndex(this.scenarioList.size());
        this.scenarioList.add(scenario);
    }

    public List<ScenarioTestPlan> getScenarioTestPlanList(){
        return new ArrayList<>(this.scenarioList);
    }
}
