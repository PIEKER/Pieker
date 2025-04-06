package pieker.generators.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
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
}