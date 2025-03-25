package pieker.code.generators;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.junit.jupiter.api.Test;

import java.util.Map;

class VelocityTemplateGeneratorTest {

    @Test
    void testBasicGen() throws Exception {
        VelocityTemplateProcessor velocityTemplateProcessor = new VelocityTemplateProcessor();

        Template t = velocityTemplateProcessor.loadTemplate("multistep/MultiStepProxy.vm");

        VelocityContext context = new VelocityContext();
        context.put("port" , 48999);
        Map<String, String> endpointJars = Map.of(
                "/task1" , "task1.jar" ,
                "/task2" , "task2.jar" ,
                "/task3" , "task3.jar"
        );
        context.put("endpoints" , endpointJars.keySet());
        context.put("endpointJars" , endpointJars);

        String content = velocityTemplateProcessor.fillTemplate(t, context);

        velocityTemplateProcessor.saveToFile(content, "MultiStepProxy.java");

    }

}
