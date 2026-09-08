package com.aptavis.projecttracker.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ProjectId implements Serializable {

    @Column(name = "project_id")
    private Long projectId;

    public ProjectId() {}

    public ProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public static ProjectId of(Long projectId) {
        return projectId != null ? new ProjectId(projectId) : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectId projectId1 = (ProjectId) o;
        return Objects.equals(projectId, projectId1.projectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId);
    }
}
