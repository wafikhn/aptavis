package com.aptavis.projecttracker.frontend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TaskModel(
    Long taskId,
    String name,
    TaskStatus status,
    Integer weight,
    Long projectId
) {
    public TaskModel(String name, TaskStatus status, Integer weight, Long projectId) {
        this(null, name, status, weight, projectId);
    }
}
