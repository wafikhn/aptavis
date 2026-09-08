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
        this.objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    }

    private String resolveBaseUrl() {
        String rawUrl = getRawBaseUrl();
        if (rawUrl == null || rawUrl.isBlank()) {
            throw new IllegalStateException(ApiConstants.ERROR_ENV_CONFIG_MISSING);
        }
        rawUrl = rawUrl.trim();
        while (rawUrl.endsWith("/")) {
            rawUrl = rawUrl.substring(0, rawUrl.length() - 1);
        }
        return rawUrl;
    }

    private String getRawBaseUrl() {
        // 1. Check System Property (WildFly system-properties)
        String sysProp = System.getProperty(ApiConstants.ENV_BACKEND_API_URL);
        if (sysProp != null && !sysProp.isBlank()) {
            return sysProp;
        }

        // 2. Check Environment Variable
        String envVar = System.getenv(ApiConstants.ENV_BACKEND_API_URL);
        if (envVar != null && !envVar.isBlank()) {
            return envVar;
        }

        // 3. Read from .env file in root project / working directory
        return readUrlFromDotEnv();
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

    protected String buildFullUrl(String path) {
        String cleanPath = path != null ? path.trim() : "";
        if (!cleanPath.startsWith("/")) {
            cleanPath = "/" + cleanPath;
        }
        return baseUrl + cleanPath;
    }

    protected <T> List<T> getList(String path, TypeReference<List<T>> typeRef) {
        try {
            String fullUrl = buildFullUrl(path);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(fullUrl))
                    .header(ApiConstants.HEADER_ACCEPT, ApiConstants.MEDIA_TYPE_JSON)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), typeRef);
            }
            System.err.println("REST GET " + fullUrl + " failed with HTTP " + response.statusCode() + ": " + response.body());
        } catch (Exception e) {
            System.err.println("REST GET exception: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    protected <T> T post(String path, Object body, Class<T> responseClass) {
        try {
            String fullUrl = buildFullUrl(path);
            String json = objectMapper.writeValueAsString(body);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(fullUrl))
                    .header(ApiConstants.HEADER_CONTENT_TYPE, ApiConstants.MEDIA_TYPE_JSON)
                    .header(ApiConstants.HEADER_ACCEPT, ApiConstants.MEDIA_TYPE_JSON)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200 || response.statusCode() == 201) {
                return objectMapper.readValue(response.body(), responseClass);
            }
            System.err.println("REST POST " + fullUrl + " failed with HTTP " + response.statusCode() + ": " + response.body());
        } catch (Exception e) {
            System.err.println("REST POST exception: " + e.getMessage());
        }
        return null;
    }

    public static class ApiResponse<T> {
        private final T data;
        private final String error;
        private final int statusCode;

        public ApiResponse(T data, String error, int statusCode) {
            this.data = data;
            this.error = error;
            this.statusCode = statusCode;
        }

        public boolean isSuccess() {
            return statusCode >= 200 && statusCode < 300 && data != null;
        }

        public T getData() { return data; }
        public String getError() { return error; }
        public int getStatusCode() { return statusCode; }
    }

    protected <T> ApiResponse<T> postWithResponse(String path, Object body, Class<T> responseClass) {
        try {
            String fullUrl = buildFullUrl(path);
            String json = objectMapper.writeValueAsString(body);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(fullUrl))
                    .header(ApiConstants.HEADER_CONTENT_TYPE, ApiConstants.MEDIA_TYPE_JSON)
                    .header(ApiConstants.HEADER_ACCEPT, ApiConstants.MEDIA_TYPE_JSON)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200 || response.statusCode() == 201) {
                T data = objectMapper.readValue(response.body(), responseClass);
                return new ApiResponse<>(data, null, response.statusCode());
            }
            return new ApiResponse<>(null, response.body(), response.statusCode());
        } catch (Exception e) {
            return new ApiResponse<>(null, e.getMessage(), 500);
        }
    }

    protected boolean delete(String path) {
        try {
            String fullUrl = buildFullUrl(path);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(fullUrl))
                    .DELETE()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return true;
            }
            System.err.println("REST DELETE " + fullUrl + " failed with HTTP " + response.statusCode() + ": " + response.body());
        } catch (Exception e) {
            System.err.println("REST DELETE exception: " + e.getMessage());
        }
        return false;
    }
}
