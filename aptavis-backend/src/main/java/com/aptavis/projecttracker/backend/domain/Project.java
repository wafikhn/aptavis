package com.aptavis.projecttracker.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Project implements Serializable {

    public static final String QUERY_FIND_ALL_WITH_TASKS = "Project.findAllWithTasks";
    public static final String QUERY_FIND_BY_ID = "Project.findById";

    @EmbeddedId
    @AttributeOverride(name = "projectId", column = @Column(name = "project_id"))
    @EqualsAndHashCode.Include
    @Setter(AccessLevel.NONE)
    private ProjectId projectId;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectStatus status = ProjectStatus.DRAFT;

    @Column(name = "completion_progress", nullable = false)
    private double completionProgress = 0.0;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Task> tasks = new ArrayList<>();
}
