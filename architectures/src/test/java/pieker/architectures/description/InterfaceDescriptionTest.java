package pieker.architectures.description;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import pieker.architectures.model.Link;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
class InterfaceDescriptionTest {

    private static final Path DESCRIPTION_FILE = Path.of("src/test/resources/test.description.yml");

    @Test
    void testParsing() {

        InterfaceDescription architectureDescription = InterfaceDescriptionParser.parse(DESCRIPTION_FILE);

        assertNotNull(architectureDescription);
        assertEquals(2, architectureDescription.getComponents().size());
        assertEquals("serviceA", architectureDescription.getComponents().getFirst().getName());
        assertEquals(2, architectureDescription.getComponents().getFirst().getRequires().size());
        assertEquals("serviceC", architectureDescription.getComponents().getFirst().getRequires().getFirst().getTarget());
        assertEquals(Link.LinkType.HTTP, architectureDescription.getComponents().getFirst().getRequires().getFirst().getType());
    }

}
