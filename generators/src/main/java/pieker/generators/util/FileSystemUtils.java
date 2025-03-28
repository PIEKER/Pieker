package pieker.generators.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

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
            return walk.filter(Files::isRegularFile)
                    .map(Path::toString)
                    .filter(f -> f.endsWith(extension))
                    .toList();
        }
    }

    /**
     * Get the file names from a list of file paths.
     *
     * @param filePaths list of file paths
     * @return list of file names
     */
    public static List<String> getFileNames(List<String> filePaths) {
        return filePaths.stream()
                .map(Path::of)
                .map(Path::getFileName)
                .map(Path::toString)
                .toList();
    }
}
