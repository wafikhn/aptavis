package com.aptavis.projecttracker.frontend.client;

import com.aptavis.projecttracker.frontend.constant.ApiConstants;
import com.aptavis.projecttracker.frontend.model.ProjectModel;
import com.aptavis.projecttracker.frontend.model.TaskModel;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ProjectApiClient extends RestEndpoint {

    public List<ProjectModel> findAllProjects() {
        return getList(ApiConstants.ENDPOINT_PROJECTS, new TypeReference<List<ProjectModel>>() {});
    }

    public ProjectModel saveProject(ProjectModel projectModel) {
        return post(ApiConstants.ENDPOINT_PROJECTS, projectModel, ProjectModel.class);
    }

    public ApiResponse<ProjectModel> saveProjectWithResponse(ProjectModel projectModel) {
        return postWithResponse(ApiConstants.ENDPOINT_PROJECTS, projectModel, ProjectModel.class);
    }

    public boolean deleteProject(Long projectId) {
        if (projectId == null) return false;
        return delete(ApiConstants.ENDPOINT_PROJECTS + "/" + projectId);
    }

    public TaskModel saveTask(TaskModel taskModel) {
        return post(ApiConstants.ENDPOINT_TASKS, taskModel, TaskModel.class);
    }

    public ApiResponse<TaskModel> saveTaskWithResponse(TaskModel taskModel) {
        return postWithResponse(ApiConstants.ENDPOINT_TASKS, taskModel, TaskModel.class);
    }

    public boolean deleteTask(Long taskId) {
        if (taskId == null) return false;
        return delete(ApiConstants.ENDPOINT_TASKS + "/" + taskId);
    }
}
