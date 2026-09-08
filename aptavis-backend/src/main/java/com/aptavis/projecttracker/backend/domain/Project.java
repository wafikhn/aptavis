package com.aptavis.projecttracker.backend.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "projects")
@NamedQueries({
    @NamedQuery(
        name = Project.QUERY_FIND_ALL_WITH_TASKS,
        query = "SELECT DISTINCT p FROM Project p LEFT JOIN FETCH p.tasks ORDER BY p.projectId.projectId DESC"
    ),
    @NamedQuery(
        name = Project.QUERY_FIND_BY_ID,
        query = "SELECT p FROM Project p LEFT JOIN FETCH p.tasks WHERE p.projectId = :projectId"
    )
})
public class Project implements Serializable {

    public static final String QUERY_FIND_ALL_WITH_TASKS = "Project.findAllWithTasks";
    public static final String QUERY_FIND_BY_ID = "Project.findById";

    @EmbeddedId
    @AttributeOverride(name = "projectId", column = @Column(name = "project_id"))
    private ProjectId projectId;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectStatus status = ProjectStatus.DRAFT;

    @Column(name = "completion_progress", nullable = false)
    private double completionProgress = 0.0;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Task> tasks = new ArrayList<>();

    public Project() {}

    public Project(ProjectId projectId, String name, ProjectStatus status, double completionProgress, List<Task> tasks) {
        this.projectId = projectId;
        this.name = name;
        this.status = status;
        this.completionProgress = completionProgress;
        this.tasks = tasks != null ? tasks : new ArrayList<>();
    }

    public Project(ProjectId projectId, String name, ProjectStatus status, double completionProgress, LocalDate startDate, LocalDate endDate, List<Task> tasks) {
        this.projectId = projectId;
        this.name = name;
        this.status = status;
        this.completionProgress = completionProgress;
        this.startDate = startDate;
        this.endDate = endDate;
        this.tasks = tasks != null ? tasks : new ArrayList<>();
    }

    public ProjectId getProjectId() { return projectId; }
    public void setProjectId(ProjectId projectId) { this.projectId = projectId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public ProjectStatus getStatus() { return status; }
    public void setStatus(ProjectStatus status) { this.status = status; }

    public double getCompletionProgress() { return completionProgress; }
    public void setCompletionProgress(double completionProgress) { this.completionProgress = completionProgress; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public List<Task> getTasks() { return tasks; }
    public void setTasks(List<Task> tasks) { this.tasks = tasks; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return Objects.equals(projectId, project.projectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId);
    }
}
