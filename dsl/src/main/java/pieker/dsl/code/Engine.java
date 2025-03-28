package pieker.dsl.code;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import pieker.dsl.PiekerDslException;
import pieker.dsl.code.component.*;
import pieker.dsl.code.exception.PiekerProcessingException;
import pieker.dsl.code.preprocessor.Converter;
import pieker.dsl.code.preprocessor.FileManager;
import pieker.dsl.code.preprocessor.Validator;
import pieker.dsl.model.*;

import java.util.*;

@Slf4j
public class Engine {

    @Getter
    private static Step currentStep = new Step(null, null, "PLACEHOLDER_STEP");
    private static FileManager fileManager;

    private Engine(){}

    public static void run(Feature feature){
        validate(feature);

        for (Scenario scenario : feature.getScenarioList()){
            log.debug("processing Scenario {}", scenario.getName());
            processTestScenario(scenario);
            createTestComponents(scenario);
            log.info("finished processing Scenario {}", scenario.getName());
        }
    }

    public static void validate(Feature feature){
        fileManager = new FileManager(feature.getResourceDirectory());
        log.debug("starting validation of Feature {}", feature.getName());
        Converter.run(feature);
        Validator.validate(feature);
        log.info("finished validation of Feature {}", feature.getName());
    }

    /*
        -- PROCESS
     */
    private static void processTestScenario(Scenario scenario){
        if (scenario.getBeforeEach() != null){
            processTestStep(scenario.getBeforeEach());
        }
        scenario.getStepList().forEach(Engine::processTestStep);
    }

    private static void processTestStep(Step step){

        if (!step.isBeforeEach()){
            step.migrateBeforeEach(step.getScenario().getBeforeEach());
        }

        currentStep = step;
        log.debug("processing Step {}", currentStep.getName());

        Given given = step.getGiven();
        if (given != null) given.processEntryList();
        else throw new PiekerProcessingException("invalid step detected: '" + step.getName() + "' has no given node.");


        When when = step.getWhen();
        if (when != null) when.processEntryList();

        Then then = step.getThen();
        if (then != null) then.processAssertList();
    }

    /*
        -- ARCHITECTURE
     */
    private static void createTestComponents(Scenario scenario) {
        log.debug("creating architecture draft for Scenario {}", scenario.getName());
        createSupervisorSteps(scenario);
        createTrafficContainerList(scenario);
        createLinkProxies(scenario);
        createServiceProxies(scenario);
        createDatabaseProxies(scenario);
        log.debug("finished architecture draft for Scenario {}", scenario.getName());
    }

    private static void createServiceProxies(Scenario scenario) {
        scenario.setServiceProxyList(createComponentList(scenario, ServiceProxy.class));
    }

    private static void createDatabaseProxies(Scenario scenario) {
        scenario.setDatabaseProxyList(createComponentList(scenario, DatabaseProxy.class));
    }

    private static void createLinkProxies(Scenario scenario) {
        scenario.setLinkProxyList(createComponentList(scenario, LinkProxy.class));
    }

    private static void createSupervisorSteps(Scenario scenario) {
        List<SupervisorStep> supervisorStepList = new ArrayList<>();
        scenario.getStepList().forEach(step -> {
            SupervisorStep supervisorStep = step.createSupervisorStep();
            supervisorStep.setScenarioId(scenario.getName());
            supervisorStepList.add(supervisorStep);
        });

        scenario.setSupervisorStepList(supervisorStepList);
    }


    private static void createTrafficContainerList(Scenario scenario) {
        Map<String, TrafficContainer> trafficContainerMap = new HashMap<>();

        scenario.getStepList().forEach(step -> createTrafficContainerFromStep(
                step,
                trafficContainerMap,
                scenario
        ));
    }

    private static void createTrafficContainerFromStep(Step step, Map<String, TrafficContainer> trafficContainerMap, Scenario scenario){
        step.createTrafficContainer();
        for (TrafficContainer trafficContainer : step.getTrafficContainerMap().values()){
            if(!trafficContainerMap.containsKey(trafficContainer.getIdentifier())){
                trafficContainerMap.put(
                        trafficContainer.getIdentifier(),
                        trafficContainer.convertToScenarioInstance(scenario.getName(), step.getId())
                );
            } else {
                trafficContainerMap.get(trafficContainer.getIdentifier()).addStepWithTraffic(
                        step.getId(),
                        trafficContainer.getTrafficList()
                );
            }
        }
        scenario.setTrafficContainerList(trafficContainerMap.values().stream().toList());
    }

    private static <T extends ProxyComponent<T>> List<T> createComponentList(Scenario scenario, Class<T> type) {
        Map<String, T> typeMap = new HashMap<>();

        scenario.getStepList().forEach(step -> createComponentListFromStep(
                step,
                typeMap,
                scenario,
                type
        ));

        return typeMap.values().stream().toList();
    }

    private static <T extends ProxyComponent<T>> void createComponentListFromStep(Step step, Map<String, T> typeMap, Scenario scenario, Class<T> type){
        List<T> typeList = step.filterTestComponentsByInstance(type);
        for (T typeElem: typeList ){

            if(!typeMap.containsKey(typeElem.getIdentifier())){
                typeMap.put(
                        typeElem.getIdentifier(),
                        typeElem.convertToScenarioInstance(scenario.getName(), step.getId())
                );
            } else {
                T scenarioComponent = typeMap.get(typeElem.getIdentifier());
                scenarioComponent.getStepToLog().put(step.getId(), typeElem.isEnableLogs());
                scenarioComponent.addStepWithCondition(step.getId(),typeElem.getConditionList());
            }
        }
    }

     /*
        -- Getter
      */
    public static FileManager getFileManager() {
        if (fileManager!= null){
            return fileManager;
        }
        throw new PiekerDslException("invalid call on fileManager. No feature bound.");
    }
}
