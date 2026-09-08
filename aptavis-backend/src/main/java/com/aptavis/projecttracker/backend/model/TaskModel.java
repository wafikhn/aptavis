package com.aptavis.projecttracker.backend.model;

import com.aptavis.projecttracker.backend.domain.TaskStatus;

import java.util.List;

public record TaskModel(
    Long taskId,
    String name,
    TaskStatus status,
    Integer weight,
    Long projectId,
    Long parentTaskId,
    List<TaskModel> subtasks
) {}
