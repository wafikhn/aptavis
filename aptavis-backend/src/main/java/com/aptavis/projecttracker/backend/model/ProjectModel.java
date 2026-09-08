package com.aptavis.projecttracker.backend.model;

import com.aptavis.projecttracker.backend.domain.ProjectStatus;

import java.time.LocalDate;
import java.util.List;

public record ProjectModel(
    Long projectId,
    String name,
    ProjectStatus status,
    double completionProgress,
    LocalDate startDate,
    LocalDate endDate,
    List<TaskModel> tasks
) {}
