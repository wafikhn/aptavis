package com.aptavis.projecttracker.backend.mapper;

import com.aptavis.projecttracker.backend.domain.Task;
import com.aptavis.projecttracker.backend.model.TaskModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TaskMapper {

    public static TaskModel toModel(Task task) {
        if (task == null) return null;
        List<TaskModel> subtaskModels = task.getSubtasks() != null ?
                task.getSubtasks().stream().map(TaskMapper::toModel).collect(Collectors.toList()) :
                new ArrayList<>();

        Long parentId = task.getParentTask() != null && task.getParentTask().getTaskId() != null ?
                task.getParentTask().getTaskId().getTaskId() : null;

        return new TaskModel(
            task.getTaskId() != null ? task.getTaskId().getTaskId() : null,
            task.getName(),
            task.getStatus(),
            task.getWeight(),
            task.getProject() != null && task.getProject().getProjectId() != null ? task.getProject().getProjectId().getProjectId() : null,
            parentId,
            subtaskModels
        );
    }
}
