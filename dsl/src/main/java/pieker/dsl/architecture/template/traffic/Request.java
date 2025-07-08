package pieker.dsl.architecture.template.traffic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.VelocityContext;
import pieker.common.Template;
import pieker.common.connection.Http;
import pieker.common.connection.ResponseTuple;
import pieker.dsl.architecture.Engine;
import pieker.dsl.architecture.exception.PiekerProcessingException;
import pieker.dsl.architecture.preprocessor.FileManager;

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
    private int connectionTimeout = 3000;
    @JsonIgnore
    private int readTimeout = 5000;
    @JsonIgnore
    private String headers = "{}";
    @JsonIgnore
    private String body = "";
    @JsonIgnore
    private String bodyType = "json";


    public Request(String service, String parameter) {
        this.service = service;
        this.parameter = parameter.trim();
    }

    @Override
    public ResponseTuple sendTraffic(String[] args) {
        if (args.length == 0){
            throw new IllegalArgumentException("no arguments provided for request traffic");
        }
        String endpointUrl = "/" + this.service + "/" + args[1] + this.url;
        return Http.send(this.service, args[0] + endpointUrl, this.method, this.connectionTimeout, this.readTimeout, this.headers, this.body, this.bodyType);
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
            ctx.put("requestBodyType", this.bodyType);
        } catch(JsonProcessingException e){
            throw new PiekerProcessingException("unable escape characters for header/body: " + e.getMessage());
        }
    }

    @Override
    public void translateParameters() {
        this.convertJsonToValues(this.getParameter());
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
                    case "bodyType" -> this.bodyType = node.get(field).asText();
                    default -> log.warn("unknown field used in request json: '{}'", field);
                }
            }
            if (this.bodyType.equals("text")) this.body = this.body.replace("\"", "");
        } catch(JsonProcessingException e){
            throw new PiekerProcessingException("unable to process file data. Invalid Json provided: " + e.getMessage());
        }
    }

    @Override
    public String getTarget() {
        return this.service;
    }

    /**
     * Resolves parameter from a stored String value, including possible file referencing.
     * @return String of parameter JSON.
     */
    public String getParameter(){
        if (this.parameter.startsWith(FileManager.PREFIX)) {
            return Engine.getFileManager().getDataFromFileHash(this.parameter);
        }
        return this.parameter;
    }
}
