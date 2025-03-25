package pieker.architectures.compose;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import pieker.architectures.ArchitectureFactory;
import pieker.architectures.common.model.HttpApiLink;
import pieker.architectures.compose.model.ComposeArchitectureModel;
import pieker.architectures.compose.model.ComposeComponent;
import pieker.architectures.model.ComponentLink;
import pieker.architectures.model.Link;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class ComposeGeneratorTest {

    private static final Path DOCKER_COMPOSE_FILE = Path.of("src/test/resources/docker-compose.yml");
    private static final Path COMPOSE_DESCRIPTION_FILE = Path.of("src/test/resources/compose.description.yml");

    @Test
    void testGenerator() {
        final ComposeModelGenerator modelGenerator = (ComposeModelGenerator) ArchitectureFactory
                .createGenerator(DOCKER_COMPOSE_FILE, COMPOSE_DESCRIPTION_FILE);
        final ComposeArchitectureModel model = modelGenerator.generate();

        assertNotNull(model);
        assertTrue(model.validate());
        assertFalse(model.getComponents().isEmpty());
        assertFalse(model.getLinks().isEmpty());

        List<Link<ComposeComponent>> links = model.getLinksForTarget(model.getComponent("service-c").get());
        assertFalse(links.isEmpty());

        HttpApiLink<ComposeComponent> httpApiLink = (HttpApiLink<ComposeComponent>) links.getFirst();
        assertEquals(ComponentLink.LinkType.HTTP_API, links.getFirst().getType());

        String targetUrlEnvName = httpApiLink.getSourceRelatedEnvironmentVariables().get("URL_VAR");
        assertEquals("COUNTER_URL", targetUrlEnvName);
    }

}
