package pieker.app;

import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import pieker.code.generators.step.Generator;
import pieker.dsl.model.Feature;

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
        ArgumentHandler argumentHandler = new ArgumentHandler();
        new CommandLine(argumentHandler).parseArgs(args);

        log.info("""
                {}
                
                Hello, this is PIEKER - let us 'kieken', what you got!
                Configuration:
                     DSL:                       {},
                     DSL resource directory:    {},
                     mode:                      {}
                """,
                PIEKER_LOGO,
                argumentHandler.dslFilePath,
                argumentHandler.dslResourceDirectory,
                getMode(argumentHandler)
                );

        Feature feature = pieker.dsl.Main.parse(argumentHandler.dslFilePath, argumentHandler.dslResourceDirectory);

        if (argumentHandler.validateOnly){
            pieker.dsl.code.Engine.validate(feature);
            return;
        }

        pieker.dsl.code.Engine.run(feature);
        feature.getScenarioTestPlanList().forEach(Generator::createScenarioJson);
        if (argumentHandler.dslConfigOnly) return;

        log.debug("starting to create test-components.");
        feature.getScenarioTestPlanList().forEach(scenario -> {
            scenario.getTrafficComponents().forEach(
                    c -> Generator.generateTrafficComponent(scenario.getName(), c));
            scenario.getProxyComponents().forEach(
                    c -> Generator.generateProxyComponent(scenario.getName(), c));
        });
        log.info("finished to create test-Components.");


    }

    private static String getMode(ArgumentHandler h){
        if (h.validateOnly){
            return "VALIDATE-ONLY";
        }
        if (h.dslConfigOnly){
            return "DSL-CONFIG-ONLY";
        }

        return "DEFAULT";
    }
}
