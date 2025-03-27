package pieker.common.connection;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class Http {

    private Http(){}

    public static String send(String service, String url, String method, int connectionTimeout, int readTimeout,
                       String jsonHeaders, String requestBody) {
        try{
            URL urlInstance = new URI(url).toURL();
            HttpURLConnection connection = (HttpURLConnection) urlInstance.openConnection();

            // Set request method and timeouts
            connection.setRequestMethod(method);
            connection.setConnectTimeout(connectionTimeout);
            connection.setReadTimeout(readTimeout);
            connection.setDoOutput(true);

            // Set headers

            for (Map.Entry<String, String> header : jsonToMap(jsonHeaders).entrySet()) {
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
            log.error("exception occurred while sending request to service {}. Exception: {}", service, e.getMessage());
            return "ERROR ON REQUEST";
        }
    }


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

    public static Map<String, String> jsonToMap(String jsonString) {
        Map<String, String> map = new HashMap<>();
        if (jsonString == null || jsonString.isEmpty()) {
            return map;
        }
        JSONObject jsonObject = new JSONObject(jsonString);
        for (String key : jsonObject.keySet()) {
            map.put(key, jsonObject.getString(key));
        }
        return map;
    }
}
