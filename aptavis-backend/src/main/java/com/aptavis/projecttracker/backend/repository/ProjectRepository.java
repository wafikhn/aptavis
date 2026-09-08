package com.aptavis.projecttracker.backend.repository;

import com.aptavis.projecttracker.backend.constant.PersistenceConstants;
import com.aptavis.projecttracker.backend.domain.Project;
import com.aptavis.projecttracker.backend.domain.ProjectId;
import com.aptavis.projecttracker.backend.domain.ProjectStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class ProjectRepository {

    @PersistenceContext(unitName = PersistenceConstants.PERSISTENCE_UNIT)
    private EntityManager em;

    public List<Project> findAll() {
        return em.createNamedQuery(Project.QUERY_FIND_ALL_WITH_TASKS, Project.class)
                .getResultList();
    }

    public Optional<Project> findById(ProjectId id) {
        if (id == null || id.getProjectId() == null) return Optional.empty();
        List<Project> results = em.createNamedQuery(Project.QUERY_FIND_BY_ID, Project.class)
                .setParameter(PersistenceConstants.PARAM_PROJECT_ID, id)
                .getResultList();
        return results.stream().findFirst();
    }

    public Project save(Project project) {
        if (project.getProjectId() == null || project.getProjectId().getProjectId() == null) {
            Long nextId = ((Number) em.createNativeQuery(PersistenceConstants.QUERY_NEXTVAL_PROJECTS_SEQ).getSingleResult()).longValue();
            Project projectWithId = new Project(
                    ProjectId.of(nextId),
                    project.getName(),
                    project.getStatus() != null ? project.getStatus() : ProjectStatus.DRAFT,
                    project.getCompletionProgress(),
                    project.getTasks() != null ? project.getTasks() : new ArrayList<>()
            );
            em.persist(projectWithId);
            return projectWithId;
        } else {
            return em.merge(project);
        }
    }

    public void delete(ProjectId id) {
        if (id == null || id.getProjectId() == null) return;
        findById(id).ifPresent(em::remove);
    }
}
