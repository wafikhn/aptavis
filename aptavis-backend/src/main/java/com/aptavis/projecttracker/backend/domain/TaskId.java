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
public class TaskId implements Serializable {

    @Column(name = "task_id")
    private Long taskId;

    public static TaskId of(Long taskId) {
        return taskId != null ? new TaskId(taskId) : null;
    }
}
