package pieker.dsl.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import pieker.api.Assertion;
import pieker.api.Evaluation;
import pieker.common.*;
import pieker.common.dto.AssertionDto;
import pieker.common.dto.EvaluationDto;
import pieker.common.dto.RunDto;
import pieker.common.dto.StepDto;
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
    private List<Behavior.Entry> entryList = new ArrayList<>();
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
        this.entryList.add(new Behavior.Entry(leftKey, leftData));
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

    @Override
    public RunDto createTestRunDto() {
        Map<String, List<Assertion>> stepToAssertionsMap = this.getAssertionsMap();
        Map<String, List<AssertionDto>> stepToAssertionDtoMap = new HashMap<>();

        //build evaluation DTOs
        for (Step step : this.stepList) {
            for (Assertion ass : stepToAssertionsMap.get(step.getId())) {
                AssertionDto assDto = AssertionDto.builder()
                        .identifier(ass.getIdentifier())
                        .evaluations(new ArrayList<>())
                        .build();
                for (Evaluation ev: ass.getEvaluation()){
                    assDto.setAssertExpression(ev.getAssertExpression());
                    assDto.setAssertType(ev.getAssertType());
                    EvaluationDto evDto = EvaluationDto.builder()
                            .success(ev.isSuccess())
                            .errorMessage(ev.getErrorMessage())
                            .build();
                    assDto.getEvaluations().add(evDto);
                }
                if(!stepToAssertionDtoMap.containsKey(step.getId())){
                    stepToAssertionDtoMap.put(step.getId(), List.of(assDto));
                } else {
                    stepToAssertionDtoMap.get(step.getId()).add(assDto);
                }
            }
        }

        //create RunDto
        return RunDto.builder()
                .scenarioName(this.name)
                .steps(this.stepList.stream().map(step ->
                    StepDto.builder()
                            .name(step.getName())
                            .identifier(step.getId())
                            .assertions(stepToAssertionDtoMap.getOrDefault(step.getId(), new ArrayList<>()))
                            .build()
                    ).toList())
                .build();
    }

    // used by jackson to generate TestPlan JSON
    @JsonProperty("stepToSequenceMap")
    public Map<String, List<TrafficTemplate>> getStepToSequenceMap() {
        Map<String, List<TrafficTemplate>> stepToSequenceMap = new HashMap<>();
        if (this.beforeEach != null) {
            stepToSequenceMap.put("beforeEach", this.beforeEach.getSequence());
        }
        this.stepList.forEach(step -> stepToSequenceMap.put(step.getId(), step.getSequence()));
        return stepToSequenceMap;
    }

}
