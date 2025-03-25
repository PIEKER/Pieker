package pieker.architectures.parser;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import pieker.architectures.parser.exceptions.ArchitectureParserException;

import java.nio.file.Path;

/**
 * Abstract base class for architecture related parsers.
 *
 * @param <T> type of data parsed from the file
 */
@Slf4j
@Getter
public abstract class AbstractArchitectureParser<T> implements ArchitectureParser<T> {

    @Setter
    protected Path filePath;
    protected T parsedData;

    @Override
    public void parse() throws ArchitectureParserException {
        parse(filePath);
    }

    @Override
    public void parse(Path filePath) throws ArchitectureParserException {
        if (filePath == null) {
            throw new IllegalArgumentException("File path cannot be null");
        }
        log.info("Parsing file: {}", filePath);
        parsedData = parseFile(filePath);
        log.info("Successfully parsed file: {}", filePath);
    }

    /**
     * Parses the file at the given path. This method should be implemented by subclasses.
     *
     * @param filePath the path to the file to be parsed
     * @return parsed data
     * @throws ArchitectureParserException if an error occurs during parsing
     */
    protected abstract T parseFile(Path filePath) throws ArchitectureParserException;

}

