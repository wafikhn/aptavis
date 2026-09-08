package com.aptavis.projecttracker.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TaskId implements Serializable {

    @Column(name = "task_id")
    private Long taskId;

    public TaskId() {}

    public TaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public static TaskId of(Long taskId) {
        return taskId != null ? new TaskId(taskId) : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskId taskId1 = (TaskId) o;
        return Objects.equals(taskId, taskId1.taskId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId);
    }
}
