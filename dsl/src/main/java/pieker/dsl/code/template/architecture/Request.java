package pieker.dsl.code.template.architecture;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.VelocityContext;
import pieker.common.Template;
import pieker.dsl.code.Engine;
import pieker.dsl.code.exception.PiekerProcessingException;
import pieker.dsl.code.preprocessor.FileManager;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;

import static pieker.dsl.util.CodeUtil.jsonToMap;

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
    private String headers = "{}";
    @JsonIgnore
    private String requestBody = "{}";


    public Request(String service, String parameter) {
        this.service = service;
        this.parameter = parameter.trim();
    }

    @Override
    public String sendTraffic(String[] args) {
        try{
            URL urlInstance = new URI(this.url).toURL();
            HttpURLConnection connection = (HttpURLConnection) urlInstance.openConnection();

            // Set request method and timeouts
            connection.setRequestMethod(this.method.toUpperCase());
            connection.setConnectTimeout(this.connectionTimeout);
            connection.setReadTimeout(this.readTimeout);
            connection.setDoOutput(true);

            // Set headers

            for (Map.Entry<String, String> header : jsonToMap(this.headers).entrySet()) {
                connection.setRequestProperty(header.getKey(), header.getValue());
            }

            // Write request body if applicable
            InputStream inputStream = getInputStream(requestBody, connection);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            }
        } catch (Exception e){
            log.error("exception occurred while sending request to service {}. Exception: {}", this.service, e.getMessage());
            return "ERROR ON REQUEST";
        }
    }

    @Override
    public void addContextVariable(VelocityContext ctx) {
        ctx.put("trafficType", "request");
        ctx.put("requestUrl", this.url);
        ctx.put("requestMethod", this.method);
        ctx.put("requestConnTimeout", this.connectionTimeout);
        ctx.put("requestReadTimeout", this.readTimeout);
        ctx.put("requestHeaders", this.headers);
        ctx.put("requestBody", this.requestBody);
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
                    case "body" -> this.requestBody = node.get(field).toString();
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

    @JsonIgnore
    private static InputStream getInputStream(String requestBody, HttpURLConnection connection) throws IOException {
        if (requestBody != null && !requestBody.isEmpty()) {
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
        }

        // Read response
        return (connection.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST)
                ? connection.getInputStream() : connection.getErrorStream();
    }
}
