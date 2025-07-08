package pieker.common.connection;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Http {
    private static final Gson GSON = new Gson();

    private Http(){}

    public static ResponseTuple send(String service, String url, String method, int connectionTimeout, int readTimeout,
                       String jsonHeaders, String requestBody, String bodyType) {
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(connectionTimeout, TimeUnit.MILLISECONDS)
                    .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
                    .build();

            // Parse headers
            Map<String, String> headersMap = jsonToMap(jsonHeaders);
            Headers.Builder headersBuilder = new Headers.Builder();
            for (Map.Entry<String, String> entry : headersMap.entrySet()) {
                headersBuilder.add(entry.getKey(), entry.getValue());
            }

            // Build request body based on bodyType
            RequestBody body = null;
            if (method.equalsIgnoreCase("POST") || method.equalsIgnoreCase("PUT")) {
                Map<String, Object> bodyMap = !bodyType.equalsIgnoreCase("text") ? jsonToMapObject(requestBody): new HashMap<>();

                switch (bodyType.toLowerCase()) {
                    case "json":
                        String json = GSON.toJson(bodyMap);
                        body = RequestBody.create(json, MediaType.parse("application/json"));
                        break;

                    case "form":
                        FormBody.Builder formBuilder = new FormBody.Builder();
                        for (Map.Entry<String, Object> entry : bodyMap.entrySet()) {
                            formBuilder.add(entry.getKey(), entry.getValue().toString());
                        }
                        body = formBuilder.build();
                        break;

                    case "multipart":
                        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                        for (Map.Entry<String, Object> entry : bodyMap.entrySet()) {
                            multipartBuilder.addFormDataPart(entry.getKey(), entry.getValue().toString());
                        }
                        body = multipartBuilder.build();
                        break;
                    case "text":
                        body = RequestBody.create(requestBody, MediaType.parse("text/plain"));
                        break;

                    default:
                        throw new IllegalArgumentException("Unsupported body type: " + bodyType);
                }
            }

            Request.Builder requestBuilder = new Request.Builder()
                    .url(url)
                    .method(method, body)
                    .headers(headersBuilder.build());

            Response response = client.newCall(requestBuilder.build()).execute();

            if (response.body() != null) {
                return new ResponseTuple(response.body().string(), response.code());
            } else {
                return new ResponseTuple("", response.code());
            }
        } catch (Exception e) {
            String message = "Exception occurred while sending request to service " + service + ". Exception: " + e.getMessage();
            log.error(message);
            return new ResponseTuple(message, 500);
        }
    }

    private static Map<String, Object> jsonToMapObject(String json) {
        if (json == null || json.trim().isEmpty()) {
            return Map.of();
        }
        return GSON.fromJson(json, new TypeToken<Map<String, Object>>(){}.getType());
    }

    private static Map<String, String> jsonToMap(String json) {
        if (json == null || json.trim().isEmpty()) {
            return Map.of();
        }
        return GSON.fromJson(json, new TypeToken<Map<String, String>>(){}.getType());
    }
}
