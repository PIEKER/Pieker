package pieker.dsl;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pieker.dsl.architecture.Engine;
import pieker.dsl.model.Feature;
import pieker.dsl.parser.FeatureParser;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;


class DslTest {

    static Path resourceDir;
    static Feature feature = null;

    static {
        try {
            resourceDir = Paths.get(Objects.requireNonNull(DslTest.class.getClassLoader().getResource("input")).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    static void parseFeature() {
        String inputFile = "all-valid.feature";
        feature = new Feature(inputFile, resourceDir.toString());
        assertDoesNotThrow(() -> FeatureParser.parse(feature, true));
        assertDoesNotThrow(() -> Engine.run(feature));
    }

    @Test
    void testValidScenario(){

        assertFalse(feature.getScenarioTestPlanList().isEmpty());

    }
}
