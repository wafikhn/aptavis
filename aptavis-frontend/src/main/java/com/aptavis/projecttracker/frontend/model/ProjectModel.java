package com.aptavis.projecttracker.frontend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProjectModel(
    Long projectId,
    String name,
    ProjectStatus status,
    double completionProgress,
    List<TaskModel> tasks
) {
    public ProjectModel(String name) {
        this(null, name, ProjectStatus.DRAFT, 0.0, List.of());
    }
}
