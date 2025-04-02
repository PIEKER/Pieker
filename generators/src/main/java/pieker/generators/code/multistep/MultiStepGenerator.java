package pieker.generators.code.multistep;

import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.VelocityContext;
import pieker.common.ScenarioComponent;
import pieker.common.ScenarioTestPlan;
import pieker.generators.code.CodeGenerationException;
import pieker.generators.code.VelocityTemplateProcessor;
import pieker.generators.util.FileSystemUtils;
import pieker.generators.util.JarBuilder;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generates multi-step proxies for the given test plan based on generated Java files for each step.
 */
@Slf4j
public class MultiStepGenerator {

    private static final String EXECUTION_NAME = System.getProperty("executionName");
    // Paths
    private static final String PROJECT_ROOT = System.getProperty("projectRoot");
    private static final String GEN_DIR = PROJECT_ROOT + System.getProperty("genDir", "../.gen/");
    private static final String CODE_DIR = GEN_DIR + EXECUTION_NAME + File.separator + "code" + File.separator;
    // Templates
    private static final String PROXY_TEMPLATE = "multistep" + File.separator + "MultiStepProxy.vm";
    private static final String MANIFEST_TEMPLATE = "multistep" + File.separator + "MANIFEST.vm";

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
        generateMultiStepProxies(testPlan.getComponents());
    }

    /**
     * Generates multi-step proxies for the given components based on generated Java files for each step.
     *
     * @param scenarioComponents collection of scenario components
     * @throws CodeGenerationException if an error occurs
     */
    public static void generateMultiStepProxies(Collection<ScenarioComponent> scenarioComponents) throws CodeGenerationException {
        for (ScenarioComponent component : scenarioComponents) {
            try {
                generateMultiStepProxy(component.getName());
            } catch (IOException | InterruptedException e) {
                log.error("Error generating multi-step proxy for component '{}'", component.getName(), e);
                throw new CodeGenerationException(e.getMessage());
            }
        }
    }

    /**
     * Generates a multi-step proxy for the given component based on generated Java files for each step.
     *
     * @param componentName name of the component
     * @throws IOException          if an error occurs
     * @throws InterruptedException if the process is interrupted
     */
    public static void generateMultiStepProxy(String componentName) throws IOException, InterruptedException {
        final List<String> javaStepFiles = FileSystemUtils.getFiles(CODE_DIR + componentName, ".java");
        final List<String> fileNames = FileSystemUtils.getFileNames(javaStepFiles);
        final String componentDir = CODE_DIR + componentName + File.separator;
        final String componentJarDir = componentDir + "jars" + File.separator;

        log.debug("Found {} Java files for component '{}': {}", javaStepFiles.size(), componentName, javaStepFiles);

        // Build Step JARs
        for (String javaFile : javaStepFiles) {
            JarBuilder.buildJar(javaFile);
        }

        // Create MultiStepProxy.java file for current component
        final String multiStepProxyName = componentName.replace('-', '_');
        VelocityContext proxyContext = new VelocityContext();
        proxyContext.put("port", 42690);
        Map<String, String> endpointJars = new HashMap<>();
        for (String fileName : fileNames) {
            fileName = fileName.replace(".java", "");
            endpointJars.put("/" + fileName, fileName + ".jar");
        }
        proxyContext.put("endpoints", endpointJars.keySet());
        proxyContext.put("endpointJars", endpointJars);
        proxyContext.put("proxyName", multiStepProxyName);
        TEMPLATE_PROCESSOR.processTemplate(PROXY_TEMPLATE, proxyContext, multiStepProxyName + ".java", componentDir);

        // Create MANIFEST.MF for Multistep Proxy JAR
        VelocityContext manifestContext = new VelocityContext();
        manifestContext.put("proxyName", multiStepProxyName);
        manifestContext.put("jars", fileNames.stream().map(name -> name.replace("java", "jar")).toList());
        TEMPLATE_PROCESSOR.processTemplate(MANIFEST_TEMPLATE, manifestContext, "MANIFEST.MF", componentJarDir);

        // Build Multistep JAR
        JarBuilder.buildJar(componentDir + multiStepProxyName + ".java", componentDir + "jars", true);
    }
}
