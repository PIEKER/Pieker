package pieker.generators.util;

import lombok.extern.slf4j.Slf4j;
import pieker.generators.code.CodeGenerationException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Utility class for building JAR files from Java source files.
 */
@Slf4j
public final class JarBuilder {

    private JarBuilder() {
    }

    /**
     * Builds a JAR file from a given Java file without dependencies.
     *
     * @param javaFilePath path to the Java file
     * @throws IOException          if an error occurs
     * @throws InterruptedException if the process is interrupted
     */
    public static void buildJar(String javaFilePath) throws IOException, InterruptedException {
        File javaFile = new File(javaFilePath);
        String directory = javaFile.getParent();
        String buildDirPath = directory + File.separator + "build";
        String outputDirPath = directory + File.separator + "jars";
        buildJar(javaFilePath, buildDirPath, outputDirPath);
    }

    /**
     * Builds a JAR file from a given Java file including the given dependencies.
     *
     * @param javaFilePath path to the Java file
     * @throws IOException          if an error occurs
     * @throws InterruptedException if the process is interrupted
     */
    public static void buildJar(String javaFilePath, Collection<String> dependencies) throws IOException, InterruptedException {
        File javaFile = new File(javaFilePath);
        String directory = javaFile.getParent();
        String buildDirPath = directory + File.separator + "build";
        String outputDirPath = directory + File.separator + "jars";
        buildJar(javaFilePath, buildDirPath, outputDirPath, false, dependencies);
    }

    /**
     * Builds a JAR file from a given Java file.
     *
     * @param javaFilePath path to the Java file
     * @param buildDirPath path to the build directory
     * @param skipManifest whether to skip creating a manifest file
     * @throws IOException          if an error occurs
     * @throws InterruptedException if the process is interrupted
     */
    public static void buildJar(String javaFilePath, String buildDirPath, boolean skipManifest,
                                Collection<String> dependencyPaths) throws IOException, InterruptedException {
        File javaFile = new File(javaFilePath);
        String directory = javaFile.getParent();
        String outputDirPath = directory + File.separator + "jars";
        buildJar(javaFilePath, buildDirPath, outputDirPath, skipManifest, dependencyPaths);
    }

    /**
     * Builds a JAR file from a given Java file.
     *
     * @param javaFilePath  path to the Java file
     * @param buildDirPath  path to the build directory
     * @param outputDirPath path to the output directory
     * @throws IOException          if an error occurs
     * @throws InterruptedException if the process is interrupted
     */
    public static void buildJar(String javaFilePath, String buildDirPath, String outputDirPath) throws IOException, InterruptedException {
        buildJar(javaFilePath, buildDirPath, outputDirPath, false, null);
    }

    /**
     * Builds a JAR file from a given Java file.
     *
     * @param javaFilePath  path to the Java file
     * @param buildDirPath  path to the build directory
     * @param outputDirPath path to the output directory
     * @param skipManifest  whether to skip creating a manifest file
     * @param dependencies  Paths of dependencies to include in the JAR
     * @throws IOException          if an error occurs
     * @throws InterruptedException if the process is interrupted
     */
    public static void buildJar(String javaFilePath, String buildDirPath, String outputDirPath, boolean skipManifest,
                                Collection<String> dependencies) throws IOException, InterruptedException {
        // Get the directory of the .java file
        File javaFile = new File(javaFilePath);
        String directory = javaFile.getParent();

        // Create the build directory
        File buildDir = new File(buildDirPath);
        if (!buildDir.exists() && !buildDir.mkdirs()) {
            throw new IOException("Failed to create build directory: " + buildDirPath);
        }

        // Compile the .java file into a .class file
        List<String> compileCommand = new ArrayList<>();
        compileCommand.add("javac");
        compileCommand.add("-d");
        compileCommand.add(buildDirPath);

        // Add dependencies to classpath if applicable
        if (dependencies != null && !dependencies.isEmpty()) {
            log.debug("Including dependencies in build: {}", dependencies);
            final String classpath = String.join(File.pathSeparator, dependencies.stream()
                    .map(s -> s.substring(s.lastIndexOf(File.separator)))
                    .toList());
            compileCommand.add("-cp");
            compileCommand.add(classpath);
            dependencies.forEach(dependencyPath -> {
                try {
                    FileSystemUtils.copyFileFromClasspath(dependencyPath, buildDirPath);
                } catch (IOException e) {
                    log.error("Failed to copy file from classpath: {}", dependencyPath, e);
                    throw new CodeGenerationException("Failed to copy file from classpath: " + dependencyPath);
                }
            });
        }

        compileCommand.add(javaFilePath);
        Process compileProcess = new ProcessBuilder(compileCommand)
                .directory(new File(directory))
                .inheritIO()
                .start();

        int compileResult = compileProcess.waitFor();
        if (compileResult != 0) {
            throw new IOException("Failed to compile " + javaFilePath);
        }

        File manifestFile = new File(buildDirPath, "MANIFEST.MF");
        String mainClassName = javaFile.getName().replace(".java", "");
        // Create a manifest file for the JAR
        if (!skipManifest) {
            String manifestContent = "Manifest-Version: 1.0\nMain-Class: " + mainClassName + "\n";
            Files.write(manifestFile.toPath(), manifestContent.getBytes());
        }

        // Extract dependencies into build directory if provided
        if (dependencies != null && !dependencies.isEmpty()) {
            for (String dependencyPath : dependencies) {
                Process jarExtractionProcess = new ProcessBuilder("jar", "xf", dependencyPath)
                        .directory(new File(buildDirPath))
                        .inheritIO()
                        .start();

                int jarExtractionResult = jarExtractionProcess.waitFor();
                if (jarExtractionResult != 0) {
                    throw new IOException("Failed to extract JAR dependency file '%s' into '%s'".formatted(dependencyPath, buildDirPath));
                }
            }
        }

        // Build the JAR file
        File jarFile = new File(outputDirPath, mainClassName + ".jar");
        Process jarProcess = new ProcessBuilder("jar", "cfm", jarFile.getAbsolutePath(), manifestFile.getAbsolutePath(), "-C", buildDirPath, ".")
                .directory(new File(directory))
                .inheritIO()
                .start();

        int jarResult = jarProcess.waitFor();
        if (jarResult != 0) {
            throw new IOException("Failed to create JAR file");
        }
        log.info("JAR file created successfully: {}", jarFile.getAbsolutePath());

        // Clean up
        Files.delete(manifestFile.toPath());
        deleteClassFiles(buildDirPath);
    }

    /**
     * Deletes all .class files in the given directory.
     *
     * @param directoryPath path to the directory
     */
    private static void deleteClassFiles(String directoryPath) {
        File dir = new File(directoryPath);
        if (dir.exists() && dir.isDirectory()) {
            for (File file : Objects.requireNonNull(dir.listFiles((d, name) -> name.endsWith(".class")))) {
                if (!file.delete()) {
                    log.error("Failed to delete file: {}", file.getAbsolutePath());
                }
            }
        } else {
            log.error("Invalid directory.");
        }
    }

}
