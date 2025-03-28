package pieker.architectures;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import pieker.architectures.compose.ComposeComponentInjector;
import pieker.architectures.compose.ComposeModelGenerator;
import pieker.architectures.compose.DockerComposeParser;
import pieker.architectures.compose.model.ComposeArchitectureModel;
import pieker.architectures.generator.ModelGenerator;
import pieker.architectures.injector.ComponentInjector;
import pieker.architectures.model.ArchitectureModel;
import pieker.architectures.parser.ArchitectureParser;
import pieker.architectures.parser.exceptions.ArchitectureParserException;
import pieker.architectures.parser.exceptions.UnsupportedFileTypeException;
import pieker.common.ScenarioTestPlan;
import pieker.dsl.model.Feature;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class ArchitectureIntegrationTest {

    private static final Path DOCKER_COMPOSE_FILE = Path.of("src/test/resources/docker-compose.yml");
    private static final Path DESCRIPTION_FILE = Path.of("src/test/resources/compose.description.yml");

    @Test
    void testParser() throws UnsupportedFileTypeException, ArchitectureParserException {

        ArchitectureParser<?> architectureParser = ArchitectureFactory.createParser(DOCKER_COMPOSE_FILE);
        assertInstanceOf(DockerComposeParser.class, architectureParser);

        architectureParser.parse(DOCKER_COMPOSE_FILE);
        assertNotNull(architectureParser.getParsedData());

        log.debug("Parsed data: {}", architectureParser.getParsedData());
    }

    @Test
    void testGenerator() throws UnsupportedFileTypeException {

        ModelGenerator<?> modelGenerator = ArchitectureFactory.createGenerator(DOCKER_COMPOSE_FILE);
        assertInstanceOf(ComposeModelGenerator.class, modelGenerator);

        ArchitectureModel<?> model = modelGenerator.generate(DOCKER_COMPOSE_FILE);
        assertNotNull(model);

        log.debug("Generated model: {}", model);
    }

    @Test
    void testInjector() throws UnsupportedFileTypeException {

        ModelGenerator<?> modelGenerator = ArchitectureFactory.createGenerator(DOCKER_COMPOSE_FILE);
        ComposeArchitectureModel model = (ComposeArchitectureModel) modelGenerator.generate(DOCKER_COMPOSE_FILE);

        ComponentInjector<?, ?> componentInjector = ArchitectureFactory.createInjector(model);

        assertInstanceOf(ComposeComponentInjector.class, componentInjector);
    }

    @Test
    void testFullWorkflow() throws UnsupportedFileTypeException, ArchitectureParserException, IOException {

        final ModelGenerator<?> modelGenerator = ArchitectureFactory.createGenerator(DOCKER_COMPOSE_FILE);
        final ArchitectureModel<?> model = modelGenerator.generate(DOCKER_COMPOSE_FILE, DESCRIPTION_FILE);
        final ComponentInjector<?, ?> componentInjector = ArchitectureFactory.createInjector(model);

        final int newComponents = getTestPlan().getComponents().size();
        final int existingComponents = model.getAllComponents().size();
        componentInjector.injectComponents(getTestPlan());

        assertEquals(existingComponents + newComponents, model.getAllComponents().size());
        model.removeUnlinkedComponents();
        // As long as STORAGE is not supported, at least one component should be removed
        assertTrue(model.getAllComponents().size() < existingComponents + newComponents);

        String genDir = System.getProperty("genDir");
        ((ComposeArchitectureModel) model).toYamlFile("../" + genDir + "pieker-example-docker-compose.yml");
    }

    private ScenarioTestPlan getTestPlan() throws IOException {
        final String dslFilePath = "../.input/dsl/runningExample.feature";
        final String dslResourceDirectory = "../.input/resources";
        Feature feature = pieker.dsl.Main.parse(dslFilePath, dslResourceDirectory);
        pieker.dsl.code.Engine.validate(feature);
        pieker.dsl.code.Engine.run(feature);
        return feature.getScenarioTestPlanList().getFirst();
    }

}