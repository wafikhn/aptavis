-- SQL Schema Definition for Mini Aplikasi Project Tracker (PostgreSQL)

-- 1. Create projects table
CREATE TABLE IF NOT EXISTS projects (
    project_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    completion_progress DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    CONSTRAINT chk_project_status CHECK (status IN ('DRAFT', 'IN_PROGRESS', 'DONE'))
);

-- 2. Create tasks table
CREATE TABLE IF NOT EXISTS tasks (
    task_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    weight INTEGER NOT NULL DEFAULT 1,
    project_id BIGINT NOT NULL,
    CONSTRAINT fk_task_project FOREIGN KEY (project_id) REFERENCES projects(project_id) ON DELETE CASCADE,
    CONSTRAINT chk_task_status CHECK (status IN ('DRAFT', 'IN_PROGRESS', 'DONE')),
    CONSTRAINT chk_task_weight CHECK (weight >= 1)
);

-- Indexes for performance optimization
CREATE INDEX IF NOT EXISTS idx_tasks_project_id ON tasks(project_id);

-- Optional Sample Seed Data (Matches the Skill Test Diagram)
INSERT INTO projects (project_id, name, status, completion_progress) VALUES 
(1, 'Project 1', 'IN_PROGRESS', 66.67),
(2, 'Project 2', 'DRAFT', 0.0)
ON CONFLICT (project_id) DO NOTHING;

INSERT INTO tasks (task_id, name, status, weight, project_id) VALUES
(1, 'Task 1', 'DONE', 2, 1),
(2, 'Task 2', 'DRAFT', 1, 1),
(3, 'Task 1', 'DRAFT', 1, 2),
(4, 'Task 2', 'DRAFT', 1, 2)
ON CONFLICT (task_id) DO NOTHING;

-- Reset sequence IDs to prevent conflicts when inserting new rows from app
SELECT setval('projects_project_id_seq', (SELECT MAX(project_id) FROM projects));
SELECT setval('tasks_task_id_seq', (SELECT MAX(task_id) FROM tasks));
