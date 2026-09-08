package com.aptavis.projecttracker.frontend.constant;

public final class ApiConstants {

    private ApiConstants() {}

    // Configuration & Environment
    public static final String ENV_BACKEND_API_URL = "BACKEND_API_URL";
    public static final String ENV_FILE_PREFIX = "BACKEND_API_URL=";
    public static final String[] ENV_FILE_PATHS = {".env", "../.env", "../../.env"};

    // Endpoints & Paths
    public static final String ENDPOINT_PROJECTS = "/projects";
    public static final String ENDPOINT_TASKS = "/tasks";

    // HTTP Headers & Media Types
    public static final String HEADER_ACCEPT = "Accept";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String MEDIA_TYPE_JSON = "application/json";

    // Errors
    public static final String ERROR_ENV_CONFIG_MISSING = "BACKEND_API_URL is not configured in system properties, environment variables, or .env file";
}
