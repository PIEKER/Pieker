package pieker.generators.code.step;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import pieker.common.ScenarioProxyComponent;
import pieker.common.ScenarioTestPlan;
import pieker.common.ScenarioTrafficComponent;
import pieker.common.TrafficTemplate;
import pieker.dsl.architecture.template.component.DatabaseProxy;
import pieker.generators.code.CodeGenerationException;
import pieker.generators.code.VelocityTemplateProcessor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Slf4j
public class StepGenerator {

    private static final String PROXY_HTTP_TEMPLATE_FILE = "proxy/proxyHTTP.vm";
    private static final String PROXY_DB_TEMPLATE_FILE = "proxy/proxyDB.vm";
    private static final String TRAFFIC_THREAD_TEMPLATE_FILE = "traffic/thread.vm";
    private static final String TRAFFIC_CONTAINER_TEMPLATE_FILE = "traffic/trafficContainer.vm";
    private static final String DEFAULT_FILENAME = "Default";
    private static final String PROJECT_ROOT = System.getProperty("projectRoot");
    private static final String OUTPUT_DIR = PROJECT_ROOT + System.getProperty("genDir", ".gen/");
    private static final String CLASS_NAME = "className";
    private static final String TRAFFIC_IDENTIFIER = "trafficIdentifier";
    private static final String PROXY_IDENTIFIER = "proxyIdentifier";
    private static final String ENABLE_LOGS = "enableLogging";
    private static final VelocityTemplateProcessor VELOCITY = new VelocityTemplateProcessor(OUTPUT_DIR);

    private StepGenerator() {
    }

    public static void createScenarioJson(ScenarioTestPlan scenario) {
        ObjectMapper om = new ObjectMapper();
        try {
            String path = OUTPUT_DIR + scenario.getName();
            Files.createDirectories(Path.of(path));

            // Convert object to JSON file
            om.writerWithDefaultPrettyPrinter().writeValue(
                    new File(path, scenario.getName() + ".json"), scenario);
            log.info("JSON file created successfully for {}!", scenario.getName());
        } catch (IOException e) {
            throw new CodeGenerationException("unable to create JSON file for "
                    + scenario.getName()
                    + ". Error: " + e.getMessage());
        }
    }

    public static void generateProxyComponent(String scenarioName, ScenarioProxyComponent scenarioComponent) {

        //create empty default
        VelocityContext defaultCtx = new VelocityContext();
        Template defaultTemplate = scenarioComponent instanceof DatabaseProxy ?
                VELOCITY.loadTemplate(PROXY_DB_TEMPLATE_FILE) :
                VELOCITY.loadTemplate(PROXY_HTTP_TEMPLATE_FILE);
        defaultCtx.put(CLASS_NAME, DEFAULT_FILENAME);
        String defaultFile = VELOCITY.fillTemplate(defaultTemplate, defaultCtx);
        saveCodeFile(defaultFile, getComponentFileName(scenarioName, scenarioComponent.getName(), DEFAULT_FILENAME));

        //create step files
        Map<String, List<pieker.common.ConditionTemplate>> stepConditionMap = scenarioComponent.getStepToConditionMap();
        stepConditionMap.forEach((stepId, conditionList) -> {
                    VelocityContext ctx = new VelocityContext();
                    ctx.put(CLASS_NAME, stepId);
                    ctx.put(PROXY_IDENTIFIER, stepId +"_"+ scenarioComponent.getName());
                    ctx.put(ENABLE_LOGS, scenarioComponent.getStepToLog().get(stepId));
                    Template template = scenarioComponent instanceof DatabaseProxy ?
                            VELOCITY.loadTemplate(PROXY_DB_TEMPLATE_FILE) :
                            VELOCITY.loadTemplate(PROXY_HTTP_TEMPLATE_FILE);

                    conditionList.forEach(t -> t.addContextVariable(ctx));
                    String proxyFile = VELOCITY.fillTemplate(template, ctx);
                    saveCodeFile(proxyFile,
                            getComponentFileName(scenarioName, scenarioComponent.getName(), stepId));
                }
        );

    }

    public static void generateTrafficComponent(String scenarioName, ScenarioTrafficComponent scenarioComponent) {
        //create empty default
        VelocityContext ctxContext = new VelocityContext();
        ctxContext.put("threadBody", "// no traffic required at default");
        ctxContext.put(CLASS_NAME, DEFAULT_FILENAME);
        String defaultFile = VELOCITY.fillTemplate(VELOCITY.loadTemplate(TRAFFIC_CONTAINER_TEMPLATE_FILE), ctxContext);
        saveCodeFile(defaultFile, getComponentFileName(scenarioName, scenarioComponent.getName(), DEFAULT_FILENAME));

        //create step files
        Map<String, List<TrafficTemplate>> stepConditionMap = scenarioComponent.getStepToTrafficMap();
        for (Map.Entry<String, List<TrafficTemplate>> entry : stepConditionMap.entrySet()) {
            String stepId = entry.getKey();
            StringBuilder threadBody = new StringBuilder();

            //create thread-code for traffic
            for (TrafficTemplate traffic : entry.getValue()) {
                VelocityContext ctx = new VelocityContext();
                ctx.put(ENABLE_LOGS, traffic.isEnableLogs());
                ctx.put(TRAFFIC_IDENTIFIER, stepId +"_"+ traffic.getIdentifier().replace("-", "_"));
                traffic.addContextVariable(ctx);
                String trafficType = (String) ctx.get("trafficType");
                if (trafficType == null) {
                    log.warn("traffic container with empty trafficType detected. Skipping traffic '{}'.",
                            traffic.getIdentifier());
                } else if (trafficType.equals("request") || (trafficType.equals("sql"))) {
                    Template template = VELOCITY.loadTemplate(TRAFFIC_THREAD_TEMPLATE_FILE);
                    String thread = VELOCITY.fillTemplate(template, ctx);
                    threadBody.append(thread.concat("\n\t\t"));
                } else {
                    throw new CodeGenerationException("unknown traffic type detected. " +
                            "No code template applicable to type: '" + trafficType + "'.");
                }
            }

            // create container file with embedded thread-code
            List<String> trafficIdentifierList = entry.getValue().stream().map(TrafficTemplate::getIdentifier).toList();
            trafficIdentifierList = trafficIdentifierList.stream().map(s -> s.replace("-", "_")).toList();
            trafficIdentifierList = trafficIdentifierList.stream().map(s -> stepId + "_" + s).toList();
            Template template = VELOCITY.loadTemplate(TRAFFIC_CONTAINER_TEMPLATE_FILE);
            VelocityContext ctxTrafficContainer = new VelocityContext();
            ctxTrafficContainer.put(CLASS_NAME, stepId);
            ctxTrafficContainer.put("trafficIdentifierList", trafficIdentifierList);
            ctxTrafficContainer.put("threadBody", threadBody.toString());
            String trafficContainerFile = VELOCITY.fillTemplate(template, ctxTrafficContainer);
            saveCodeFile(trafficContainerFile, getComponentFileName(scenarioName, scenarioComponent.getName(), stepId));
        }
    }

    private static void saveCodeFile(String file, String filePath) {
        try {
            VELOCITY.saveToFile(file, filePath);
        } catch (Exception e) {
            log.error("Error while saving code-file: {}", e.getMessage());
        }
    }

    private static String getComponentFileName(String scenarioName, String scenarioComponentName, String stepId) {
        return scenarioName + "/code/" + scenarioComponentName + "/" + stepId + ".java";
    }
}
