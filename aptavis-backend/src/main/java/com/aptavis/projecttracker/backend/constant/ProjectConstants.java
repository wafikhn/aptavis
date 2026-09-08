package com.aptavis.projecttracker.backend.constant;

public final class ProjectConstants {

    private ProjectConstants() {}

    // Validation & Error Messages
    public static final String ERROR_NAME_REQUIRED = "Name cannot be empty";
    public static final String ERROR_PROJECT_ID_REQUIRED = "ProjectId is required";
    public static final String ERROR_PROJECT_NOT_FOUND = "Project not found";

    // Metrics Constants
    public static final double INITIAL_PROGRESS = 0.0;
    public static final double PERCENTAGE_MULTIPLIER = 100.0;
}
