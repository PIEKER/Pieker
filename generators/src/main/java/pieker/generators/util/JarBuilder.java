package pieker.generators.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

@Slf4j
public final class JarBuilder {

    private JarBuilder() {
    }

    /**
     * Builds a JAR file from a given Java file.
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
     * Builds a JAR file from a given Java file.
     *
     * @param javaFilePath path to the Java file
     * @param buildDirPath path to the build directory
     * @param skipManifest whether to skip creating a manifest file
     * @throws IOException          if an error occurs
     * @throws InterruptedException if the process is interrupted
     */
    public static void buildJar(String javaFilePath, String buildDirPath, boolean skipManifest) throws IOException, InterruptedException {
        File javaFile = new File(javaFilePath);
        String directory = javaFile.getParent();
        String outputDirPath = directory + File.separator + "jars";
        buildJar(javaFilePath, buildDirPath, outputDirPath, skipManifest);
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
        buildJar(javaFilePath, buildDirPath, outputDirPath, false);
    }

    /**
     * Builds a JAR file from a given Java file.
     *
     * @param javaFilePath  path to the Java file
     * @param buildDirPath  path to the build directory
     * @param outputDirPath path to the output directory
     * @param skipManifest  whether to skip creating a manifest file
     * @throws IOException          if an error occurs
     * @throws InterruptedException if the process is interrupted
     */
    public static void buildJar(String javaFilePath, String buildDirPath, String outputDirPath, boolean skipManifest) throws IOException, InterruptedException {
        // Get the directory of the .java file
        File javaFile = new File(javaFilePath);
        String directory = javaFile.getParent();

        // Create the build directory
        File buildDir = new File(buildDirPath);
        if (!buildDir.exists() && !buildDir.mkdirs()) {
            throw new IOException("Failed to create build directory: " + buildDirPath);
        }

        // Compile the .java file into a .class file
        Process compileProcess = new ProcessBuilder("javac", "-d", buildDirPath, javaFilePath)
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
