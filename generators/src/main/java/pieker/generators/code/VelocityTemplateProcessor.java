package pieker.generators.code;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;

/**
 * Processor class for Apache Velocity templates.
 */
public class VelocityTemplateProcessor {

    protected final String outputDirectory;
    private final VelocityEngine velocityEngine;
    private final String templateDirectory;

    public VelocityTemplateProcessor() {
        this.templateDirectory = "templates/";
        this.outputDirectory = System.getProperty("projectRoot") + "/" + System.getProperty("genDir") + "/" + System.getProperty("executionName");
        this.velocityEngine = initVelocityEngine();
    }

    public VelocityTemplateProcessor(String outputDirectory) {
        this.templateDirectory = "templates/";
        this.outputDirectory = outputDirectory;
        this.velocityEngine = initVelocityEngine();
    }

    public VelocityTemplateProcessor(String templateDirectory, String outputDirectory) {
        this.templateDirectory = templateDirectory;
        this.outputDirectory = outputDirectory;
        this.velocityEngine = initVelocityEngine();
    }

    private VelocityEngine initVelocityEngine() {
        Properties properties = new Properties();
        properties.setProperty("resource.loaders", "classpath");
        properties.setProperty("resource.loader.classpath.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        VelocityEngine engine = new VelocityEngine(properties);
        engine.init();
        return engine;
    }

    /**
     * Loads the given Velocity template.
     *
     * @param templateName Name of template file (relative to template directory)
     * @return loaded template
     */
    public Template loadTemplate(String templateName) {
        return loadTemplateAt(this.templateDirectory + templateName);
    }

    /**
     * Loads the given Velocity template.
     *
     * @param templatePath Path to template file
     * @return loaded template
     */
    public Template loadTemplateAt(String templatePath) {
        return velocityEngine.getTemplate(templatePath);
    }

    /**
     * Processes the given template with provided data and saves it to a file with the given name.
     *
     * @param templatePath Path to template file
     * @param context      Velocity context containing data for filling template
     * @param fileName     Name of the file to save the processed template to
     * @throws IOException If an error occurs during file writing
     */
    public void processTemplate(String templatePath, VelocityContext context, String fileName) throws IOException {
        Template template = loadTemplate(templatePath);
        String content = fillTemplate(template, context);
        saveToFile(content, this.outputDirectory + fileName);
    }

    /**
     * Processes the given template with provided data and saves it to a file with the given name.
     *
     * @param templatePath    Path to template file
     * @param context         Velocity context containing data for filling template
     * @param fileName        Name of the file to save the processed template to
     * @param outputDirectory Directory to save the file to
     * @throws IOException If an error occurs during file writing
     */
    public void processTemplate(String templatePath, VelocityContext context, String fileName, String outputDirectory) throws IOException {
        Template template = loadTemplate(templatePath);
        String content = fillTemplate(template, context);
        saveToFileAt(content, Path.of(outputDirectory + fileName));
    }

    /**
     * Fills the given template with provided data.
     *
     * @param template Loaded Velocity template
     * @param values   Map containing key-value pairs for filling template
     * @return processed template as a String
     */
    public String fillTemplate(Template template, Map<String, Object> values) {
        VelocityContext context = new VelocityContext();
        values.forEach(context::put);
        return fillTemplate(template, context);
    }

    /**
     * Fills the given template with provided data.
     *
     * @param template Loaded Velocity template
     * @param context  Velocity context containing data for filling template
     * @return processed template as a String
     */
    public String fillTemplate(Template template, VelocityContext context) {
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        return writer.toString();
    }

    /**
     * Saves the processed template to a file.
     *
     * @param content  Content string
     * @param fileName File name to save content to (uses output directory)
     * @throws IOException If an error occurs during file writing
     */
    public void saveToFile(String content, String fileName) throws IOException {
        try {
            Files.createDirectories(Path.of(this.outputDirectory + fileName).getParent());
            try (FileWriter writer = new FileWriter(this.outputDirectory + fileName)) {
                writer.write(content);
            }
        } catch (Exception e) {
            throw new IOException("Failed to save file: " + e.getMessage());
        }
    }

    /**
     * Saves the processed template to a file.
     *
     * @param content    Content string
     * @param outputPath File path to save content to
     * @throws IOException If an error occurs during file writing
     */
    public void saveToFileAt(String content, Path outputPath) throws IOException {
        try {
            Files.createDirectories(outputPath.getParent());
            try (FileWriter writer = new FileWriter(outputPath.toString())) {
                writer.write(content);
            }
        } catch (Exception e) {
            throw new IOException("Failed to save file: " + e.getMessage());
        }

    }

}
