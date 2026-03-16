CREATE SCHEMA IF NOT EXISTS task_schema;
SET
search_path TO task_schema;

CREATE TABLE workspaces
(
    id          VARCHAR(255) PRIMARY KEY,
    user_id     VARCHAR(255) NOT NULL UNIQUE,
    name        VARCHAR(255),
    description TEXT,
    created_at  TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE projects
(
    id          VARCHAR(255) PRIMARY KEY,
    name        VARCHAR(255),
    description TEXT,
    created_by  VARCHAR(255),
    start_at    TIMESTAMPTZ,
    end_at      TIMESTAMPTZ,
    created_at  TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE workspace_project
(
    project_id   VARCHAR(255) NOT NULL,
    workspace_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (project_id, workspace_id),
    CONSTRAINT fk_wp_project FOREIGN KEY (project_id) REFERENCES projects (id) ON DELETE CASCADE,
    CONSTRAINT fk_wp_workspace FOREIGN KEY (workspace_id) REFERENCES workspaces (id) ON DELETE CASCADE
);

CREATE TABLE columns
(
    id         VARCHAR(255) PRIMARY KEY,
    project_id VARCHAR(255) NOT NULL,
    name       VARCHAR(255),
    position   FLOAT8       NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ           DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ           DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_col_project FOREIGN KEY (project_id)
        REFERENCES projects (id) ON DELETE CASCADE
);

CREATE TABLE tasks
(
    id           VARCHAR(255) PRIMARY KEY,
    column_id    VARCHAR(255) NOT NULL,
    title        VARCHAR(255),
    description  TEXT,
    position     FLOAT8       NOT NULL DEFAULT 0,
    start_at     TIMESTAMPTZ,
    due_at       TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,
    created_by   VARCHAR(255),
    assignee_id  VARCHAR(255),
    label        VARCHAR(255),
    created_at   TIMESTAMPTZ           DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMPTZ           DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_task_column FOREIGN KEY (column_id)
        REFERENCES columns (id) ON DELETE CASCADE
);

CREATE TABLE project_member
(
    id         VARCHAR(255) PRIMARY KEY,
    project_id VARCHAR(255) NOT NULL,
    user_id    VARCHAR(255) NOT NULL,
    role       VARCHAR(50)  NOT NULL,
    CONSTRAINT fk_pm_project FOREIGN KEY (project_id)
        REFERENCES projects (id) ON DELETE CASCADE,
    CONSTRAINT uq_pm_project_user UNIQUE (project_id, user_id)
);

CREATE INDEX idx_workspace_user_id ON workspaces (user_id);
CREATE INDEX idx_wp_project_id ON workspace_project (project_id);
CREATE INDEX idx_wp_workspace_id ON workspace_project (workspace_id);
CREATE INDEX idx_col_project_id ON columns (project_id);
CREATE INDEX idx_col_position ON columns (project_id, position);
CREATE INDEX idx_task_column_id ON tasks (column_id);
CREATE INDEX idx_task_position ON tasks (column_id, position);
CREATE INDEX idx_task_assignee ON tasks (assignee_id);
CREATE INDEX idx_pm_project_user ON project_member (project_id, user_id);