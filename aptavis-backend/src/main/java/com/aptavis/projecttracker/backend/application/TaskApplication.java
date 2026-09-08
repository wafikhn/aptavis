package com.aptavis.projecttracker.backend.application;

import com.aptavis.projecttracker.backend.model.TaskModel;
import com.aptavis.projecttracker.backend.service.TaskService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/tasks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TaskApplication {

    @Inject
    private TaskService taskService;

    @POST
    public Response saveTask(TaskModel model) {
        return taskService.saveTask(model);
    }

    @DELETE
    @Path("/{taskId}")
    public Response deleteTask(@PathParam("taskId") Long taskId) {
        return taskService.deleteTask(taskId);
    }
}
