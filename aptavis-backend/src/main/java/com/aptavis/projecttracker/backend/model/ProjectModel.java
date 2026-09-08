package com.aptavis.projecttracker.backend.model;

import com.aptavis.projecttracker.backend.domain.ProjectStatus;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.util.List;

public record ProjectModel(
    Long projectId,
    String name,
    ProjectStatus status,
    double completionProgress,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    LocalDate startDate,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    LocalDate endDate,
    List<TaskModel> tasks
) {}
