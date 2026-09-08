package com.aptavis.projecttracker.frontend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
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
) {
    public ProjectModel(String name) {
        this(null, name, ProjectStatus.DRAFT, 0.0, null, null, List.of());
    }
}
