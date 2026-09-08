package com.aptavis.projecttracker.frontend.client;

import com.aptavis.projecttracker.frontend.constant.ApiConstants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

public abstract class RestEndpoint {

    protected final String baseUrl;
    protected final HttpClient httpClient;
    protected final ObjectMapper objectMapper;

    public RestEndpoint() {
        this.baseUrl = resolveBaseUrl();
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    private String resolveBaseUrl() {
        // 1. Check System Property (WildFly system-properties)
        String sysProp = System.getProperty(ApiConstants.ENV_BACKEND_API_URL);
        if (sysProp != null && !sysProp.isBlank()) {
            return sysProp.trim();
        }

        // 2. Check Environment Variable
        String envVar = System.getenv(ApiConstants.ENV_BACKEND_API_URL);
        if (envVar != null && !envVar.isBlank()) {
            return envVar.trim();
        }

        // 3. Read from .env file in root project / working directory
        String dotEnvUrl = readUrlFromDotEnv();
        if (dotEnvUrl != null && !dotEnvUrl.isBlank()) {
            return dotEnvUrl.trim();
        }

        throw new IllegalStateException(ApiConstants.ERROR_ENV_CONFIG_MISSING);
    }

    private String readUrlFromDotEnv() {
        for (String pathStr : ApiConstants.ENV_FILE_PATHS) {
            Path path = Paths.get(pathStr);
            if (Files.exists(path)) {
                try {
                    List<String> lines = Files.readAllLines(path);
                    for (String line : lines) {
                        line = line.trim();
                        if (line.startsWith(ApiConstants.ENV_FILE_PREFIX)) {
                            return line.substring(ApiConstants.ENV_FILE_PREFIX.length()).trim();
                        }
                    }
                } catch (Exception ignored) {}
            }
        }
        return null;
    }

    protected <T> List<T> getList(String path, TypeReference<List<T>> typeRef) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + path))
                    .header(ApiConstants.HEADER_ACCEPT, ApiConstants.MEDIA_TYPE_JSON)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), typeRef);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    protected <T> T post(String path, Object body, Class<T> responseClass) {
        try {
            String json = objectMapper.writeValueAsString(body);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + path))
                    .header(ApiConstants.HEADER_CONTENT_TYPE, ApiConstants.MEDIA_TYPE_JSON)
                    .header(ApiConstants.HEADER_ACCEPT, ApiConstants.MEDIA_TYPE_JSON)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200 || response.statusCode() == 201) {
                return objectMapper.readValue(response.body(), responseClass);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void delete(String path) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + path))
                    .DELETE()
                    .build();

            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
