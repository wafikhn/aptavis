package com.aptavis.projecttracker.backend.service;

import com.aptavis.projecttracker.backend.constant.ProjectConstants;
import com.aptavis.projecttracker.backend.constant.TaskConstants;
import com.aptavis.projecttracker.backend.domain.Project;
import com.aptavis.projecttracker.backend.domain.ProjectId;
import com.aptavis.projecttracker.backend.domain.Task;
import com.aptavis.projecttracker.backend.domain.TaskId;
import com.aptavis.projecttracker.backend.domain.TaskStatus;
import com.aptavis.projecttracker.backend.mapper.TaskMapper;
import com.aptavis.projecttracker.backend.model.TaskModel;
import com.aptavis.projecttracker.backend.repository.ProjectRepository;
import com.aptavis.projecttracker.backend.repository.TaskRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

import java.util.Optional;

@ApplicationScoped
@Transactional
public class TaskService {

    @Inject
    private TaskRepository taskRepository;

    @Inject
    private ProjectRepository projectRepository;

    @Inject
    private ProjectService projectService;

    public Optional<Task> findTaskById(TaskId id) {
        return taskRepository.findById(id);
    }

    public Response saveTask(TaskModel model) {
        Response validationError = validateTaskModel(model);
        if (validationError != null) {
            return validationError;
        }

        Optional<Project> projectOpt = projectRepository.findById(ProjectId.of(model.projectId()));
        if (projectOpt.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity(ProjectConstants.ERROR_PROJECT_NOT_FOUND).build();
        }

        Project project = projectOpt.get();
        Task task = resolveOrCreateTask(model, project);
        applyTaskUpdates(task, model, project);

        Task savedTask = taskRepository.save(task);
        syncProjectAndMetrics(project, savedTask);

        return Response.ok(TaskMapper.toModel(savedTask)).build();
    }

    public Response deleteTask(Long taskId) {
        Optional<Task> taskOpt = taskRepository.findById(TaskId.of(taskId));
        if (taskOpt.isPresent()) {
            removeTaskFromProjectAndRepository(taskOpt.get(), taskId);
        }
        return Response.noContent().build();
    }

    private Response validateTaskModel(TaskModel model) {
        if (model == null || model.name() == null || model.name().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(ProjectConstants.ERROR_NAME_REQUIRED).build();
        }
        if (model.projectId() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(ProjectConstants.ERROR_PROJECT_ID_REQUIRED).build();
        }
        return null;
    }

    private Task resolveOrCreateTask(TaskModel model, Project project) {
        if (model.taskId() == null) {
            return new Task();
        }
        return taskRepository.findById(TaskId.of(model.taskId()))
                .orElseGet(() -> new Task(
                        TaskId.of(model.taskId()),
                        model.name().trim(),
                        model.status() != null ? model.status() : TaskStatus.DRAFT,
                        model.weight() != null ? model.weight() : TaskConstants.DEFAULT_TASK_WEIGHT,
                        project
                ));
    }

    private void applyTaskUpdates(Task task, TaskModel model, Project project) {
        task.setName(model.name().trim());
        task.setProject(project);
        if (model.status() != null) {
            task.setStatus(model.status());
        }
        if (model.weight() != null) {
            task.setWeight(model.weight());
        }
    }

    private void syncProjectAndMetrics(Project project, Task savedTask) {
        if (project.getTasks() != null && !project.getTasks().contains(savedTask)) {
            project.getTasks().add(savedTask);
        }
        projectService.recalculateProjectMetrics(project);
        projectRepository.save(project);
    }

    private void removeTaskFromProjectAndRepository(Task task, Long taskId) {
        Project project = task.getProject();
        if (project != null) {
            if (project.getTasks() != null) {
                project.getTasks().remove(task);
            }
            taskRepository.delete(TaskId.of(taskId));
            projectService.recalculateProjectMetrics(project);
            projectRepository.save(project);
        } else {
            taskRepository.delete(TaskId.of(taskId));
        }
    }
}
