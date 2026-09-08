package com.aptavis.projecttracker.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ProjectId implements Serializable {

    @Column(name = "project_id")
    private Long projectId;

    public static ProjectId of(Long projectId) {
        return projectId != null ? new ProjectId(projectId) : null;
    }
}
