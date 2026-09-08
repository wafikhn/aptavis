package com.aptavis.projecttracker.frontend.ui;

import com.aptavis.projecttracker.frontend.client.ProjectApiClient;
import com.aptavis.projecttracker.frontend.constant.NotificationConstants;
import com.aptavis.projecttracker.frontend.constant.UiTextConstants;
import com.aptavis.projecttracker.frontend.model.ProjectModel;
import com.aptavis.projecttracker.frontend.model.TaskModel;
import com.aptavis.projecttracker.frontend.model.TaskStatus;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;

import java.util.List;

public class ProjectTaskDialog extends Dialog {

    private final ProjectApiClient apiClient;
    private final Runnable onSaveCallback;

    public enum Mode {
        PROJECT, TASK
    }

    private Mode mode = Mode.PROJECT;
    private ProjectModel currentProject;
    private TaskModel currentTask;

    private final H3 title = new H3();

    private final TextField projectNameField = new TextField("Nama Project");

    private final ComboBox<ProjectModel> projectComboBox = new ComboBox<>("Project");
    private final TextField taskNameField = new TextField("Nama Task");
    private final Select<TaskStatus> taskStatusSelect = new Select<>();
    private final IntegerField taskWeightField = new IntegerField("Bobot (Weight)");

    private final Button saveButton = new Button(UiTextConstants.BTN_SIMPAN);
    private final Button deleteButton = new Button(UiTextConstants.BTN_HAPUS);
    private final Button cancelButton = new Button(UiTextConstants.BTN_BATAL);

    public ProjectTaskDialog(ProjectApiClient apiClient, Runnable onSaveCallback) {
        this.apiClient = apiClient;
        this.onSaveCallback = onSaveCallback;

        setWidth("450px");
        setCloseOnOutsideClick(true);

        initUI();
    }

    private void initUI() {
        title.addClassName("dialog-title");

        projectNameField.setWidthFull();
        projectNameField.setPlaceholder(UiTextConstants.PLACEHOLDER_PROJECT_NAME);

        projectComboBox.setWidthFull();
        projectComboBox.setItemLabelGenerator(ProjectModel::name);
        projectComboBox.setPlaceholder(UiTextConstants.PLACEHOLDER_PROJECT_SELECT);

        taskNameField.setWidthFull();
        taskNameField.setPlaceholder(UiTextConstants.PLACEHOLDER_TASK_NAME);

        taskStatusSelect.setLabel("Status");
        taskStatusSelect.setItems(TaskStatus.values());
        taskStatusSelect.setValue(TaskStatus.DRAFT);
        taskStatusSelect.setWidthFull();

        taskWeightField.setValue(1);
        taskWeightField.setMin(1);
        taskWeightField.setStepButtonsVisible(true);
        taskWeightField.setWidthFull();

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        saveButton.addClickListener(e -> save());
        deleteButton.addClickListener(e -> delete());
        cancelButton.addClickListener(e -> close());

        HorizontalLayout actions = new HorizontalLayout(deleteButton, new Span(), cancelButton, saveButton);
        actions.setWidthFull();
        actions.setJustifyContentMode(HorizontalLayout.JustifyContentMode.BETWEEN);

        VerticalLayout layout = new VerticalLayout(title, projectNameField, projectComboBox, taskNameField, taskStatusSelect, taskWeightField, actions);
        layout.setPadding(false);
        layout.setSpacing(true);

        add(layout);
    }

    public void openForNewProject() {
        this.mode = Mode.PROJECT;
        this.currentProject = new ProjectModel("");
        this.currentTask = null;

        title.setText("Tambah Project Baru");
        projectNameField.clear();
        projectNameField.setVisible(true);

        projectComboBox.setVisible(false);
        taskNameField.setVisible(false);
        taskStatusSelect.setVisible(false);
        taskWeightField.setVisible(false);

        deleteButton.setVisible(false);
        open();
    }

    public void openForEditProject(ProjectModel project) {
        this.mode = Mode.PROJECT;
        this.currentProject = project;
        this.currentTask = null;

        title.setText("Edit Project: " + project.name());
        projectNameField.setValue(project.name() != null ? project.name() : "");
        projectNameField.setVisible(true);

        projectComboBox.setVisible(false);
        taskNameField.setVisible(false);
        taskStatusSelect.setVisible(false);
        taskWeightField.setVisible(false);

        deleteButton.setVisible(true);
        open();
    }

    public void openForNewTask(ProjectModel defaultProject) {
        this.mode = Mode.TASK;
        this.currentProject = null;
        this.currentTask = new TaskModel("", TaskStatus.DRAFT, 1, null);

        List<ProjectModel> allProjects = apiClient.findAllProjects();
        projectComboBox.setItems(allProjects);

        title.setText("Tambah Task Baru");
        projectNameField.setVisible(false);

        projectComboBox.setVisible(true);
        if (defaultProject != null) {
            projectComboBox.setValue(defaultProject);
        } else if (!allProjects.isEmpty()) {
            projectComboBox.setValue(allProjects.get(0));
        }

        taskNameField.clear();
        taskNameField.setVisible(true);

        taskStatusSelect.setValue(TaskStatus.DRAFT);
        taskStatusSelect.setVisible(true);

        taskWeightField.setValue(1);
        taskWeightField.setVisible(true);

        deleteButton.setVisible(false);
        open();
    }

    public void openForEditTask(TaskModel task) {
        this.mode = Mode.TASK;
        this.currentProject = null;
        this.currentTask = task;

        List<ProjectModel> allProjects = apiClient.findAllProjects();
        projectComboBox.setItems(allProjects);

        title.setText("Edit Task: " + task.name());
        projectNameField.setVisible(false);

        projectComboBox.setVisible(true);
        if (task.projectId() != null) {
            allProjects.stream()
                    .filter(p -> p.projectId() != null && p.projectId().equals(task.projectId()))
                    .findFirst()
                    .ifPresent(projectComboBox::setValue);
        }

        taskNameField.setValue(task.name() != null ? task.name() : "");
        taskNameField.setVisible(true);

        taskStatusSelect.setValue(task.status() != null ? task.status() : TaskStatus.DRAFT);
        taskStatusSelect.setVisible(true);

        taskWeightField.setValue(task.weight() != null ? task.weight() : 1);
        taskWeightField.setVisible(true);

        deleteButton.setVisible(true);
        open();
    }

    private void save() {
        if (mode == Mode.PROJECT) {
            String name = projectNameField.getValue();
            if (name == null || name.trim().isEmpty()) {
                Notification n = Notification.show(NotificationConstants.NOTIF_PROJECT_EMPTY_NAME, 3000, Notification.Position.BOTTOM_END);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
            ProjectModel projectToSave = new ProjectModel(
                    currentProject.projectId(),
                    name.trim(),
                    currentProject.status(),
                    currentProject.completionProgress(),
                    currentProject.tasks()
            );
            ProjectModel saved = apiClient.saveProject(projectToSave);
            if (saved != null) {
                Notification n = Notification.show(NotificationConstants.NOTIF_PROJECT_SAVED, 3000, Notification.Position.BOTTOM_END);
                n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } else {
                Notification n = Notification.show(NotificationConstants.NOTIF_SAVE_FAILED, 5000, Notification.Position.BOTTOM_END);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        } else {
            ProjectModel selectedProject = projectComboBox.getValue();
            String name = taskNameField.getValue();
            Integer weight = taskWeightField.getValue();
            TaskStatus status = taskStatusSelect.getValue();

            if (selectedProject == null) {
                Notification n = Notification.show(NotificationConstants.NOTIF_SELECT_PROJECT_FIRST, 3000, Notification.Position.BOTTOM_END);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
            if (name == null || name.trim().isEmpty()) {
                Notification n = Notification.show(NotificationConstants.NOTIF_TASK_EMPTY_NAME, 3000, Notification.Position.BOTTOM_END);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            TaskModel taskToSave = new TaskModel(
                    currentTask.taskId(),
                    name.trim(),
                    status,
                    weight,
                    selectedProject.projectId()
            );

            TaskModel saved = apiClient.saveTask(taskToSave);
            if (saved != null) {
                Notification n = Notification.show(NotificationConstants.NOTIF_TASK_SAVED, 3000, Notification.Position.BOTTOM_END);
                n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } else {
                Notification n = Notification.show(NotificationConstants.NOTIF_SAVE_FAILED, 5000, Notification.Position.BOTTOM_END);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        }

        close();
        if (onSaveCallback != null) {
            onSaveCallback.run();
        }
    }

    private void delete() {
        if (mode == Mode.PROJECT && currentProject != null && currentProject.projectId() != null) {
            boolean deleted = apiClient.deleteProject(currentProject.projectId());
            if (deleted) {
                Notification n = Notification.show(NotificationConstants.NOTIF_PROJECT_DELETED, 3000, Notification.Position.BOTTOM_END);
                n.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
            } else {
                Notification n = Notification.show(NotificationConstants.NOTIF_DELETE_FAILED, 5000, Notification.Position.BOTTOM_END);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        } else if (mode == Mode.TASK && currentTask != null && currentTask.taskId() != null) {
            boolean deleted = apiClient.deleteTask(currentTask.taskId());
            if (deleted) {
                Notification n = Notification.show(NotificationConstants.NOTIF_TASK_DELETED, 3000, Notification.Position.BOTTOM_END);
                n.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
            } else {
                Notification n = Notification.show(NotificationConstants.NOTIF_DELETE_FAILED, 5000, Notification.Position.BOTTOM_END);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        }

        close();
        if (onSaveCallback != null) {
            onSaveCallback.run();
        }
    }
}
