package pieker.architectures.compose;

import org.junit.jupiter.api.Test;
import pieker.architectures.common.model.HttpLink;
import pieker.architectures.compose.model.ComposeArchitectureModel;
import pieker.architectures.compose.model.ComposeComponent;
import pieker.architectures.compose.model.ComposeService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ComposeComponentInjectorTest {

    @Test
    void testServiceInjection() {
        // Setup: A --l--> B
        final ComposeArchitectureModel model = new ComposeArchitectureModel("3.8");
        final ComposeService a = new ComposeService("a");
        a.setEnvironment(new HashMap<>(Map.of("I", "1")));
        final ComposeService b = new ComposeService("b");
        final HttpLink<ComposeComponent> l = new HttpLink<>(a, b);
        model.addRootComponents(List.of(a, b));
        model.addLink(l);

        // Test: A --l'--> X --l--> B
        final ComposeComponentInjector injector = new ComposeComponentInjector(model);
        final ComposeService x = new ComposeService("x");
        injector.injectProxy(l, x, Map.of("I", "2", "J", "3"));

        assertTrue(model.getComponents().contains(x));
        assertEquals(2, model.getLinks().size());
        assertTrue(model.getLinks().contains(l));
        assertEquals(1, model.getLinksForSource(x).size());
        assertEquals(1, model.getLinksForTarget(x).size());
        assertEquals(1, model.getLinksForSource(a).size());
        assertEquals(1, model.getLinksForTarget(b).size());
        assertEquals("2", a.getEnvironmentValue("I"));
        assertEquals("3", a.getEnvironmentValue("J"));
    }

}