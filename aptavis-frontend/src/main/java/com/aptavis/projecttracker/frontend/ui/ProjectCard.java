package com.aptavis.projecttracker.frontend.ui;

import com.aptavis.projecttracker.frontend.client.ProjectApiClient;
import com.aptavis.projecttracker.frontend.constant.NotificationConstants;
import com.aptavis.projecttracker.frontend.constant.UiTextConstants;
import com.aptavis.projecttracker.frontend.model.ProjectModel;
import com.aptavis.projecttracker.frontend.model.ProjectStatus;
import com.aptavis.projecttracker.frontend.model.TaskModel;
import com.aptavis.projecttracker.frontend.model.TaskStatus;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.function.Consumer;

public class ProjectCard extends VerticalLayout {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final ProjectModel project;
    private final Consumer<ProjectModel> onEditProject;
    private final Consumer<ProjectModel> onAddTaskToProject;
    private final Consumer<TaskModel> onEditTask;
    private final Runnable refreshCallback;
    private final ProjectApiClient apiClient;

    public ProjectCard(ProjectModel project,
                       ProjectApiClient apiClient,
                       Consumer<ProjectModel> onEditProject,
                       Consumer<ProjectModel> onAddTaskToProject,
                       Consumer<TaskModel> onEditTask,
                       Runnable refreshCallback) {
        this.project = project;
        this.apiClient = apiClient;
        this.onEditProject = onEditProject;
        this.onAddTaskToProject = onAddTaskToProject;
        this.onEditTask = onEditTask;
        this.refreshCallback = refreshCallback;

        initCardStyle();
        buildCardContent();
    }

    private void initCardStyle() {
        setWidthFull();
        setPadding(true);
        setSpacing(true);
        addClassName("project-card");
    }

    private void buildCardContent() {
        removeAll();

        H4 title = new H4(project.name());
        title.addClassName("card-title");

        String scheduleText = formatSchedule(project);
        Span dateSpan = new Span("🗓️ " + scheduleText);
        dateSpan.addClassName("schedule-span");

        VerticalLayout titleLayout = new VerticalLayout(title, dateSpan);
        titleLayout.setPadding(false);
        titleLayout.setSpacing(false);

        Span statusBadge = createProjectStatusBadge(project.status());

        Button addTaskBtn = new Button(UiTextConstants.BTN_TASK, VaadinIcon.PLUS.create());
        addTaskBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
        addTaskBtn.setTooltipText(UiTextConstants.TOOLTIP_ADD_TASK);
        addTaskBtn.addClickListener(e -> onAddTaskToProject.accept(project));

        Button editProjectBtn = new Button(VaadinIcon.EDIT.create());
        editProjectBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
        editProjectBtn.setTooltipText(UiTextConstants.TOOLTIP_EDIT_PROJECT);
        editProjectBtn.addClickListener(e -> onEditProject.accept(project));

        Button deleteProjectBtn = new Button(VaadinIcon.TRASH.create());
        deleteProjectBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
        deleteProjectBtn.setTooltipText(UiTextConstants.TOOLTIP_DELETE_PROJECT);
        deleteProjectBtn.addClickListener(e -> {
            apiClient.deleteProject(project.projectId());
            Notification n = Notification.show(NotificationConstants.NOTIF_PROJECT_DELETED_SHORT, 3000, Notification.Position.BOTTOM_END);
            n.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
            refreshCallback.run();
        });

        HorizontalLayout headerRight = new HorizontalLayout(statusBadge, addTaskBtn, editProjectBtn, deleteProjectBtn);
        headerRight.setAlignItems(FlexComponent.Alignment.CENTER);

        HorizontalLayout header = new HorizontalLayout(titleLayout, headerRight);
        header.setWidthFull();
        header.setJustifyContentMode(HorizontalLayout.JustifyContentMode.BETWEEN);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        add(header);

        double progressPct = project.completionProgress();
        ProgressBar progressBar = new ProgressBar();
        progressBar.setValue(Math.min(1.0, Math.max(0.0, progressPct / 100.0)));
        progressBar.setWidthFull();

        String formattedPct = String.format(Locale.US, "%.1f%%", progressPct);
        Span progressText = new Span("Progress: " + formattedPct);
        progressText.addClassName("progress-text");

        HorizontalLayout progressHeader = new HorizontalLayout(progressText);
        progressHeader.setWidthFull();
        progressHeader.setJustifyContentMode(HorizontalLayout.JustifyContentMode.END);

        VerticalLayout progressLayout = new VerticalLayout(progressHeader, progressBar);
        progressLayout.setPadding(false);
        progressLayout.setSpacing(false);
        progressLayout.addClassName("progress-layout");
        add(progressLayout);

        VerticalLayout taskListLayout = new VerticalLayout();
        taskListLayout.setPadding(false);
        taskListLayout.setSpacing(true);
        taskListLayout.setWidthFull();

        if (project.tasks() == null || project.tasks().isEmpty()) {
            Span emptySpan = new Span(UiTextConstants.MSG_NO_TASKS_IN_PROJECT);
            emptySpan.addClassName("empty-task-span");
            taskListLayout.add(emptySpan);
        } else {
            for (TaskModel task : project.tasks()) {
                renderTaskItem(task, taskListLayout, 0);
            }
        }

        add(taskListLayout);
    }

    private void renderTaskItem(TaskModel task, VerticalLayout container, int depth) {
        HorizontalLayout taskRow = createTaskRow(task, depth);
        container.add(taskRow);

        if (task.subtasks() != null && !task.subtasks().isEmpty()) {
            for (TaskModel subtask : task.subtasks()) {
                renderTaskItem(subtask, container, depth + 1);
            }
        }
    }

    private HorizontalLayout createTaskRow(TaskModel task, int depth) {
        Span iconSpan = new Span(depth > 0 ? "↳ " : "+ ");
        iconSpan.addClassName(depth > 0 ? "subtask-icon" : "task-plus-icon");

        Span taskName = new Span(task.name());
        taskName.addClassName("task-name");

        HorizontalLayout left = new HorizontalLayout(iconSpan, taskName);
        left.setAlignItems(FlexComponent.Alignment.CENTER);

        Span statusBadge = createTaskStatusBadge(task.status());

        Span weightBadge = new Span("Bobot: " + task.weight());
        weightBadge.getElement().getThemeList().add("badge contrast Small");

        Button editTaskBtn = new Button(VaadinIcon.EDIT.create());
        editTaskBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
        editTaskBtn.setTooltipText(UiTextConstants.TOOLTIP_EDIT_TASK);
        editTaskBtn.addClickListener(e -> onEditTask.accept(task));

        Button deleteTaskBtn = new Button(VaadinIcon.TRASH.create());
        deleteTaskBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
        deleteTaskBtn.setTooltipText(UiTextConstants.TOOLTIP_DELETE_TASK);
        deleteTaskBtn.addClickListener(e -> {
            apiClient.deleteTask(task.taskId());
            Notification n = Notification.show(NotificationConstants.NOTIF_TASK_DELETED_SHORT, 3000, Notification.Position.BOTTOM_END);
            n.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
            refreshCallback.run();
        });

        HorizontalLayout right = new HorizontalLayout(weightBadge, statusBadge, editTaskBtn, deleteTaskBtn);
        right.setAlignItems(FlexComponent.Alignment.CENTER);

        HorizontalLayout row = new HorizontalLayout(left, right);
        row.setWidthFull();
        row.setJustifyContentMode(HorizontalLayout.JustifyContentMode.BETWEEN);
        row.setAlignItems(FlexComponent.Alignment.CENTER);
        row.addClassName("task-row");

        if (depth > 0) {
            row.getStyle().set("margin-left", (depth * 24) + "px");
            row.getStyle().set("opacity", "0.95");
        }

        return row;
    }

    private String formatSchedule(ProjectModel project) {
        if (project.startDate() != null && project.endDate() != null) {
            return project.startDate().format(DATE_FORMATTER) + " s/d " + project.endDate().format(DATE_FORMATTER);
        } else if (project.startDate() != null) {
            return "Mulai: " + project.startDate().format(DATE_FORMATTER);
        } else if (project.endDate() != null) {
            return "Selesai: " + project.endDate().format(DATE_FORMATTER);
        }
        return "Jadwal belum diatur";
    }

    private Span createProjectStatusBadge(ProjectStatus status) {
        Span badge = new Span(status != null ? status.getDisplayName() : "Draft");
        badge.getElement().getThemeList().add("badge");
        if (status == ProjectStatus.DONE) {
            badge.getElement().getThemeList().add("success");
        } else if (status == ProjectStatus.IN_PROGRESS) {
            badge.getElement().getThemeList().add("primary");
        } else {
            badge.getElement().getThemeList().add("contrast");
        }
        return badge;
    }

    private Span createTaskStatusBadge(TaskStatus status) {
        Span badge = new Span(status != null ? status.getDisplayName() : "Draft");
        badge.getElement().getThemeList().add("badge small");
        if (status == TaskStatus.DONE) {
            badge.getElement().getThemeList().add("success");
        } else if (status == TaskStatus.IN_PROGRESS) {
            badge.getElement().getThemeList().add("primary");
        } else {
            badge.getElement().getThemeList().add("contrast");
        }
        return badge;
    }
}
