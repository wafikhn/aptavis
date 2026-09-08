package com.aptavis.projecttracker.frontend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TaskModel(
    Long taskId,
    String name,
    TaskStatus status,
    Integer weight,
    Long projectId,
    Long parentTaskId,
    List<TaskModel> subtasks
) {
    public TaskModel(String name, TaskStatus status, Integer weight, Long projectId, Long parentTaskId) {
        this(null, name, status, weight, projectId, parentTaskId, List.of());
    }

    public TaskModel(String name, TaskStatus status, Integer weight, Long projectId) {
        this(null, name, status, weight, projectId, null, List.of());
    }
}
