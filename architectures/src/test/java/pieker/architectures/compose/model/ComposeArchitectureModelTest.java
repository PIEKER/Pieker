package pieker.architectures.compose.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pieker.architectures.ArchitectureFactory;
import pieker.architectures.common.model.HttpApiLink;
import pieker.architectures.compose.ComposeModelGenerator;
import pieker.architectures.model.Link;

import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ComposeArchitectureModelTest {

    private ComposeArchitectureModel composeArchitectureModel;
    private ComposeService composeService;
    private ComposeVolume composeVolume;
    private ComposeNetwork composeNetwork;

    @BeforeEach
    void initModel() {
        this.composeService = new ComposeService("service");
        this.composeVolume = new ComposeVolume("volume");
        this.composeNetwork = new ComposeNetwork("network");

        this.composeArchitectureModel = new ComposeArchitectureModel("3.8");
        this.composeArchitectureModel.addComponent(this.composeService);
        this.composeArchitectureModel.addComponent(this.composeVolume);
        this.composeArchitectureModel.addComponent(this.composeNetwork);
    }

    @Test
    void testLinks() {
        final HttpApiLink httpApiLink = new HttpApiLink(this.composeService, this.composeService);

        this.composeArchitectureModel.addLink(httpApiLink);

        assertNotNull(this.composeArchitectureModel.getLinks());
        assertEquals(1, this.composeArchitectureModel.getLinks().size());
        assertTrue(this.composeArchitectureModel.getLinks().contains(httpApiLink));
        assertEquals(httpApiLink, this.composeArchitectureModel.getLinksForSource(this.composeService).getFirst());
        assertEquals(httpApiLink, this.composeArchitectureModel.getLinksForTarget(this.composeService).getFirst());

        assertTrue(this.composeArchitectureModel.validate());

        Link clonedLink = httpApiLink.clone();
        assertInstanceOf(HttpApiLink.class, clonedLink);
    }

    @Test
    void testComponents() {
        final ComposeService composeService2 = new ComposeService("service2");

        this.composeService.addContainedComponent(composeService2);
        assertTrue(this.composeService.isValid());
        composeService2.addContainedComponent(this.composeService);
        assertFalse(this.composeService.isValid());
    }

    @Test
    void toYaml() {
        // Get appropriate generator instance
        ComposeModelGenerator modelGenerator = (ComposeModelGenerator) ArchitectureFactory.createGenerator(
                Path.of("src/test/resources/docker-compose.yml"));
        // Parse and generate model
        ComposeArchitectureModel model = modelGenerator.generate();

        // Define new service
        ComposeService service = new ComposeService("injected-db");
        service.setImage("postgres:latest");
        service.setPorts(Map.of("1345", "5432"));
        service.setEnvironment(Map.of(
                "POSTGRES_USER", "testuser",
                "POSTGRES_PASSWORD", "testpassword",
                "POSTGRES_DB", "testdb")
        );

        model.addComponent(service);

        // Assemble model to executable format
        model.toYamlFile("src/test/resources/generated-docker-compose.yml");
    }

}