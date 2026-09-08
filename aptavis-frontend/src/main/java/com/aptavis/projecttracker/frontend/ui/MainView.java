package com.aptavis.projecttracker.frontend.ui;

import com.aptavis.projecttracker.frontend.client.ProjectApiClient;
import com.aptavis.projecttracker.frontend.constant.UiTextConstants;
import com.aptavis.projecttracker.frontend.model.ProjectModel;
import com.vaadin.cdi.annotation.CdiComponent;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

@Route("")
@PageTitle("Project Tracker - Aptavis Frontend")
@StyleSheet("./styles/styles.css")
@CdiComponent
public class MainView extends VerticalLayout {

    private final ProjectApiClient apiClient;
    private ProjectTaskDialog dialog;

    private final VerticalLayout projectsContainer = new VerticalLayout();
    private final TextField searchField = new TextField();

    @Inject
    public MainView(ProjectApiClient apiClient) {
        this.apiClient = apiClient;

        setSizeFull();
        setPadding(true);
        setSpacing(true);
        addClassName("main-container");

        initUI();
        refreshProjects();
    }

    private void initUI() {
        dialog = new ProjectTaskDialog(apiClient, this::refreshProjects);

        H2 title = new H2(UiTextConstants.APP_TITLE);
        title.addClassName("card-title");

        Paragraph subtitle = new Paragraph(UiTextConstants.APP_SUBTITLE);
        subtitle.addClassName("app-subtitle");

        VerticalLayout headerText = new VerticalLayout(title, subtitle);
        headerText.setPadding(false);
        headerText.setSpacing(false);

        Button addProjectBtn = new Button(UiTextConstants.BTN_ADD_PROJECT, VaadinIcon.PLUS.create());
        addProjectBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addProjectBtn.addClickListener(e -> dialog.openForNewProject());

        Button addTaskBtn = new Button(UiTextConstants.BTN_ADD_TASK, VaadinIcon.PLUS_CIRCLE.create());
        addTaskBtn.addClickListener(e -> dialog.openForNewTask(null));

        searchField.setPlaceholder(UiTextConstants.PLACEHOLDER_SEARCH);
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setClearButtonVisible(true);
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> filterProjects(e.getValue()));
        searchField.addClassName("search-field");

        HorizontalLayout toolbar = new HorizontalLayout(addProjectBtn, addTaskBtn, searchField);
        toolbar.setWidthFull();
        toolbar.setAlignItems(FlexComponent.Alignment.CENTER);

        HorizontalLayout topBar = new HorizontalLayout(headerText, toolbar);
        topBar.setWidthFull();
        topBar.setJustifyContentMode(HorizontalLayout.JustifyContentMode.BETWEEN);
        topBar.setAlignItems(FlexComponent.Alignment.END);

        add(topBar);

        projectsContainer.setPadding(false);
        projectsContainer.setSpacing(true);
        projectsContainer.setWidthFull();
        add(projectsContainer);
    }

    public void refreshProjects() {
        filterProjects(searchField.getValue());
    }

    private void filterProjects(String query) {
        projectsContainer.removeAll();
        List<ProjectModel> projects = apiClient.findAllProjects();

        if (query != null && !query.trim().isEmpty()) {
            String q = query.trim().toLowerCase();
            projects = projects.stream()
                    .filter(p -> (p.name() != null && p.name().toLowerCase().contains(q)) ||
                            (p.tasks() != null && p.tasks().stream().anyMatch(t -> t.name() != null && t.name().toLowerCase().contains(q))))
                    .collect(Collectors.toList());
        }

        if (projects.isEmpty()) {
            VerticalLayout emptyState = new VerticalLayout();
            emptyState.setAlignItems(FlexComponent.Alignment.CENTER);
            emptyState.setPadding(true);
            emptyState.addClassName("empty-state");

            Span icon = new Span(VaadinIcon.FOLDER_OPEN_O.create());
            icon.addClassName("empty-state-icon");

            Paragraph msg = new Paragraph(query == null || query.isEmpty() ?
                    UiTextConstants.MSG_EMPTY_PROJECTS :
                    UiTextConstants.MSG_NO_SEARCH_RESULTS);
            msg.addClassName("empty-state-msg");

            emptyState.add(icon, msg);
            projectsContainer.add(emptyState);
            return;
        }

        for (ProjectModel project : projects) {
            ProjectCard card = new ProjectCard(
                    project,
                    apiClient,
                    p -> dialog.openForEditProject(p),
                    p -> dialog.openForNewTask(p),
                    t -> dialog.openForEditTask(t),
                    this::refreshProjects
            );
            projectsContainer.add(card);
        }
    }
}
