package com.aptavis.projecttracker.backend.model;

import com.aptavis.projecttracker.backend.domain.ProjectStatus;

import java.util.List;

public record ProjectModel(
    Long projectId,
    String name,
    ProjectStatus status,
    double completionProgress,
    List<TaskModel> tasks
) {}
