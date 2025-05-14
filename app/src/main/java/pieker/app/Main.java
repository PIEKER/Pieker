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
import pieker.supervisor.Supervisor;
import pieker.supervisor.SupervisorFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

@Slf4j
public class Main {

    private static final String PIEKER_LOGO = """
            
             ____    _   _______   _   __  _______   ____    _
            |  _ \\  | | |   ____| | | / / |   ____| |  _ \\  | |
            | |_) | | | |  |____  | |/ /  |  |____  | |_) | | |
            | ___/  | | |   ____| |    |  |   ____| | _ _/  |_|
            | |     | | |  |____  | |\\ \\  |  |____  | |\\ \\   _
            |_|     |_| |_______| | | \\_\\ |_______| |_| \\_\\ |_|
            """;

    private static final PluginManager PLUGIN_MANAGER = new PluginManager(System.getProperty("pluginDir"));

    private static ScenarioTestPlan testPlan;
    private static ArchitectureModel<?> architectureModel;
    private static final String DSL_FILE_PATH = System.getProperty("dslFilePath");
    private static final String DSL_RESOURCE_DIRECTORY = System.getProperty("dslResourceDirectory");
    private static final String ARCHITECTURE_FILE_PATH = System.getProperty("architectureFile");
    private static final String INTERFACE_DESCRIPTION_FILE_PATH = System.getProperty("interfaceDescriptionFile");
    private static final String GEN_DIR = System.getProperty("genDir");
    private static final long ASSERT_TIMEOUT = Long.parseLong(System.getProperty("assertTimeout", "30000"));

    public static void main(String[] args) throws IOException {

        log.info("""
                        {}
                        
                        Hello, this is PIEKER - let us 'kieken', what you got:
                        
                        Run Configuration:
                             DSL file:                   {},
                             DSL resource directory:     {},
                             Architecture file:          {},
                             Interface description file: {},
                             Installed Plugins:          {},
                             assertTimeout:              {}
                        """,
                PIEKER_LOGO,
                DSL_FILE_PATH,
                DSL_RESOURCE_DIRECTORY,
                ARCHITECTURE_FILE_PATH,
                INTERFACE_DESCRIPTION_FILE_PATH,
                PLUGIN_MANAGER,
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
            return;
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

            if (System.getProperty("skipImageGeneration", "false").equals("false")) {
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
        Supervisor<?> supervisor = SupervisorFactory.createSupervisor(testPlan, architectureModel);

        supervisor.setupTestEnvironment();

        supervisor.executeTests();

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Error during test execution: {}", e.getMessage());
        }

        supervisor.stopTestEnvironment();
        supervisor.destroyTestEnvironment();
    }

    private static void evaluate(){
        Evaluator evaluator = new Evaluator(); //TODO pass connection parameters
        evaluator.preprocessFiles(GEN_DIR + File.separator + System.getProperty("scenarioName"), ".log");
        testPlan.getStepIds().forEach(sId ->
            evaluator.run(
                    testPlan.getAssertionsMap().getOrDefault(sId, new ArrayList<>()),
                    ASSERT_TIMEOUT
            )
        );
        evaluator.generateResultJson(testPlan);
    }

}
