package com.aptavis.projecttracker.backend.model;

import com.aptavis.projecttracker.backend.domain.TaskStatus;

public record TaskModel(
    Long taskId,
    String name,
    TaskStatus status,
    Integer weight,
    Long projectId
) {}
