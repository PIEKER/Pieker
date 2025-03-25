package pieker.code.generators;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.FileWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;

/**
 * Processor class for Apache Velocity templates.
 */
public class VelocityTemplateProcessor {

    private final VelocityEngine velocityEngine;
    private final String templateDirectory;
    protected final String outputDirectory;

    public VelocityTemplateProcessor() {
        this.templateDirectory = "templates/";
        this.outputDirectory = ".gen/";
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
     * @throws Exception If an error occurs during file writing
     */
    public void processTemplate(String templatePath, VelocityContext context, String fileName) throws Exception {
        Template template = loadTemplate(templatePath);
        String content = fillTemplate(template, context);
        saveToFile(content, this.outputDirectory + fileName);
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
     * @throws Exception If an error occurs during file writing
     */
    public void saveToFile(String content, String fileName) throws Exception {
        Files.createDirectories(Path.of(this.outputDirectory + fileName).getParent());
        try (FileWriter writer = new FileWriter(this.outputDirectory + fileName)) {
            writer.write(content);
        }
    }

    /**
     * Saves the processed template to a file.
     *
     * @param content    Content string
     * @param outputPath File path to save content to
     * @throws Exception If an error occurs during file writing
     */
    public void saveToFileAt(String content, Path outputPath) throws Exception {
        Files.createDirectories(outputPath.getParent());
        try (FileWriter writer = new FileWriter(outputPath.toString())) {
            writer.write(content);
        }
    }

}
