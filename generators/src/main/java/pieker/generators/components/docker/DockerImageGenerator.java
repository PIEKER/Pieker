package pieker.generators.components.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.velocity.VelocityContext;
import pieker.common.ScenarioComponent;
import pieker.common.ScenarioTestPlan;
import pieker.generators.code.VelocityTemplateProcessor;
import pieker.generators.util.FileSystemUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * This class is responsible for generating Docker images for the components defined in a test plan.
 * It uses the Docker Java API to build and save Docker images based on the generated Java files.
 */
@Slf4j
public final class DockerImageGenerator {

    private static final String EXECUTION_NAME = System.getProperty("scenarioName");
    private static final boolean MINIMAL_IMAGE = Boolean.parseBoolean(System.getProperty("minimalDockerImages"));
    // Paths
    private static final String PROJECT_ROOT = System.getProperty("projectRoot");
    private static final String GEN_DIR = PROJECT_ROOT + System.getProperty("genDir", "../.gen/");
    private static final String CODE_DIR = GEN_DIR + EXECUTION_NAME + File.separator + "code" + File.separator;
    private static final String IMAGE_DIR = GEN_DIR + EXECUTION_NAME + File.separator + "images" + File.separator;
    // Templates
    private static final String DOCKERFILE_TEMPLATE = "docker/ProxyDockerfile.vm";
    private static final String DOCKERFILE_MINIMAL_TEMPLATE = "docker/ProxyDockerfileMinimal.vm";

    private static final VelocityTemplateProcessor TEMPLATE_PROCESSOR = new VelocityTemplateProcessor();

    private DockerImageGenerator() {
    }

    /**
     * Generates Docker images for generated components in the given test plan.
     *
     * @param testPlan test plan
     * @throws IOException if an error occurs
     */
    public static void generateImages(ScenarioTestPlan testPlan) throws IOException {
        generateImages(testPlan.getComponents());
    }

    /**
     * Generates Docker images for the given components by building JARs from the generate Proxy code.
     *
     * @param components collection of scenario components
     * @throws IOException if an error occurs
     */
    public static void generateImages(Collection<ScenarioComponent> components) throws IOException {
        final List<String> componentNames = components.stream().map(ScenarioComponent::getName).toList();
        generateImages(componentNames);
    }

    /**
     * Generates Docker images for the given component names by building JARs from the generate Proxy code.
     *
     * @param componentNames list of scenario component names
     * @throws IOException if an error occurs
     */
    public static void generateImages(List<String> componentNames) throws IOException {
        log.info("Generating image for {} components", componentNames.size());
        log.debug("Components: {}", componentNames);

        final List<Path> proxyJARs = FileSystemUtils.getJARsInDir(CODE_DIR)
                .stream()
                .filter(jar -> componentNames.contains(jar.getFileName()
                        .toString()
                        .replace(".jar", "")))
                .toList();

        log.debug("Found {} JARs: {}", proxyJARs.size(), proxyJARs.stream().map(Path::getFileName).toList());

        // Generate Dockerfiles for each component
        for (Path proxyJar : proxyJARs) {
            final String componentName = proxyJar.getFileName().toString().replace(".jar", "");
            final String dockerFilePath = CODE_DIR + componentName + File.separator;
            final VelocityContext context = new VelocityContext();
            context.put("jarPath", "jars/" + proxyJar.getFileName());
            final String templatePath = MINIMAL_IMAGE ? DOCKERFILE_MINIMAL_TEMPLATE : DOCKERFILE_TEMPLATE;
            log.debug("Generating {}Dockerfile for {} at {}", MINIMAL_IMAGE ? "minimal " : "", proxyJar.getFileName(), dockerFilePath);
            TEMPLATE_PROCESSOR.processTemplate(templatePath, context, "Dockerfile", dockerFilePath);
        }

        // Build Docker images based on Dockerfiles
        for (String componentName : componentNames) {
            final String buildContextPath = CODE_DIR + componentName;
            log.debug("Building image for component {} at {}", componentName, buildContextPath);
            // Build Docker image with name "<componentName>:<scenarioName>"
            final String imageId = buildImage(buildContextPath, componentName, System.getProperty("scenarioName", "latest"));
            // Save Docker image to file
            log.debug("Saving image {} for component {} to file", imageId, componentName);
            saveImage(imageId, componentName, IMAGE_DIR);
        }
    }

    /**
     * Builds a Docker image from the specified build context path.
     *
     * @param buildContextPath path to the build context directory (containing Dockerfile)
     * @param imageName        name of the image (lowercase)
     * @param tag              tag for the image (after name)
     * @return image ID of the built image
     * @throws IOException if an error occurs
     */
    public static String buildImage(String buildContextPath, String imageName, String tag) throws IOException {

        try (DockerClient dockerClient = getDockerClient()) {
            // Specify the directory containing your Dockerfile and compiled Java files (the build context)
            File dockerContext = new File(buildContextPath);

            // Build the image from the Dockerfile; this returns the image ID
            String imageId = dockerClient.buildImageCmd(dockerContext)
                    .withDockerfile(new File(dockerContext, "Dockerfile"))
                    .withTags(Set.of(imageName.toLowerCase() + ":" + tag.toLowerCase()))
                    .exec(new BuildImageResultCallback())
                    .awaitImageId();

            log.info("Image built with ID: {}and tag: {}:{}", imageId, imageName, tag);
            return imageId;
        }
    }

    /**
     * Saves the Docker image with the specified ID to a tar-file.
     *
     * @param imageId   ID of the Docker image to save
     * @param imageName name of the image file (without extension)
     * @param dirPath   directory path where the image file will be saved
     * @throws IOException if an error occurs
     */
    public static void saveImage(String imageId, String imageName, String dirPath) throws IOException {

        try (DockerClient dockerClient = getDockerClient()) {
            InputStream imageStream = dockerClient.saveImageCmd(imageId).exec();

            File buildDir = new File(dirPath);
            if (!buildDir.exists() && !buildDir.mkdirs()) {
                throw new IOException("Could not create directory: %s".formatted(buildDir.getAbsolutePath()));
            }
            FileOutputStream fos = new FileOutputStream(dirPath + imageName + ".tar");
            IOUtils.copy(imageStream, fos);

            fos.close();
            imageStream.close();

            log.debug("Saved image with ID {} to {}", imageId, dirPath + imageName);
        }
    }

    /**
     * Creates a Docker client.
     *
     * @return Docker client
     */
    private static DockerClient getDockerClient() {
        // FIXME: Use TLS-enabled Docker client
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("tcp://localhost:2375")
                .build();

        return DockerClientBuilder.getInstance(config).build();
    }

}
