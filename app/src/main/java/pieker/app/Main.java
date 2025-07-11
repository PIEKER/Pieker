package pieker.app;

import lombok.extern.slf4j.Slf4j;
import pieker.architectures.ArchitectureFactory;
import pieker.architectures.generator.ModelGenerator;
import pieker.architectures.injector.ComponentInjector;
import pieker.architectures.model.ArchitectureModel;
import pieker.architectures.model.Component;
import pieker.common.ScenarioTestPlan;
import pieker.common.plugin.PluginManager;
import pieker.dsl.architecture.Engine;
import pieker.dsl.model.Feature;
import pieker.dsl.util.FeatureUtil;
import pieker.evaluator.Evaluator;
import pieker.generators.code.multistep.MultiStepGenerator;
import pieker.generators.code.step.StepGenerator;
import pieker.generators.components.docker.DockerImageGenerator;
import pieker.orchestrator.Orchestrator;
import pieker.orchestrator.OrchestratorFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Main {

    private static final String PIEKER_LOGO = """
                                    \s
                      %%            \s
                  **  %%%%          \s
                 *****%%%%%%           _____  _____  ______  _  __ ______  _____
               *******%%%%%%%%        |  __ \\|_   _||  ____|| |/ /|  ____||  __ \\
             *********%%%%%%%%%%      | |__) | | |  | |__   | ' / | |__   | |__) |
            **********%%%%%%%%%%%%    |  ___/  | |  |  __|  |  <  |  __|  |  _  /
               *******%%%%%%%%%%      | |     _| |_ | |____ | . \\ | |____ | | \\ \\
                  ****%%%%%%%%        |_|    |_____||______||_|\\_\\|______||_|  \\_\\
                    **%%%%%%        \s
                      %%%%          \s
                      %%            \s
            """;
    private static final String FALSE = "false";
    private static final String SCENARIO_NAME = "scenarioName";

    // - Paths -
    private static final String PROJECT_ROOT = System.getProperty("projectRoot");
    private static final String DSL_FILE_PATH = PROJECT_ROOT + System.getProperty("dslFilePath");
    private static final String DSL_RESOURCE_DIRECTORY = PROJECT_ROOT + System.getProperty("dslResourceDirectory");
    private static final String ARCHITECTURE_FILE_PATH = PROJECT_ROOT + System.getProperty("architectureFile");
    private static final String INTERFACE_DESCRIPTION_FILE_PATH = PROJECT_ROOT + System.getProperty("interfaceDescriptionFile");
    private static final String GEN_DIR = PROJECT_ROOT + System.getProperty("genDir");

    private static final PluginManager PLUGIN_MANAGER = new PluginManager(System.getProperty("pluginDir"));
    private static final float TEST_DEFAULT_DURATION = Float.parseFloat(System.getProperty("testDurationDefault", "30.0"));
    private static final float ASSERT_TIMEOUT = Float.parseFloat(System.getProperty("assertTimeout", "30.0"));

    private static ScenarioTestPlan testPlan;
    private static ArchitectureModel<?> architectureModel;

    public static void main(String[] args) throws IOException {
        log.info("""
                        \u001B[1;34m
                        {}
                        \u001B[1;32mRun Configuration:\u001B[0;32m
                             DSL file:                   {}
                             DSL resource directory:     {}
                             Architecture file:          {}
                             Interface description file: {}
                             Installed Plugins:          {}
                             Default Test Duration:      {} s
                             assertTimeout:              {} s\u001b[0m
                        """,
                PIEKER_LOGO,
                Paths.get(DSL_FILE_PATH).normalize(),
                Paths.get(DSL_RESOURCE_DIRECTORY).normalize(),
                Paths.get(ARCHITECTURE_FILE_PATH).normalize(),
                Paths.get(INTERFACE_DESCRIPTION_FILE_PATH).normalize(),
                PLUGIN_MANAGER.getPluginRegistry().keySet(),
                TEST_DEFAULT_DURATION,
                ASSERT_TIMEOUT
        );

        // Check setup
        if (!checkSetup()) {
            log.error("Setup check failed. Exiting...");
            System.exit(1);
        }

        // Parse DSL, Architecture and Interface description files, generate test code, test components, and test environment
        preprocessing();

        if (System.getProperty("runTests", "true").equals(FALSE)) {
            log.info("Skipping test execution as per configuration.");
            System.exit(0);
        }
        // Test Execution
        runTests();
        // Evaluation
        evaluate();
        // Cleanup
        cleanup();
    }

    /**
     * Checks if the setup is valid.
     *
     * @return true if the setup is valid, false otherwise
     */
    private static boolean checkSetup() {
        boolean isValid = true;
        if (DSL_FILE_PATH == null) {
            log.error("No DSL file path provided. Please set the 'dslFilePath' property.");
            isValid = false;
        }
        if (ARCHITECTURE_FILE_PATH == null) {
            log.error("No architecture file path provided. Please set the 'architectureFile' property.");
            isValid = false;
        }
        if (INTERFACE_DESCRIPTION_FILE_PATH == null) {
            log.error("No interface description file path provided. Please set the 'interfaceDescriptionFile' property.");
            isValid = false;
        }
        return isValid;
    }

    /**
     * Contains all procedures that need to be complete before test execution.
     *
     * @throws IOException if an error occurs during preprocessing
     */
    private static void preprocessing() throws IOException {
        // Parse DSL
        Feature feature = pieker.dsl.Main.parse(DSL_FILE_PATH, DSL_RESOURCE_DIRECTORY, PLUGIN_MANAGER);

        if (Boolean.parseBoolean(System.getProperty("validateOnly", FALSE))) {
            Engine.validate(feature);
            log.info("Validation finished successfully.");
            System.exit(0);
        }

        // Generate Test Plan List
        Engine.run(feature);

        // Set selected Scenario
        String scenarioName = FeatureUtil.createCodeSafeString(System.getProperty(SCENARIO_NAME, ""));
        if (scenarioName.isEmpty()){
            testPlan = feature.getScenarioTestPlanList().getFirst();
        } else {
            List<ScenarioTestPlan> testPlanList = feature.getScenarioTestPlanList()
                    .stream()
                    .filter(scenarioTestPlan -> scenarioTestPlan.getName().equals(scenarioName))
                    .toList();
            assert testPlanList.size() > 1;
            testPlan = testPlanList.getFirst();
        }
        System.setProperty(SCENARIO_NAME, testPlan.getName());

        // Generate Test Plan JSON
        StepGenerator.createScenarioJson(testPlan);


        if (Boolean.parseBoolean(System.getProperty("testPlanOnly", FALSE))) {
            log.info("PIEKER Test Plan generation finished successfully.");
            System.exit(0);
        }


        // Generate Test Environment
        log.info("Starting test architecture generation...");
        final Path architectureFilePath = Path.of(ARCHITECTURE_FILE_PATH);
        final Path interfaceDescriptionFilePath = Path.of(INTERFACE_DESCRIPTION_FILE_PATH);
        final ModelGenerator<?> modelGenerator = ArchitectureFactory.createGenerator(architectureFilePath);
        final ArchitectureModel<?> model = modelGenerator.generate(architectureFilePath, interfaceDescriptionFilePath);
        final ComponentInjector<?, ?> componentInjector = ArchitectureFactory.createInjector(model);

        componentInjector.injectComponents(testPlan);
        architectureModel = model;

        if (!System.getProperty("skipFileGeneration", FALSE).equals(FALSE)) {
            return;
        }

        // Generate code for test components
        log.info("Starting test component code generation...");
        testPlan.getTrafficComponents().forEach(c ->
                StepGenerator.generateTrafficComponent(testPlan.getName(), c));
        testPlan.getProxyComponents().forEach(c ->
                StepGenerator.generateProxyComponent(testPlan.getName(), c, (ArchitectureModel<Component>) architectureModel));
        log.info("Finished generating test component code.");

        // Package test component code into JAR files
        log.info("Starting test component JAR generation...");
        MultiStepGenerator.generateMultiStepProxies(testPlan);
        log.info("Finished test component JAR generation.");

        if (System.getProperty("skipDockerImageGeneration", FALSE).equals(FALSE)) {
            // Generate Docker images for test components
            log.info("Starting Docker image generation...");
            try {
                DockerImageGenerator.generateImages(testPlan);
            } catch (Exception e) {
                log.error("Aborting... Error during Docker image generation: {}", e.getMessage());
            }
            log.info("Finished Docker image generation.");
        }

        log.info("Finished test architecture generation. Storing results...");
        final String filePath = GEN_DIR + File.separator + System.getProperty(SCENARIO_NAME) + File.separator + "docker-compose.yml";
        architectureModel.saveToFile(filePath);
    }

    /**
     * Executes the tests using the generated test plan and architecture model.
     */
    private static void runTests() {
        if (architectureModel == null || testPlan == null) {
            log.error("Test plan or architecture model is null. Exiting...");
            System.exit(1);
        }
        System.setProperty(SCENARIO_NAME, testPlan.getName());
        Orchestrator<?> orchestrator = OrchestratorFactory.createOrchestrator(testPlan, architectureModel);
        // Start Test Environment
        orchestrator.setupTestEnvironment();
        // Run Test Steps
        orchestrator.executeTests();
        // Stop Components in Test Environment
        orchestrator.stopTestEnvironment();
        // Restart Components that are needed to verify the test results
        orchestrator.startComponents(testPlan, architectureModel);
    }

    private static void evaluate() {
        if (architectureModel == null){
            log.error("Architecture model is null, detected during evaluation. Exiting...");
            System.exit(1);
        }
        Evaluator evaluator = new Evaluator();
        evaluator.preprocessFiles(GEN_DIR + File.separator + System.getProperty(SCENARIO_NAME), ".log");
        testPlan.getStepIds().forEach(sId ->
                evaluator.run(
                        testPlan.getAssertionsMap().getOrDefault(sId, new ArrayList<>()),
                        (long) (ASSERT_TIMEOUT * 1000)
                )
        );
        evaluator.publishResult(testPlan);
    }

    private static void cleanup() {
        Orchestrator<?> orchestrator = OrchestratorFactory.createOrchestrator(testPlan, architectureModel);
        orchestrator.destroyTestEnvironment();
    }

}
