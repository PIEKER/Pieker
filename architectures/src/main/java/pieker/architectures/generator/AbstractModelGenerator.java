package pieker.architectures.generator;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import pieker.architectures.ArchitectureFactory;
import pieker.architectures.description.InterfaceDescription;
import pieker.architectures.description.InterfaceDescriptionParser;
import pieker.architectures.description.DescriptionComponent;
import pieker.architectures.generator.exceptions.ArchitectureGeneratorException;
import pieker.architectures.model.ArchitectureModel;
import pieker.architectures.model.Component;
import pieker.architectures.model.Link;
import pieker.architectures.parser.ArchitectureParser;
import pieker.architectures.parser.exceptions.ArchitectureParserException;
import pieker.architectures.parser.exceptions.UnsupportedFileTypeException;

import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Setter
public abstract class AbstractModelGenerator<M extends ArchitectureModel<C>, C extends Component> implements ModelGenerator<M> {

    protected Path filePath;
    protected Path descriptionPath;

    @Override
    public M generate() throws ArchitectureGeneratorException {
        M model = generate(filePath);
        model.addLinks(constructLinks(descriptionPath));
        validate(model);
        return model;
    }

    public M generate(InterfaceDescription description) throws ArchitectureGeneratorException, NoSuchElementException {
        return generate(filePath, description);
    }

    @Override
    public M generate(Path file, Path descriptionPath) throws ArchitectureGeneratorException, NoSuchElementException {
        return generate(file, InterfaceDescriptionParser.parse(descriptionPath));
    }

    @Override
    public M generate(Path file, InterfaceDescription description) throws ArchitectureGeneratorException, NoSuchElementException {
        M model = generate(file);
        description.validate(model);
        model.addLinks(constructLinks(description));
        setComponentInterfaceTypes(model, description);
        validate(model);
        return model;
    }

    @Override
    public M generate(Path filePath) throws ArchitectureGeneratorException {
        log.info("Generating architecture model from file: {}", filePath);
        try {
            ArchitectureParser<?> parser = ArchitectureFactory.createParser(filePath);
            parser.parse(filePath);
            M model = constructModel(parser.getParsedData());
            validate(model);
            return model;
        } catch (UnsupportedFileTypeException | ArchitectureParserException e) {
            throw new ArchitectureGeneratorException(e.getMessage());
        }
    }

    /**
     * Constructs the architecture model from parsed data.
     *
     * @param <T>        the type of the parsed data
     * @param parsedData the data parsed from the file
     * @return the constructed architecture model
     */
    protected abstract <T> M constructModel(T parsedData);

    /**
     * Constructs the links between components in the architecture model.
     *
     * @param descriptionPath path to dependency description file
     * @return list of links
     */
    protected List<Link<C>> constructLinks(Path descriptionPath) {
        if (descriptionPath == null) {
            return List.of();
        }
        return constructLinks(InterfaceDescriptionParser.parse(descriptionPath));
    }

    /**
     * Constructs the links between components in the architecture model.
     *
     * @param description dependency description
     * @return list of links
     */
    protected abstract List<Link<C>> constructLinks(InterfaceDescription description);

    /**
     * Sets the types of provided interfaces of components in the architecture model.
     *
     * @param model       architecture model
     * @param description dependency description
     */
    protected void setComponentInterfaceTypes(M model, InterfaceDescription description) {
        for (DescriptionComponent descComponent : description.getComponents()) {
            model.getComponent(descComponent.getName()).ifPresent(component ->
                    component.setProvidedInterfaceType(descComponent.getProvides().getInterfaceType()));
        }
    }

    /**
     * Validates the architecture model.
     *
     * @param model architecture model to validate
     * @throws ArchitectureGeneratorException if model is invalid
     */
    protected void validate(M model) throws ArchitectureGeneratorException {
        try {
            model.validate();
        } catch (Exception e) {
            throw new ArchitectureGeneratorException("Error while validating the architecture model: %s".formatted(e.getMessage()));
        }
    }

}
