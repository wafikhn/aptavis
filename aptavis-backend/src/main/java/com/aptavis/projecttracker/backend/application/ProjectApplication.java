package com.aptavis.projecttracker.backend.application;

import com.aptavis.projecttracker.backend.model.ProjectModel;
import com.aptavis.projecttracker.backend.service.ProjectService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/projects")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProjectApplication {

    @Inject
    private ProjectService projectService;

    @GET
    public List<ProjectModel> getAllProjects() {
        return projectService.getAllProjects();
    }

    @GET
    @Path("/{projectId}")
    public Response getProjectById(@PathParam("projectId") Long projectId) {
        return projectService.getProjectById(projectId);
    }

    @POST
    public Response saveProject(ProjectModel model) {
        return projectService.saveProject(model);
    }

    @DELETE
    @Path("/{projectId}")
    public Response deleteProject(@PathParam("projectId") Long projectId) {
        return projectService.deleteProject(projectId);
    }
}
