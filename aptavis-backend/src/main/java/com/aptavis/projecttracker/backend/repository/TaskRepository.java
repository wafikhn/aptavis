package com.aptavis.projecttracker.backend.repository;

import com.aptavis.projecttracker.backend.constant.PersistenceConstants;
import com.aptavis.projecttracker.backend.constant.TaskConstants;
import com.aptavis.projecttracker.backend.domain.ProjectId;
import com.aptavis.projecttracker.backend.domain.Task;
import com.aptavis.projecttracker.backend.domain.TaskId;
import com.aptavis.projecttracker.backend.domain.TaskStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class TaskRepository {

    @PersistenceContext(unitName = PersistenceConstants.PERSISTENCE_UNIT)
    private EntityManager em;

    public List<Task> findAll() {
        return em.createNamedQuery(Task.QUERY_FIND_ALL, Task.class)
                .getResultList();
    }

    public Optional<Task> findById(TaskId id) {
        if (id == null || id.getTaskId() == null) return Optional.empty();
        return Optional.ofNullable(em.find(Task.class, id));
    }

    public List<Task> findByProjectId(ProjectId projectId) {
        if (projectId == null || projectId.getProjectId() == null) return List.of();
        return em.createNamedQuery(Task.QUERY_FIND_BY_PROJECT_ID, Task.class)
                .setParameter(PersistenceConstants.PARAM_PROJECT_ID, projectId)
                .getResultList();
    }

    public Task save(Task task) {
        if (task.getTaskId() == null || task.getTaskId().getTaskId() == null) {
            Long nextId = ((Number) em.createNativeQuery(PersistenceConstants.QUERY_NEXTVAL_TASKS_SEQ).getSingleResult()).longValue();
            Task taskWithId = new Task(
                    TaskId.of(nextId),
                    task.getName(),
                    task.getStatus() != null ? task.getStatus() : TaskStatus.DRAFT,
                    task.getWeight() != null ? task.getWeight() : TaskConstants.DEFAULT_TASK_WEIGHT,
                    task.getProject(),
                    task.getParentTask(),
                    task.getSubtasks()
            );
            em.persist(taskWithId);
            return taskWithId;
        } else {
            return em.merge(task);
        }
    }

    public void delete(TaskId id) {
        if (id == null || id.getTaskId() == null) return;
        findById(id).ifPresent(em::remove);
    }
}
