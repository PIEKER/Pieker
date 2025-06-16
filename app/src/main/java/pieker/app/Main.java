package pieker.app;

import lombok.extern.slf4j.Slf4j;
import pieker.architectures.ArchitectureFactory;
import pieker.architectures.generator.ModelGenerator;
import pieker.architectures.injector.ComponentInjector;
import pieker.architectures.model.ArchitectureModel;
import pieker.common.ScenarioTestPlan;
import pieker.common.plugin.PluginManager;
import pieker.dsl.model.Feature;
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

    // - Paths -
    private static final String PROJECT_ROOT = System.getProperty("projectRoot");
    private static final String DSL_FILE_PATH = PROJECT_ROOT + System.getProperty("dslFilePath");
    private static final String DSL_RESOURCE_DIRECTORY = PROJECT_ROOT + System.getProperty("dslResourceDirectory");
    private static final String ARCHITECTURE_FILE_PATH = PROJECT_ROOT + System.getProperty("architectureFile");
    private static final String INTERFACE_DESCRIPTION_FILE_PATH = PROJECT_ROOT + System.getProperty("interfaceDescriptionFile");
    private static final String GEN_DIR = PROJECT_ROOT + System.getProperty("genDir");

    private static final PluginManager PLUGIN_MANAGER = new PluginManager(System.getProperty("pluginDir"));
    private static final float TEST_DEFAULT_DURATION = Float.parseFloat(System.getProperty("testDurationDefault", "30.0"));
    private static final long ASSERT_TIMEOUT = Long.parseLong(System.getProperty("assertTimeout", "30000"));

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
                             Default Test Duration:      {}
                             assertTimeout:              {} \u001b[0m
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

        if (System.getProperty("runTests", "true").equals("false")) {
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

        if (Boolean.parseBoolean(System.getProperty("validateOnly", "false"))) {
            pieker.dsl.architecture.Engine.validate(feature);
            log.info("Validation finished successfully.");
            System.exit(0);
        }

        // Generate Test Plan
        pieker.dsl.architecture.Engine.run(feature);
        feature.getScenarioTestPlanList().forEach(StepGenerator::createScenarioJson);

        if (Boolean.parseBoolean(System.getProperty("testPlanOnly", "false"))) {
            log.info("PIEKER Test Plan generation finished successfully.");
            System.exit(0);
        }

        if (System.getProperty("skipFileGeneration", "false").equals("false")) {
            // Generate code for test components
            log.info("Starting test component code generation...");
            feature.getScenarioTestPlanList().forEach(scenario -> {
                scenario.getTrafficComponents().forEach(
                        c -> StepGenerator.generateTrafficComponent(scenario.getName(), c));
                scenario.getProxyComponents().forEach(
                        c -> StepGenerator.generateProxyComponent(scenario.getName(), c));
            });
            log.info("Finished generating test component code.");

            // Package test component code into JAR files
            log.info("Starting test component JAR generation...");
            feature.getScenarioTestPlanList().forEach(scenario -> {
                System.setProperty("scenarioName", scenario.getName());
                MultiStepGenerator.generateMultiStepProxies(scenario);
            });
            log.info("Finished test component JAR generation.");

            if (System.getProperty("skipDockerImageGeneration", "false").equals("false")) {
                // Generate Docker images for test components
                log.info("Starting Docker image generation...");
                feature.getScenarioTestPlanList().forEach(scenario -> {
                    System.setProperty("scenarioName", scenario.getName());
                    try {
                        DockerImageGenerator.generateImages(scenario);
                    } catch (Exception e) {
                        log.error("Aborting... Error during Docker image generation: {}", e.getMessage());
                    }
                });
                log.info("Finished Docker image generation.");
            }
        }

        // Generate Test Environment
        log.info("Starting test architecture generation...");
        final Path architectureFilePath = Path.of(ARCHITECTURE_FILE_PATH);
        final Path interfaceDescriptionFilePath = Path.of(INTERFACE_DESCRIPTION_FILE_PATH);
        final ModelGenerator<?> modelGenerator = ArchitectureFactory.createGenerator(architectureFilePath);
        final ArchitectureModel<?> model = modelGenerator.generate(architectureFilePath, interfaceDescriptionFilePath);
        final ComponentInjector<?, ?> componentInjector = ArchitectureFactory.createInjector(model);

        // FIXME: Ensure only one scenario is executed at a time (e.g. through config)
        componentInjector.injectComponents(feature.getScenarioTestPlanList().getFirst());

        if (System.getProperty("skipFileGeneration", "false").equals("false")) {
            log.info("Finished test architecture generation. Storing results...");
            final String filePath = GEN_DIR + File.separator + System.getProperty("scenarioName") + File.separator + "docker-compose.yml";
            model.saveToFile(filePath);
        }

        testPlan = feature.getScenarioTestPlanList().getFirst();
        architectureModel = model;
    }

    /**
     * Executes the tests using the generated test plan and architecture model.
     */
    private static void runTests() {
        if (architectureModel == null || testPlan == null) {
            log.error("Test plan or architecture model is null. Exiting...");
            System.exit(1);
        }
        System.setProperty("scenarioName", testPlan.getName());
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
        Evaluator evaluator = new Evaluator(); //TODO pass connection parameters
        evaluator.preprocessFiles(GEN_DIR + File.separator + System.getProperty("scenarioName"), ".log");
        testPlan.getStepIds().forEach(sId ->
                evaluator.run(
                        testPlan.getAssertionsMap().getOrDefault(sId, new ArrayList<>()),
                        ASSERT_TIMEOUT
                )
        );
        evaluator.publishResult(testPlan);
    }

    private static void cleanup() {
        Orchestrator<?> orchestrator = OrchestratorFactory.createOrchestrator(testPlan, architectureModel);
        orchestrator.destroyTestEnvironment();
    }

}
