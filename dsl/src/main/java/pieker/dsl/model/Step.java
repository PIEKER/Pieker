package pieker.dsl.model;

import lombok.Getter;
import lombok.Setter;
import pieker.api.Assertions;
import pieker.common.ConditionTemplate;
import pieker.common.TestStep;
import pieker.common.TrafficTemplate;
import pieker.dsl.architecture.component.*;
import pieker.dsl.util.comparators.STComparator;

import java.util.*;

@Getter
@Setter
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
    private float duration = 4.0F;

    // -- PIEKER architecture attributes
    private String id;
    private Map<String, StepComponent> testComponentMap = new HashMap<>();
    private Map<String, TrafficContainer> trafficContainerMap = new HashMap<>();
    private List<SupervisorTraffic> supervisorTrafficList = new ArrayList<>();

    public Step(Feature feature, Scenario scenario, String name){
        this.feature = feature;
        this.scenario = scenario;
        this.name = name.replace(" ", "_");
        this.id = this.name + "_" + UUID.randomUUID().toString().replace("-", "_");
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
            if (traffic instanceof SupervisorTraffic){
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

    public void createSupervisorTraffic(){
        List<SupervisorTraffic> trafficList = new ArrayList<>(this.filterTestComponentsByInstance(SupervisorTraffic.class));
        trafficList.sort(new STComparator());
        this.supervisorTrafficList.addAll(trafficList);
    }

    public void migrateBeforeEach(Step beforeEach) {
        if (beforeEach == null) return;
        beforeEach.testComponentMap.forEach((k,v) -> this.testComponentMap.put(k, v.copy()));
    }

    protected List<Assertions> getEvaluationList(){
        return (this.then != null) ? this.then.getEvaluationList() : new ArrayList<>();
    }

    @Override
    public List<TrafficTemplate> getSequence() {
        return new ArrayList<>(this.supervisorTrafficList);
    }
}
