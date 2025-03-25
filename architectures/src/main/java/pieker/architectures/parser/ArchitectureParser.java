package pieker.architectures.parser;

import pieker.architectures.parser.exceptions.ArchitectureParserException;

import java.nio.file.Path;

/**
 * Interface for parsing architecture defining files.
 *
 * @param <T> type of data parsed from the file
 */
public interface ArchitectureParser<T> {

    /**
     * Parses the file at the given path.
     *
     * @param filePath path to the file to be parsed
     * @throws ArchitectureParserException if an error occurs during parsing
     */
    void parse(Path filePath) throws ArchitectureParserException;

    /**
     * Parses the file at the set path.
     *
     * @throws ArchitectureParserException if an error occurs during parsing
     */
    void parse() throws ArchitectureParserException;

    T getParsedData();

    void setFilePath(Path filePath);

}
