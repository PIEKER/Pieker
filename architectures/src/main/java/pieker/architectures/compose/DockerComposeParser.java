package pieker.architectures.compose;

import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;
import pieker.architectures.parser.AbstractArchitectureParser;
import pieker.architectures.parser.exceptions.ArchitectureParserException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * Parser for Docker Compose files.
 */
@Slf4j
public class DockerComposeParser extends AbstractArchitectureParser<Map<String, Object>> {

    /**
     * Parses the Docker Compose file at the given path.
     *
     * @param filePath the path to the Docker Compose file to be parsed
     * @throws ArchitectureParserException if an error occurs during parsing
     */
    @Override
    protected Map<String, Object> parseFile(Path filePath) throws ArchitectureParserException {
        try {
            String content = Files.readString(filePath);
            if (content.isBlank()) {
                log.error("File is empty or contains only whitespace: {}", filePath);
                throw new ArchitectureParserException("File is empty or contains only whitespace: " + filePath);
            }
            // Parse Docker Compose YAML
            Yaml yaml = new Yaml();
            Map<String, Object> loadedData = yaml.load(content);
            if (loadedData == null) {
                log.error("Failed to parse YAML: Parsed content is null.");
                throw new ArchitectureParserException("Failed to parse YAML: Parsed content is null.");
            }
            return loadedData;
        } catch (Exception e) {
            log.error("Failed to parse Docker Compose file: {}", filePath, e);
            throw new ArchitectureParserException("Failed to parse Docker Compose file: " + e.getMessage(), e);
        }
    }

}