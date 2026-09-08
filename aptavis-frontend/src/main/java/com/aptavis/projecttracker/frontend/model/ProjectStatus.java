package com.aptavis.projecttracker.frontend.model;

public enum ProjectStatus {
    DRAFT("Draft"),
    IN_PROGRESS("In Progress"),
    DONE("Done");

    private final String displayName;

    ProjectStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
