package pieker.generators.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Utility class for file operations.
 */
@Slf4j
public final class FileSystemUtils {

    private FileSystemUtils() {
    }

    /**
     * Get all files in a directory with a given extension.
     *
     * @param directory path to the directory
     * @param extension file extension
     * @return list of files with given extension in the directory
     * @throws IOException if an error occurs
     */
    public static List<String> getFiles(String directory, String extension) throws IOException {
        try (Stream<Path> walk = Files.walk(Paths.get(directory))) {
            return walk.filter(Files::isRegularFile).map(Path::toString).filter(f -> f.endsWith(extension)).toList();
        }
    }

    /**
     * Get the file names from a list of file paths.
     *
     * @param filePaths list of file paths
     * @return list of file names
     */
    public static List<String> getFileNames(List<String> filePaths) {
        return filePaths.stream().map(Path::of).map(Path::getFileName).map(Path::toString).toList();
    }

    /**
     * Get paths of all JAR files in a directory and its subdirectories.
     *
     * @param directory path to the directory
     * @return list of JAR files in the directory
     */
    public static List<Path> getJARsInDir(String directory) {
        log.debug("Searching JARs in directory: {}", directory);
        Path dirPath = Path.of(directory);
        try (var paths = Files.walk(dirPath)) {
            List<Path> result = paths.filter(Files::isRegularFile).filter(p -> p.toString().endsWith(".jar")).toList();
            log.debug("Found {} JARs: {}", result.size(), result.stream().map(Path::getFileName).toList());
            return result;
        } catch (IOException e) {
            log.error(e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Checks if a directory contains a file with the specified name.
     *
     * @param directoryPath path to the directory
     * @param fileName      name of the file to check
     * @return true if the directory contains the file, false otherwise
     */
    public static boolean directoryContainsFile(String directoryPath, String fileName) {
        File directory = new File(directoryPath);

        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                boolean found = false;
                for (File file : files) {
                    if (file.getName().equals(fileName)) {
                        found = true;
                        break;
                    }
                }
                return found;
            }
        }

        return false;
    }

    /**
     * Copies a file from the classpath to a specified destination.
     *
     * @param resource    path to the source file in the classpath
     * @param destination path to the destination file
     * @throws IOException if an error occurs
     */
    public static void copyFileFromClasspath(String resource, String destination) throws IOException {
        File file = new File(resource);
        Path destinationPath = Paths.get(destination, file.getName());

        try (InputStream resourceStream = FileSystemUtils.class.getClassLoader().getResourceAsStream(resource)) {

            if (resourceStream == null) {
                throw new FileNotFoundException("Resource not found: " + resource);
            }

            Files.copy(resourceStream, destinationPath);
            log.debug("File copied to: {}", destinationPath.toAbsolutePath());
        }
    }

    /**
     * Deletes all contents of a directory, including subdirectories and files.
     *
     * @param dirPath path to the directory to delete contents from
     */
    public static void deleteContents(String dirPath) {
        File directory = new File(dirPath);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IllegalArgumentException("Provided file is not a valid directory.");
        }

        File[] files = directory.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                deleteContents(String.valueOf(file));
            }
            if (!file.delete()) throw new RuntimeException("Failed to delete file: " + file.getAbsolutePath());
        }
    }

}