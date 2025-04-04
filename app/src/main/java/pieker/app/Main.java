package pieker.app;

import lombok.extern.slf4j.Slf4j;
import pieker.dsl.model.Feature;
import pieker.generators.code.multistep.MultiStepGenerator;
import pieker.generators.code.step.StepGenerator;
import pieker.generators.components.docker.DockerImageGenerator;

import java.io.IOException;

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

    public static void main(String[] args) throws IOException {

        log.info("""
                        {}
                        
                        Hello, this is PIEKER - let us 'kieken', what you got:
                        
                        Run Configuration:
                             DSL:                        {},
                             DSL resource directory:     {},
                             Architecture file:          {},
                             Interface description file: {},
                        
                        """,
                PIEKER_LOGO,
                System.getProperty("dslFilePath"),
                System.getProperty("dslResourceDirectory"),
                System.getProperty("architectureFile"),
                System.getProperty("interfaceDescriptionFile")
        );

        // Parse DSL, Architecture and Interface description files, generate test code, test components, and test environment
        preprocessing();

        // Test Execution
        // TODO

        // Evaluation
        // TODO
    }

    private static void preprocessing() throws IOException {
        // Parse DSL
        Feature feature = pieker.dsl.Main.parse(System.getProperty("dslFilePath"), System.getProperty("dslResourceDirectory"));

        if (Boolean.parseBoolean(System.getProperty("validateOnly", "false"))) {
            pieker.dsl.code.Engine.validate(feature);
            log.info("Validation finished successfully.");
            return;
        }

        // Generate Test Plan
        pieker.dsl.code.Engine.run(feature);
        feature.getScenarioTestPlanList().forEach(StepGenerator::createScenarioJson);

        if (Boolean.parseBoolean(System.getProperty("testPlanOnly", "false"))) {
            log.info("PIEKER Test Plan generation finished successfully.");
            return;
        }

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
            System.setProperty("scenarioName",  scenario.getName());
            MultiStepGenerator.generateMultiStepProxies(scenario);
        });
        log.info("Finished test component JAR generation.");

        // Generate Docker images for test components
        log.info("Starting Docker image generation...");
        feature.getScenarioTestPlanList().forEach(scenario -> {
            System.setProperty("scenarioName",  scenario.getName());
            try {
                DockerImageGenerator.generateImages(scenario);
            } catch (Exception e) {
                log.error("Aborting... Error during Docker image generation: {}", e.getMessage());
            }
        });
        log.info("Finished Docker image generation.");
    }

}
