package com.aptavis.projecttracker.backend.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "tasks")
@NamedQueries({
    @NamedQuery(
        name = Task.QUERY_FIND_ALL,
        query = "SELECT t FROM Task t ORDER BY t.taskId.taskId DESC"
    ),
    @NamedQuery(
        name = Task.QUERY_FIND_BY_PROJECT_ID,
        query = "SELECT t FROM Task t WHERE t.project.projectId = :projectId"
    )
})
public class Task implements Serializable {

    public static final String QUERY_FIND_ALL = "Task.findAll";
    public static final String QUERY_FIND_BY_PROJECT_ID = "Task.findByProjectId";

    @EmbeddedId
    @AttributeOverride(name = "taskId", column = @Column(name = "task_id"))
    private TaskId taskId;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.DRAFT;

    @Column(nullable = false)
    private Integer weight = 1;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_task_id")
    private Task parentTask;

    @OneToMany(mappedBy = "parentTask", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Task> subtasks = new ArrayList<>();

    public Task() {}

    public Task(TaskId taskId, String name, TaskStatus status, Integer weight, Project project) {
        this.taskId = taskId;
        this.name = name;
        this.status = status;
        this.weight = weight;
        this.project = project;
    }

    public Task(TaskId taskId, String name, TaskStatus status, Integer weight, Project project, Task parentTask, List<Task> subtasks) {
        this.taskId = taskId;
        this.name = name;
        this.status = status;
        this.weight = weight;
        this.project = project;
        this.parentTask = parentTask;
        this.subtasks = subtasks != null ? subtasks : new ArrayList<>();
    }

    public TaskId getTaskId() { return taskId; }
    public void setTaskId(TaskId taskId) { this.taskId = taskId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }

    public Integer getWeight() { return weight; }
    public void setWeight(Integer weight) { this.weight = weight; }

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }

    public Task getParentTask() { return parentTask; }
    public void setParentTask(Task parentTask) { this.parentTask = parentTask; }

    public List<Task> getSubtasks() { return subtasks; }
    public void setSubtasks(List<Task> subtasks) { this.subtasks = subtasks; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(taskId, task.taskId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId);
    }
}
