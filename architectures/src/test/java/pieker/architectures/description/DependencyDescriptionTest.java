package pieker.architectures.description;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import pieker.architectures.model.ComponentLink;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
class DependencyDescriptionTest {

    private static final Path DESCRIPTION_FILE = Path.of("src/test/resources/test.description.yml");

    @Test
    void testParsing() {

        DependencyDescription architectureDescription = DependencyDescriptionParser.parse(DESCRIPTION_FILE);

        assertNotNull(architectureDescription);
        assertEquals(2, architectureDescription.getComponents().size());
        assertEquals("serviceA", architectureDescription.getComponents().getFirst().getName());
        assertEquals(2, architectureDescription.getComponents().getFirst().getDependencies().size());
        assertEquals("serviceC", architectureDescription.getComponents().getFirst().getDependencies().getFirst().getTarget());
        assertEquals(ComponentLink.LinkType.HTTP_API, architectureDescription.getComponents().getFirst().getDependencies().getFirst().getType());
    }

}
