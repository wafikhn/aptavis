package com.aptavis.projecttracker.backend.mapper;

import com.aptavis.projecttracker.backend.domain.Project;
import com.aptavis.projecttracker.backend.model.ProjectModel;
import com.aptavis.projecttracker.backend.model.TaskModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectMapper {

    public static ProjectModel toModel(Project project) {
        if (project == null) return null;
        List<TaskModel> taskModels = project.getTasks() != null ?
                project.getTasks().stream().map(TaskMapper::toModel).collect(Collectors.toList()) :
                new ArrayList<>();
        return new ProjectModel(
            project.getProjectId() != null ? project.getProjectId().getProjectId() : null,
            project.getName(),
            project.getStatus(),
            project.getCompletionProgress(),
            taskModels
        );
    }
}
