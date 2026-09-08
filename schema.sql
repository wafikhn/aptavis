-- SQL Schema Definition for Mini Aplikasi Project Tracker (PostgreSQL)

-- 1. Create projects table
CREATE TABLE IF NOT EXISTS projects (
    project_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    completion_progress DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    start_date DATE,
    end_date DATE,
    CONSTRAINT chk_project_status CHECK (status IN ('DRAFT', 'IN_PROGRESS', 'DONE'))
);

-- Ensure columns exist if table was already created
ALTER TABLE projects ADD COLUMN IF NOT EXISTS start_date DATE;
ALTER TABLE projects ADD COLUMN IF NOT EXISTS end_date DATE;

-- 2. Create tasks table
CREATE TABLE IF NOT EXISTS tasks (
    task_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    weight INTEGER NOT NULL DEFAULT 1,
    project_id BIGINT NOT NULL,
    parent_task_id BIGINT,
    CONSTRAINT fk_task_project FOREIGN KEY (project_id) REFERENCES projects(project_id) ON DELETE CASCADE,
    CONSTRAINT fk_task_parent FOREIGN KEY (parent_task_id) REFERENCES tasks(task_id) ON DELETE CASCADE,
    CONSTRAINT chk_task_status CHECK (status IN ('DRAFT', 'IN_PROGRESS', 'DONE')),
    CONSTRAINT chk_task_weight CHECK (weight >= 1)
);

-- Ensure columns & constraints exist if table was already created
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS parent_task_id BIGINT;
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_task_parent') THEN
        ALTER TABLE tasks ADD CONSTRAINT fk_task_parent FOREIGN KEY (parent_task_id) REFERENCES tasks(task_id) ON DELETE CASCADE;
    END IF;
END $$;

-- Indexes for performance optimization
CREATE INDEX IF NOT EXISTS idx_tasks_project_id ON tasks(project_id);
CREATE INDEX IF NOT EXISTS idx_tasks_parent_task_id ON tasks(parent_task_id);

-- Optional Sample Seed Data (Matches the Skill Test Diagram)
INSERT INTO projects (project_id, name, status, completion_progress, start_date, end_date) VALUES 
(1, 'Project 1', 'IN_PROGRESS', 66.67, '2026-09-01', '2026-09-15'),
(2, 'Project 2', 'DRAFT', 0.0, '2026-09-16', '2026-09-30')
ON CONFLICT (project_id) DO UPDATE SET 
    start_date = EXCLUDED.start_date,
    end_date = EXCLUDED.end_date;

INSERT INTO tasks (task_id, name, status, weight, project_id, parent_task_id) VALUES
(1, 'Task 1', 'DONE', 2, 1, NULL),
(2, 'Task 2', 'DRAFT', 1, 1, NULL),
(3, 'Task 1', 'DRAFT', 1, 2, NULL),
(4, 'Task 2', 'DRAFT', 1, 2, NULL)
ON CONFLICT (task_id) DO NOTHING;

-- Reset sequence IDs to prevent conflicts when inserting new rows from app
SELECT setval('projects_project_id_seq', (SELECT GREATEST(MAX(project_id), 1) FROM projects));
SELECT setval('tasks_task_id_seq', (SELECT GREATEST(MAX(task_id), 1) FROM tasks));
