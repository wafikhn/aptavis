package com.aptavis.projecttracker.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

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
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Task implements Serializable {

    public static final String QUERY_FIND_ALL = "Task.findAll";
    public static final String QUERY_FIND_BY_PROJECT_ID = "Task.findByProjectId";

    @EmbeddedId
    @AttributeOverride(name = "taskId", column = @Column(name = "task_id"))
    @EqualsAndHashCode.Include
    @Setter(AccessLevel.NONE)
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
}
