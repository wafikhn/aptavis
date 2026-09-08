package com.aptavis.projecttracker.backend;

import com.aptavis.projecttracker.backend.domain.Project;
import com.aptavis.projecttracker.backend.domain.ProjectStatus;
import com.aptavis.projecttracker.backend.domain.Task;
import com.aptavis.projecttracker.backend.domain.TaskStatus;
import com.aptavis.projecttracker.backend.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ProjectTrackerTest {

    private ProjectService projectService;

    @BeforeEach
    void setUp() {
        projectService = new ProjectService();
    }

    @Test
    @DisplayName("Empty project should have DRAFT status and 0% progress")
    void testEmptyProject() {
        Project project = new Project();
        project.setName("Test Project");
        project.setTasks(new ArrayList<>());
        projectService.recalculateProjectMetrics(project);

        assertEquals(ProjectStatus.DRAFT, project.getStatus());
        assertEquals(0.0, project.getCompletionProgress(), 0.001);
    }

    @Test
    @DisplayName("Project progress should be 66.67% when 1 of 2 weighted tasks is Done (from image example)")
    void testProjectProgressFromImageExample() {
        Project project = new Project();
        project.setName("Image Test Project");
        project.setTasks(new ArrayList<>());

        Task task1 = new Task(null, "Task 1", TaskStatus.DONE, 2, project);
        Task task2 = new Task(null, "Task 2", TaskStatus.DRAFT, 1, project);

        project.getTasks().add(task1);
        project.getTasks().add(task2);

        projectService.recalculateProjectMetrics(project);

        assertEquals(66.666, project.getCompletionProgress(), 0.01);
        assertEquals(ProjectStatus.IN_PROGRESS, project.getStatus());
    }

    @Test
    @DisplayName("Project status should be DONE when all tasks are DONE")
    void testAllTasksDone() {
        Project project = new Project();
        project.setName("All Done Project");
        project.setTasks(new ArrayList<>());

        Task task1 = new Task(null, "Task 1", TaskStatus.DONE, 2, project);
        Task task2 = new Task(null, "Task 2", TaskStatus.DONE, 1, project);

        project.getTasks().add(task1);
        project.getTasks().add(task2);

        projectService.recalculateProjectMetrics(project);

        assertEquals(100.0, project.getCompletionProgress(), 0.001);
        assertEquals(ProjectStatus.DONE, project.getStatus());
    }
}
