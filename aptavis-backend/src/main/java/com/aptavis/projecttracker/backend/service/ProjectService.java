package com.aptavis.projecttracker.backend.service;

import com.aptavis.projecttracker.backend.constant.ProjectConstants;
import com.aptavis.projecttracker.backend.constant.TaskConstants;
import com.aptavis.projecttracker.backend.domain.Project;
import com.aptavis.projecttracker.backend.domain.ProjectId;
import com.aptavis.projecttracker.backend.domain.ProjectStatus;
import com.aptavis.projecttracker.backend.domain.Task;
import com.aptavis.projecttracker.backend.domain.TaskStatus;
import com.aptavis.projecttracker.backend.mapper.ProjectMapper;
import com.aptavis.projecttracker.backend.model.ProjectModel;
import com.aptavis.projecttracker.backend.repository.ProjectRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
@Transactional
public class ProjectService {

    @Inject
    private ProjectRepository projectRepository;

    public List<Project> findAllProjects() {
        return projectRepository.findAll();
    }

    public List<ProjectModel> getAllProjects() {
        return findAllProjects().stream()
                .map(ProjectMapper::toModel)
                .collect(Collectors.toList());
    }

    public Optional<Project> findProjectById(ProjectId id) {
        return projectRepository.findById(id);
    }

    public Response getProjectById(Long projectId) {
        return findProjectById(ProjectId.of(projectId))
                .map(project -> Response.ok(ProjectMapper.toModel(project)).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    public Project saveProject(Project project) {
        recalculateProjectMetrics(project);
        return projectRepository.save(project);
    }

    public Response saveProject(ProjectModel model) {
        if (!isValidProjectName(model)) {
            return Response.status(Response.Status.BAD_REQUEST).entity(ProjectConstants.ERROR_NAME_REQUIRED).build();
        }

        if (model.startDate() != null && model.endDate() != null) {
            if (model.startDate().isAfter(model.endDate())) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Tanggal mulai tidak boleh lebih setelah tanggal selesai!")
                        .build();
            }

            for (Project existing : findAllProjects()) {
                if (model.projectId() != null && existing.getProjectId() != null &&
                        existing.getProjectId().getProjectId().equals(model.projectId())) {
                    continue; // Skip current project being updated
                }

                if (existing.getStartDate() != null && existing.getEndDate() != null) {
                    boolean overlaps = !model.startDate().isAfter(existing.getEndDate()) &&
                            !model.endDate().isBefore(existing.getStartDate());
                    if (overlaps) {
                        String errorMsg = String.format("Jadwal project berbenturan dengan project '%s' (%s s/d %s)",
                                existing.getName(), existing.getStartDate(), existing.getEndDate());
                        return Response.status(Response.Status.BAD_REQUEST).entity(errorMsg).build();
                    }
                }
            }
        }

        Project project = resolveOrCreateProject(model);
        project.setName(model.name().trim());
        project.setStartDate(model.startDate());
        project.setEndDate(model.endDate());

        Project saved = saveProject(project);
        return Response.ok(ProjectMapper.toModel(saved)).build();
    }

    public void deleteProject(ProjectId id) {
        projectRepository.delete(id);
    }

    public Response deleteProject(Long projectId) {
        deleteProject(ProjectId.of(projectId));
        return Response.noContent().build();
    }

    /**
     * Domain business logic: calculates completion progress % and project status auto-transitions.
     */
    public void recalculateProjectMetrics(Project project) {
        if (project == null) return;

        List<Task> tasks = project.getTasks();
        if (tasks == null || tasks.isEmpty()) {
            resetProjectMetrics(project);
            return;
        }

        project.setCompletionProgress(calculateCompletionProgress(tasks));
        project.setStatus(determineProjectStatus(tasks));
    }

    private boolean isValidProjectName(ProjectModel model) {
        return model != null && model.name() != null && !model.name().trim().isEmpty();
    }

    private Project resolveOrCreateProject(ProjectModel model) {
        if (model.projectId() == null) {
            return new Project();
        }
        return findProjectById(ProjectId.of(model.projectId()))
                .orElseGet(() -> new Project(
                        ProjectId.of(model.projectId()),
                        model.name().trim(),
                        ProjectStatus.DRAFT,
                        ProjectConstants.INITIAL_PROGRESS,
                        new ArrayList<>()
                ));
    }

    private void resetProjectMetrics(Project project) {
        project.setCompletionProgress(ProjectConstants.INITIAL_PROGRESS);
        project.setStatus(ProjectStatus.DRAFT);
    }

    private double calculateCompletionProgress(List<Task> tasks) {
        int totalWeight = tasks.stream().mapToInt(this::extractWeight).sum();
        int doneWeight = tasks.stream().filter(this::isTaskDone).mapToInt(this::extractWeight).sum();

        return totalWeight > TaskConstants.MIN_WEIGHT
                ? ((double) doneWeight / totalWeight) * ProjectConstants.PERCENTAGE_MULTIPLIER
                : ProjectConstants.INITIAL_PROGRESS;
    }

    private ProjectStatus determineProjectStatus(List<Task> tasks) {
        boolean allDraft = tasks.stream().allMatch(t -> t.getStatus() == TaskStatus.DRAFT);
        boolean allDone = tasks.stream().allMatch(t -> t.getStatus() == TaskStatus.DONE);

        if (allDraft) return ProjectStatus.DRAFT;
        if (allDone) return ProjectStatus.DONE;
        return ProjectStatus.IN_PROGRESS;
    }

    private int extractWeight(Task task) {
        return task.getWeight() != null ? task.getWeight() : TaskConstants.MIN_WEIGHT;
    }

    private boolean isTaskDone(Task task) {
        return task.getStatus() == TaskStatus.DONE;
    }
}
