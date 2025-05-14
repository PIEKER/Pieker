package pieker.dsl.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import pieker.api.Assertion;
import pieker.common.*;
import pieker.dsl.architecture.component.*;

import java.util.*;

@Getter
@Setter
public class Scenario implements ScenarioTestPlan {
    // PMT attributes
    @JsonIgnore
    private final Feature feature;
    @JsonIgnore
    private int line;
    @JsonIgnore
    private int index;
    private final String name;
    private String description;
    @JsonIgnore
    private Step beforeEach;
    @JsonIgnore
    private List<Condition.Entry> entryList = new ArrayList<>();
    @JsonIgnore
    private List<Step> stepList = new ArrayList<>();

    // PIEKER architectural attributes
    List<TrafficContainer> trafficContainerList = new ArrayList<>();
    List<LinkProxy> linkProxyList = new ArrayList<>();
    List<ServiceProxy> serviceProxyList = new ArrayList<>();
    List<DatabaseProxy> databaseProxyList = new ArrayList<>();

    public Scenario(Feature feature, String name){
        this.feature = feature;
        this.name = name.replace(" ", "-");
    }

    public void addStep(Step step) {
        step.setIndex(this.stepList.size());
        this.stepList.add(step);
    }

    public void addEntry(String leftKey, String leftData){
        this.entryList.add(new Condition.Entry(leftKey, leftData));
    }

    @JsonIgnore
    @Override
    public Collection<ScenarioProxyComponent> getProxyComponents() {
        List<ScenarioProxyComponent> scenarioComponents = new ArrayList<>();
        scenarioComponents.addAll(this.linkProxyList);
        scenarioComponents.addAll(this.serviceProxyList);
        scenarioComponents.addAll(this.databaseProxyList);

        return scenarioComponents;
    }
    @JsonIgnore
    @Override
    public Collection<ScenarioTrafficComponent> getTrafficComponents() {
        return new ArrayList<>(this.trafficContainerList);
    }
    @JsonIgnore
    @Override
    public Collection<ScenarioComponent> getComponents() {
        List<ScenarioComponent> scenarioComponents = new ArrayList<>();
        scenarioComponents.addAll(this.linkProxyList);
        scenarioComponents.addAll(this.serviceProxyList);
        scenarioComponents.addAll(this.databaseProxyList);
        scenarioComponents.addAll(this.trafficContainerList);
        return scenarioComponents;
    }

    @Override
    public Collection<String> getStepIds() {
        return stepList.stream().map(Step::getId).toList();
    }

    @JsonIgnore
    @Override
    public TestStep getBeforeEachStep() {
        return this.beforeEach;
    }

    @JsonIgnore
    @Override
    public Collection<TestStep> getTestSteps() {
        return new ArrayList<>(this.stepList);
    }

    @Override
    public Map<String, Long> getStepToDurationMap() {
        Map<String, Long> stepToDurationMap = new HashMap<>();
        this.stepList.forEach(step -> stepToDurationMap.put(step.getId(), (long) (step.getDuration() * 1000)));
        return stepToDurationMap;
    }

    @JsonIgnore
    @Override
    public Map<String, List<Assertion>> getAssertionsMap() {
        Map<String, List<Assertion>> stepToAssertionsMap = new HashMap<>();
        for (Step step : this.stepList) {
            List<Assertion> assertionList = this.beforeEach.getEvaluationList();
            // cast before-each assertions to step assertions
            assertionList.forEach(ass -> ass.setStepId(step.getId()));
            assertionList.addAll(step.getEvaluationList());
            stepToAssertionsMap.put(step.getId(), assertionList);
        }
        return stepToAssertionsMap;
    }

    // used by jackson to generate TestPlan JSON
    @JsonProperty("stepToSequenceMap")
    public Map<String, List<TrafficTemplate>> getStepToSequenceMap() {
        Map<String, List<TrafficTemplate>> stepToSequenceMap = new HashMap<>();
        stepToSequenceMap.put("beforeEach", this.beforeEach.getSequence());
        this.stepList.forEach(step -> stepToSequenceMap.put(step.getId(), step.getSequence()));
        return stepToSequenceMap;
    }

}
