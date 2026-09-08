package com.aptavis.projecttracker.frontend.constant;

public final class UiTextConstants {

    private UiTextConstants() {}

    // UI Labels & Subtitles
    public static final String APP_TITLE = "Mini Aplikasi Project Tracker";
    public static final String APP_SUBTITLE = "Aptavis Frontend (Vaadin 24) • Aptavis Backend (Jakarta EE REST API)";
    
    // Buttons
    public static final String BTN_ADD_PROJECT = "Add Project";
    public static final String BTN_ADD_TASK = "Add Task";
    public static final String BTN_TASK = "Task";
    public static final String BTN_SIMPAN = "Simpan";
    public static final String BTN_HAPUS = "Hapus";
    public static final String BTN_BATAL = "Batal";
    
    // Tooltips
    public static final String TOOLTIP_ADD_TASK = "Tambah Task ke Project ini";
    public static final String TOOLTIP_EDIT_PROJECT = "Edit Project";
    public static final String TOOLTIP_DELETE_PROJECT = "Hapus Project";
    public static final String TOOLTIP_EDIT_TASK = "Edit Task";
    public static final String TOOLTIP_DELETE_TASK = "Hapus Task";

    // Placeholders
    public static final String PLACEHOLDER_SEARCH = "Cari project atau task...";
    public static final String PLACEHOLDER_PROJECT_NAME = "Masukkan nama project...";
    public static final String PLACEHOLDER_TASK_NAME = "Masukkan nama task...";
    public static final String PLACEHOLDER_PROJECT_SELECT = "Pilih project...";

    // Empty State Messages
    public static final String MSG_EMPTY_PROJECTS = "Belum ada project. Klik 'Add Project' untuk memulai!";
    public static final String MSG_NO_SEARCH_RESULTS = "Tidak ada project atau task yang cocok dengan pencarian.";
    public static final String MSG_NO_TASKS_IN_PROJECT = "Belum ada task. Klik '+ Task' untuk menambah.";
}
