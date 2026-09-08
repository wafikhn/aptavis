package com.aptavis.projecttracker.backend.constant;

public final class PersistenceConstants {

    private PersistenceConstants() {}

    public static final String PERSISTENCE_UNIT = "ProjectTrackerPU";
    public static final String PARAM_PROJECT_ID = "projectId";
    public static final String QUERY_NEXTVAL_PROJECTS_SEQ = "SELECT nextval('projects_project_id_seq')";
    public static final String QUERY_NEXTVAL_TASKS_SEQ = "SELECT nextval('tasks_task_id_seq')";
}
