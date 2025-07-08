package pieker.dsl;

import lombok.extern.slf4j.Slf4j;
import pieker.common.plugin.PluginManager;
import pieker.dsl.parser.FeatureParser;
import pieker.dsl.model.Feature;

import java.io.IOException;

@Slf4j
public class Main {

    private Main(){}

    /**
     * API Method to read in a Feature file and create a list of ScenarioTestPlans
     *
     * @param inputFile path to .feature file
     * @param resourceDirectory path to directory containing additional resources
     * @return PIEKER Model Tree root node
     * @throws IOException if any provided path is not resolvable.
     */
    public static Feature parse(String inputFile, String resourceDirectory) throws IOException {
        Feature feature = new Feature(inputFile, resourceDirectory);
        FeatureParser.parse(feature, false);
        return feature;
    }

    /**
     * API Method to read in a Feature file and create a list of ScenarioTestPlans
     *
     * @param inputFile path to .feature file
     * @param resourceDirectory path to directory containing additional resources
     * @param pm object to interact with installed plugins
     * @return PIEKER Model Tree root node
     * @throws IOException if any provided path is not resolvable.
     */
    public static Feature parse(String inputFile, String resourceDirectory, PluginManager pm) throws IOException {
        Feature feature = new Feature(inputFile, resourceDirectory);
        FeatureParser.parse(feature, false, pm);
        return feature;
    }
}