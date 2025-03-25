package pieker.architectures;

import lombok.extern.slf4j.Slf4j;
import pieker.architectures.compose.ComposeComponentInjector;
import pieker.architectures.compose.ComposeModelGenerator;
import pieker.architectures.compose.DockerComposeParser;
import pieker.architectures.compose.model.ComposeArchitectureModel;
import pieker.architectures.generator.ModelGenerator;
import pieker.architectures.injector.ComponentInjector;
import pieker.architectures.model.ArchitectureModel;
import pieker.architectures.parser.ArchitectureParser;
import pieker.architectures.parser.exceptions.UnsupportedFileTypeException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Factory class for creating instances of ModelGenerator or ArchitectureParser.
 */
@Slf4j
public class ArchitectureFactory {

    private ArchitectureFactory() {}

    // Map of file types to their corresponding generator classes
    private static final Map<SupportedFileType, Supplier<ModelGenerator<?>>> GENERATOR_MAP = Map.of(
            SupportedFileType.DOCKER_COMPOSE, ComposeModelGenerator::new
    );
    // Map of file types to their corresponding parser classes
    private static final Map<SupportedFileType, Supplier<ArchitectureParser<?>>> PARSER_MAP = Map.of(
            SupportedFileType.DOCKER_COMPOSE, DockerComposeParser::new
    );

    /**
     * Creates an appropriate ComponentInjector instance based on the model provided.
     *
     * @param model architecture model
     * @return new instance of ComponentInjector
     * @throws IllegalArgumentException if model type is not supported
     */
    public static ComponentInjector<?,?> createInjector(ArchitectureModel<?> model)
            throws IllegalArgumentException {
        return switch (model) {
            case ComposeArchitectureModel composeModel -> new ComposeComponentInjector(composeModel);
            default -> throw new IllegalArgumentException("Unsupported model type: " + model.getClass().getSimpleName());
        };
    }

    /**
     * Creates an appropriate ModelGenerator instance based on the file provided.
     *
     * @param filePath path to file
     * @return new instance of ModelGenerator
     * @throws UnsupportedFileTypeException if file type is not supported
     * @throws IllegalArgumentException     if file path is null or does not exist
     */
    public static ModelGenerator<?> createGenerator(Path filePath)
            throws UnsupportedFileTypeException, IllegalArgumentException {
        return (ModelGenerator<?>) createInstance(filePath, ModelGenerator.class);
    }

    /**
     * Creates an appropriate ModelGenerator instance based on the file provided.
     *
     * @param filePath path to file
     * @return new instance of ModelGenerator
     * @throws UnsupportedFileTypeException if file type is not supported
     * @throws IllegalArgumentException     if file path is null or does not exist
     */
    public static ModelGenerator<?> createGenerator(Path filePath, Path descriptionPath)
            throws UnsupportedFileTypeException, IllegalArgumentException {
        ModelGenerator<?> generator = (ModelGenerator<?>) createInstance(filePath, ModelGenerator.class);
        generator.setDescriptionPath(descriptionPath);
        return generator;
    }

    /**
     * Creates an appropriate ArchitectureParser instance based on the file provided.
     *
     * @param filePath path to file
     * @return new instance of ArchitectureParser
     * @throws UnsupportedFileTypeException if file type is not supported
     * @throws IllegalArgumentException     if file path is null or does not exist
     */
    public static ArchitectureParser<?> createParser(Path filePath)
            throws UnsupportedFileTypeException, IllegalArgumentException {
        return (ArchitectureParser<?>) createInstance(filePath, ArchitectureParser.class);
    }

    /**
     * Creates an appropriate class instance based on the file provided and the FactoryType.
     *
     * @param filePath path to file
     * @param clazz    type of class to create
     * @return new instance of the specified type
     * @throws UnsupportedFileTypeException if file type is not supported
     * @throws IllegalArgumentException     if file path is null or does not exist
     */
    private static Object createInstance(Path filePath, Class<?> clazz)
            throws UnsupportedFileTypeException, IllegalArgumentException {

        if (filePath == null || !Files.exists(filePath)) {
            log.error("File path is null or does not exist: {}", filePath);
            throw new IllegalArgumentException("File path is null or does not exist: " + filePath);
        }

        String fileName = filePath.getFileName().toString().toLowerCase();
        SupportedFileType fileType = SupportedFileType.fromFileName(fileName);
        if (fileType == SupportedFileType.UNSUPPORTED) {
            log.error("Unsupported file type: {}", fileName);
            throw new UnsupportedFileTypeException("Unsupported file type: " + fileName);
        }
        
        Object instance = switch (clazz.getSimpleName()) {
            case "ModelGenerator" -> GENERATOR_MAP.get(fileType).get();
            case "ArchitectureParser" -> PARSER_MAP.get(fileType).get();
            default -> throw new IllegalStateException("Unexpected value: " + clazz.getSimpleName());
        };
        
        switch (instance) {
            case ModelGenerator<?> generator -> generator.setFilePath(filePath);
            case ArchitectureParser<?> parser -> parser.setFilePath(filePath);
            default -> throw new IllegalStateException("Unexpected value: " + instance);
        }

        return instance;
    }
}
