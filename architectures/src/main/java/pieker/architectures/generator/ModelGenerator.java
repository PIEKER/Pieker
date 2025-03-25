package pieker.architectures.generator;

import pieker.architectures.description.DependencyDescription;
import pieker.architectures.generator.exceptions.ArchitectureGeneratorException;
import pieker.architectures.model.ArchitectureModel;

import java.nio.file.Path;
import java.util.NoSuchElementException;

/**
 * Interface for architecture model generator classes.
 *
 * @param <M> type of the architecture model
 */
public interface ModelGenerator<M extends ArchitectureModel<?>> {

    /**
     * Generates an architecture model from the given file path and dependency description file using the specified parser.
     *
     * @param filePath        path to the file to be parsed
     * @param descriptionPath path to the dependency description file
     * @return generated architecture model
     * @throws ArchitectureGeneratorException if generating the model fails
     * @throws NoSuchElementException         if a component in the description does not exist in the model
     */
    M generate(Path filePath, Path descriptionPath) throws ArchitectureGeneratorException;

    /**
     * Generates an architecture model from the given file path using the specified parser.
     *
     * @param filePath              path to the file to be parsed
     * @param dependencyDescription dependency description
     * @return generated architecture model
     * @throws ArchitectureGeneratorException if generating the model fails
     * @throws NoSuchElementException         if a component in the description does not exist in the model
     */
    M generate(Path filePath, DependencyDescription dependencyDescription) throws ArchitectureGeneratorException;

    /**
     * Generates an architecture model from the given file path using the specified parser.
     *
     * @param filePath path to the file to be parsed
     * @return generated architecture model
     * @throws ArchitectureGeneratorException if generating the model fails
     */
    M generate(Path filePath) throws ArchitectureGeneratorException;

    /**
     * Generates an architecture model from the set file path using the specified parser.
     *
     * @return generated architecture model
     * @throws ArchitectureGeneratorException if generating the model fails
     */
    M generate() throws ArchitectureGeneratorException;

    void setFilePath(Path filePath);

    void setDescriptionPath(Path descriptionPath);
}
