package pieker.dsl.code.template.architecture;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.VelocityContext;
import pieker.common.Template;
import pieker.common.connection.Http;
import pieker.dsl.code.Engine;
import pieker.dsl.code.exception.PiekerProcessingException;
import pieker.dsl.code.preprocessor.FileManager;

import java.util.Iterator;

@Slf4j
@Getter
public class Request implements Template, TrafficType {

    private final String name = Request.class.getSimpleName();

    private final String service;
    private final String parameter;

    // Parameter
    @JsonIgnore
    private String url = "/";
    @JsonIgnore
    private String method = "GET";
    @JsonIgnore
    private int connectionTimeout = 0;
    @JsonIgnore
    private int readTimeout = 0;
    @JsonIgnore
    private String headers = "";
    @JsonIgnore
    private String body = "";


    public Request(String service, String parameter) {
        this.service = service;
        this.parameter = parameter.trim();
    }

    @Override
    public String sendTraffic(String[] args) {
        return Http.send(this.service, this.url, this.method, this.connectionTimeout, this.readTimeout, this.headers, this.body);
    }

    @Override
    public void addContextVariable(VelocityContext ctx) {
        try{
            ObjectMapper om = new ObjectMapper();
            ctx.put("trafficType", "request");
            ctx.put("requestUrl", this.url);
            ctx.put("requestMethod", this.method);
            ctx.put("requestConnTimeout", this.connectionTimeout);
            ctx.put("requestReadTimeout", this.readTimeout);
            ctx.put("requestHeaders", om.writeValueAsString(this.headers));
            ctx.put("requestBody", om.writeValueAsString(this.body));
        } catch(JsonProcessingException e){
            throw new PiekerProcessingException("unable escape characters for header/body: " + e.getMessage());
        }
    }

    @Override
    public void translateParameters() {
        if (this.parameter.startsWith(FileManager.PREFIX)) {
            String data = Engine.getFileManager().getDataFromFileHash(this.parameter);
            this.convertJsonToValues(data);
        } else {
            this.convertJsonToValues(this.parameter);
        }
    }

    private void convertJsonToValues(String json){
        try{
            ObjectMapper om = new ObjectMapper();
            JsonNode node = om.readTree(json);

            for (Iterator<String> it = node.fieldNames(); it.hasNext(); ) {
                String field = it.next();
                switch (field){
                    case "url" -> this.url = node.get(field).asText();
                    case "method" -> this.method = node.get(field).asText();
                    case "connectionTimeout" -> this.connectionTimeout = node.get(field).asInt();
                    case "readTimeout" -> this.readTimeout = node.get(field).asInt();
                    case "headers" -> this.headers = node.get(field).toString();
                    case "body" -> this.body = node.get(field).toString();
                    default -> log.warn("unknown field used in request json: '{}'", field);
                }
            }
        } catch(JsonProcessingException e){
            throw new PiekerProcessingException("unable to process file data. Invalid Json provided.");
        }
    }

    @Override
    public String getTarget() {
        return this.service;
    }

    /**
     * Used by JsonMapper to convert Object to JSON
     * @return String of parameter json.
     */
    public String getParameter(){
        if (this.parameter.startsWith(FileManager.PREFIX)) {
            return Engine.getFileManager().getDataFromFileHash(this.parameter);
        }
        return this.parameter;
    }
}
