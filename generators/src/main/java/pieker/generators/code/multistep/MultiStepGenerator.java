package pieker.generators.code.multistep;

import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.VelocityContext;
import pieker.common.ScenarioComponent;
import pieker.common.ScenarioTestPlan;
import pieker.generators.code.VelocityTemplateProcessor;
import pieker.generators.util.FileSystemUtils;
import pieker.generators.util.JarBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generates multi-step proxies for the given test plan based on generated Java files for each step.
 */
@Slf4j
public class MultiStepGenerator {

    private static final String EXECUTION_NAME = System.getProperty("executionName");
    private static final String PROJECT_ROOT = System.getProperty("projectRoot", "../../../../../../../");
    private static final String GEN_DIR = PROJECT_ROOT + System.getProperty("genDir", "../.gen/");
    private static final String CODE_DIR = GEN_DIR + EXECUTION_NAME + "/code/";
    private static final String PROXY_TEMPLATE = "multistep/MultiStepProxy.vm";
    private static final String MANIFEST_TEMPLATE = "multistep/MANIFEST.vm";
    private static final VelocityTemplateProcessor TEMPLATE_PROCESSOR = new VelocityTemplateProcessor();

    public static void generateMultiStepProxies(ScenarioTestPlan testPlan) throws IOException, InterruptedException {
        for (ScenarioComponent component : testPlan.getComponents()) {
            generateMultiStepProxy(component.getName());
        }
    }

    public static void generateMultiStepProxy(String componentName) throws IOException, InterruptedException {
        final List<String> javaStepFiles = FileSystemUtils.getFiles(CODE_DIR + componentName, ".java");
        final List<String> fileNames = FileSystemUtils.getFileNames(javaStepFiles);

        log.debug("Found {} Java files for component '{}': {}", javaStepFiles.size(), componentName, javaStepFiles);

        // Build Step JARs
        for (String javaFile : javaStepFiles) {
            JarBuilder.buildJar(javaFile);
        }

        // Create MultiStepProxy.java file for current component
        VelocityContext proxyContext = new VelocityContext();
        proxyContext.put("port", 42690);
        Map<String, String> endpointJars = new HashMap<>();
        for (String fileName : fileNames) {
            fileName = fileName.replace(".java", "");
            endpointJars.put("/" + fileName, fileName + ".jar");
        }
        proxyContext.put("endpoints", endpointJars.keySet());
        proxyContext.put("endpointJars", endpointJars);
        TEMPLATE_PROCESSOR.processTemplate(PROXY_TEMPLATE, proxyContext, componentName + ".java",
                CODE_DIR + componentName + "/");

        // Create MANIFEST.MF for Multistep Proxy JAR
        VelocityContext manifestContext = new VelocityContext();
        manifestContext.put("jars", fileNames.stream().map(name -> name.replace("java", "jar")).toList());
        TEMPLATE_PROCESSOR.processTemplate(MANIFEST_TEMPLATE, manifestContext, "MANIFEST.MF",
                CODE_DIR + componentName + "/");

        // Build Multistep JAR
        //TODO

    }
}
