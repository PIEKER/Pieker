package pieker.architectures.description;

import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import pieker.architectures.model.ArchitectureModel;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;

public class InterfaceDescriptionParser {

    /**
     * Parses the PIEKER Interface Description file at the given path.
     *
     * @param filePath path to file
     * @return InterfaceDescription-object
     * @throws IllegalArgumentException       if the file cannot be found
     * @throws InterfaceDescriptionException if the file format is invalid or cannot be parsed
     */
    public static InterfaceDescription parse(Path filePath) {
        try {
            ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
            try (InputStream inputStream = Files.newInputStream(filePath)) {
                return objectMapper.readValue(inputStream, InterfaceDescription.class);
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Could not find file: " + filePath);
        } catch (DatabindException e) {
            throw new InterfaceDescriptionException("Invalid Format. Could not parse: " + filePath, e);
        } catch (Exception e) {
            throw new InterfaceDescriptionException("Could not parse: " + filePath, e);
        }
    }

    /**
     * Parses the PIEKER Interface Description file at the given path and validates it against the given architecture model.
     *
     * @param filePath path to file
     * @return InterfaceDescription-object
     * @throws IllegalArgumentException       if the file cannot be found
     * @throws InterfaceDescriptionException if the file format is invalid or cannot be parsed
     * @throws NoSuchElementException         if a component in the description does not exist in the model
     */
    public static InterfaceDescription parse(Path filePath, ArchitectureModel<?> model) {
        InterfaceDescription description = parse(filePath);
        description.validate(model);
        return description;
    }

}
