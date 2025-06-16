package pieker.generators.code.multistep;

import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.VelocityContext;
import pieker.common.ScenarioComponent;
import pieker.common.ScenarioTestPlan;
import pieker.dsl.architecture.template.component.DatabaseProxy;
import pieker.generators.code.CodeGenerationException;
import pieker.generators.code.VelocityTemplateProcessor;
import pieker.generators.util.FileSystemUtils;
import pieker.generators.util.JarBuilder;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Generates multi-step proxies for the given test plan based on generated Java files for each step.
 */
@Slf4j
public class MultiStepGenerator {

    private static final String EXECUTION_NAME = System.getProperty("scenarioName");
    // Paths
    private static final String PROJECT_ROOT = System.getProperty("projectRoot");
    private static final String GEN_DIR = PROJECT_ROOT + System.getProperty("genDir", "../.gen/");
    private static final String CODE_DIR = GEN_DIR + EXECUTION_NAME + File.separator + "code" + File.separator;
    // Templates
    private static final String PROXY_TEMPLATE = "multistep/MultiStepProxy.vm";
    private static final String MANIFEST_TEMPLATE = "multistep/MANIFEST.vm";

    private static final VelocityTemplateProcessor TEMPLATE_PROCESSOR = new VelocityTemplateProcessor();

    private MultiStepGenerator() {
    }

    /**
     * Generates multi-step proxies for the given test plan based on generated Java files for each step.
     *
     * @param testPlan test plan
     * @throws CodeGenerationException if an error occurs
     */
    public static void generateMultiStepProxies(ScenarioTestPlan testPlan) throws CodeGenerationException {
        log.debug("Generating Multi-Step-Proxies...");
        final Collection<ScenarioComponent> proxyComponents = new ArrayList<>(testPlan.getProxyComponents());
        generateMultiStepProxies(proxyComponents);
        log.debug("Generating Multi-Step-Traffic-Components...");
        final Collection<ScenarioComponent> trafficComponents = new ArrayList<>(testPlan.getTrafficComponents());
        generateMultiStepTrafficComponents(trafficComponents);
    }

    /**
     * Generates multi-step proxies for the given components based on generated Java files for each step.
     *
     * @param scenarioComponents  collection of proxy components
     * @throws CodeGenerationException if an error occurs
     */
    public static void generateMultiStepProxies(Collection<ScenarioComponent> scenarioComponents) throws CodeGenerationException {
        for (ScenarioComponent component : scenarioComponents) {
            try {
                List<String> dependencies = component instanceof DatabaseProxy ?
                        List.of("dependencies/netty-transport-4.2.0.Final.jar",
                                "dependencies/netty-common-4.2.0.Final.jar",
                                "dependencies/netty-buffer-4.1.119.Final.jar",
                                "dependencies/netty-resolver-4.1.119.Final.jar") :
                        List.of();

                generateMultiStepComponent(component.getName(), dependencies);
            } catch (IOException | InterruptedException e) {
                log.error("Error generating multi-step proxy for component '{}'", component.getName(), e);
                throw new CodeGenerationException(e.getMessage());
            }
        }
    }

    /**
     * Generates multi-step traffic components for the given components based on generated Java files for each step.
     *
     * @param trafficComponents collection of traffic components
     * @throws CodeGenerationException if an error occurs
     */
    public static void generateMultiStepTrafficComponents(Collection<ScenarioComponent> trafficComponents) throws CodeGenerationException {
        for (ScenarioComponent component : trafficComponents) {
            try {
                List<String> dependencies = List.of(
                        "dependencies/json-20250107.jar",
                        "dependencies/postgresql-42.7.5.jar"
                );
                log.debug("Starting to generate multi-step traffic component '{}' with dependencies: {}",
                        component.getName(), dependencies);
                generateMultiStepComponent(component.getName(), dependencies);
            } catch (IOException | InterruptedException e) {
                log.error("Error generating multi-step traffic component '{}'", component.getName(), e);
                throw new CodeGenerationException(e.getMessage());
            }
        }
    }


    /**
     * Generates a multi-step component for the given component based on generated Java files for each step. If
     * {@code dependencies} is not null or empty, the JAR will be compiled with the specified binaries in
     * {@code /resources/dependencies}.
     *
     * @param componentName name of the component
     * @param dependencies  list of dependencies for the component
     * @throws IOException          if an error occurs
     * @throws InterruptedException if the process is interrupted
     */
    public static void generateMultiStepComponent(String componentName, Collection<String> dependencies) throws IOException, InterruptedException {
        final List<String> javaStepFiles = FileSystemUtils.getFiles(CODE_DIR + componentName, ".java");
        final List<String> fileNames = FileSystemUtils.getFileNames(javaStepFiles);
        final String componentDir = CODE_DIR + componentName + File.separator;

        log.debug("Found {} Java files for component '{}': {}", javaStepFiles.size(), componentName, javaStepFiles);

        // Build Step JARs
        for (String javaFile : javaStepFiles) {
            JarBuilder.buildJar(javaFile, dependencies);
        }

        // Create MultiStepProxy.java file for current component
        final String multiStepProxyName = componentName.replace('-', '_');
        VelocityContext proxyContext = new VelocityContext();
        final int orchestratorPort = Integer.parseInt(System.getProperty("orchestratorPort", "42690"));
        proxyContext.put("port", orchestratorPort);
        Map<String, String> endpointJars = new HashMap<>();
        for (String fileName : fileNames) {
            fileName = fileName.replace(".java", "");
            endpointJars.put("/" + fileName, fileName + ".jar");
        }
        proxyContext.put("endpoints", endpointJars.keySet());
        proxyContext.put("endpointJars", endpointJars);
        proxyContext.put("proxyName", multiStepProxyName);
        TEMPLATE_PROCESSOR.processTemplate(PROXY_TEMPLATE, proxyContext, multiStepProxyName + ".java", componentDir);

        // Build Multistep JAR
        JarBuilder.buildJar(componentDir + multiStepProxyName + ".java", componentDir + "build",
                false, null);
    }

}
