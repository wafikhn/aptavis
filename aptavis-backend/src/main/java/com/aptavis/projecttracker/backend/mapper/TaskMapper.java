package com.aptavis.projecttracker.backend.mapper;

import com.aptavis.projecttracker.backend.domain.Task;
import com.aptavis.projecttracker.backend.model.TaskModel;

public class TaskMapper {

    public static TaskModel toModel(Task task) {
        if (task == null) return null;
        return new TaskModel(
            task.getTaskId() != null ? task.getTaskId().getTaskId() : null,
            task.getName(),
            task.getStatus(),
            task.getWeight(),
            task.getProject() != null && task.getProject().getProjectId() != null ? task.getProject().getProjectId().getProjectId() : null
        );
    }
}
