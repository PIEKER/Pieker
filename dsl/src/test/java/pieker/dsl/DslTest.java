package pieker.dsl;

import pieker.dsl.architecture.Engine;
import pieker.dsl.model.Feature;
import pieker.dsl.parser.FeatureParser;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.Test;


class DslTest {

    final String resourceDir = "src/main/resources/input";

    @Test
    void testFeatureParsingExample() {
        String inputFile = "runningExample.feature";
        Feature feature = new Feature(inputFile, resourceDir);
        assertDoesNotThrow(() -> FeatureParser.parse(feature, true));
    }

    @Test
    void testEngineExample() throws IOException {
        String inputFile = "runningExample.feature";
        Feature feature = new Feature(inputFile, resourceDir);
        FeatureParser.parse(feature, true);
        assertDoesNotThrow(() -> Engine.run(feature));
    }
}
