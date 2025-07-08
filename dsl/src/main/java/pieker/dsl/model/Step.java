package pieker.dsl.model;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import pieker.api.Assertion;
import pieker.common.ConditionTemplate;
import pieker.common.TestStep;
import pieker.common.TrafficTemplate;
import pieker.dsl.architecture.template.component.StepComponent;
import pieker.dsl.architecture.template.traffic.OrchestratorTraffic;
import pieker.dsl.architecture.template.traffic.Traffic;
import pieker.dsl.architecture.template.component.TrafficContainer;
import pieker.dsl.util.FeatureUtil;
import pieker.dsl.util.comparators.STComparator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

@Getter
@Setter
@Slf4j
public class Step implements TestStep {
    // -- PIEKER model attributes
    private final Feature feature;
    private final Scenario scenario;
    private int line;

    private int index;

    private final String name;
    private String description;

    private Given given;
    private When when;
    private Then then;

    private boolean beforeEach = false;
    private float duration = Float.parseFloat(System.getProperty("testDurationDefault", "30.0F"));

    // -- PIEKER architecture attributes
    private String id;
    private Map<String, StepComponent> testComponentMap = new HashMap<>();
    private Map<String, TrafficContainer> trafficContainerMap = new HashMap<>();
    private List<OrchestratorTraffic> orchestratorTrafficList = new ArrayList<>();

    public Step(Feature feature, Scenario scenario, String name){
        this.feature = feature;
        this.scenario = scenario;
        this.name = name.replace(" ", "_");
        this.id = this.name.isEmpty() ? UUID.randomUUID().toString().replace("-", "_") : this.name;
    }

    public Step(Feature feature, Scenario scenario){
        this.feature = feature;
        this.scenario = scenario;
        this.name = "beforeEach";
        this.id = name;
    }

    // -- update PIEKER architecture attributes
    public void addStepComponent(String identifier, StepComponent stepComponent){
        this.testComponentMap.put(identifier, stepComponent);
    }

    public void addConditionTemplate(String[] identifiers, ConditionTemplate template){
        for (String identifier : identifiers){
            this.testComponentMap.get(identifier).addCondition(template);
        }
    }

    public <T> List<T> filterTestComponentsByInstance(Class<T> type){
        return this.testComponentMap.values().stream().filter(type::isInstance).map(type::cast).toList();
    }

    public void createTrafficContainer(){
        List<Traffic> trafficList = this.filterTestComponentsByInstance(Traffic.class);
        for (Traffic traffic: trafficList){
            if (traffic instanceof OrchestratorTraffic){
                continue;
            }
            String target = traffic.getTrafficType().getTarget();
            if (!this.trafficContainerMap.containsKey(target)){
                TrafficContainer trafficContainer = new TrafficContainer(target);
                trafficContainer.addTraffic(traffic);
                this.trafficContainerMap.put(target, trafficContainer);
            } else {
                this.trafficContainerMap.get(target).addTraffic(traffic);
            }
        }
    }

    public void createOrchestratorTraffic(){
        List<OrchestratorTraffic> trafficList = new ArrayList<>(this.filterTestComponentsByInstance(OrchestratorTraffic.class));
        trafficList.sort(new STComparator());
        this.orchestratorTrafficList.addAll(trafficList);
    }

    public void migrateBeforeEach(Step beforeEach) {
        if (beforeEach == null) return;
        beforeEach.testComponentMap.forEach((k,v) -> this.testComponentMap.put(k, v.copy()));
        if (this.then == null){
            this.then = new Then(this);
        }
        this.then.updateEvaluationList(beforeEach.getEvaluationList().stream().map(Assertion::copy).toList());
    }

    protected List<Assertion> getEvaluationList(){
        return (this.then != null) ? this.then.getEvaluationList() : new ArrayList<>();
    }

    @Override
    public List<TrafficTemplate> getSequence() {
        return new ArrayList<>(this.orchestratorTrafficList);
    }

    @Override
    public void finish(){
        this.getEvaluationList().forEach(Assertion::prepare);
        this.getSequence().forEach(traffic -> this.dumpTrafficLogs(traffic.getIdentifier(), traffic.getLogs()));
    }

    private void dumpTrafficLogs(String traffic, Collection<String> logs){
        String outputDir = System.getProperty("genDir", "./gen");
        Path path = Paths.get(outputDir, this.scenario.getName(), "logs", this.name + "_" + FeatureUtil.createCodeSafeString(traffic) + ".log");
        try {
            Files.write(path, logs, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            log.debug("dumping logs for traffic {}", traffic);
        } catch (IOException e) {
            log.error("Unable to dump traffic logs for {} due to IOException: {}", traffic, e.getMessage());
        }
    }
}
