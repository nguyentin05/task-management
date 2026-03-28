CREATE SCHEMA IF NOT EXISTS task_schema;
SET search_path TO task_schema;

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
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    created_by  VARCHAR(255),
    start_at    TIMESTAMPTZ,
    end_at      TIMESTAMPTZ,
    created_at  TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE outbox_events (
                               id VARCHAR(255) PRIMARY KEY,
                               routing_key VARCHAR(255) NOT NULL,
                               payload TEXT NOT NULL,
                               status VARCHAR(50) NOT NULL,
                               created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                               retry_count INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE columns
(
    id         VARCHAR(255) PRIMARY KEY,
    project_id VARCHAR(255) NOT NULL,
    name       VARCHAR(255) NOT NULL,
    position   FLOAT8       NOT NULL,
    created_at TIMESTAMPTZ           DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ           DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT foreign_key_projects FOREIGN KEY (project_id) REFERENCES projects (id) ON DELETE CASCADE
);

CREATE TABLE tasks
(
    id           VARCHAR(255) PRIMARY KEY,
    column_id    VARCHAR(255)  NOT NULL,
    title        VARCHAR(255) NOT NULL,
    description  TEXT,
    position     FLOAT8       NOT NULL,
    start_at     TIMESTAMPTZ,
    due_at       TIMESTAMPTZ,
    created_by   VARCHAR(255),
    created_at   TIMESTAMPTZ           DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMPTZ           DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMPTZ,
    assignee_id  VARCHAR(255),
    label        VARCHAR(255),
    CONSTRAINT foreign_key_columns FOREIGN KEY (column_id) REFERENCES columns (id) ON DELETE CASCADE
);

CREATE TABLE workspace_project
(
    project_id   VARCHAR(255) NOT NULL,
    workspace_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (project_id, workspace_id),
    CONSTRAINT foreign_key_projects FOREIGN KEY (project_id) REFERENCES projects (id) ON DELETE CASCADE,
    CONSTRAINT foreign_key_workspaces FOREIGN KEY (workspace_id) REFERENCES workspaces (id) ON DELETE CASCADE
);

CREATE TABLE project_member
(
    id         VARCHAR(255) PRIMARY KEY,
    project_id VARCHAR(255) NOT NULL,
    user_id    VARCHAR(255) NOT NULL,
    role       VARCHAR(50)  NOT NULL,
    CONSTRAINT foreign_key_projects FOREIGN KEY (project_id) REFERENCES projects (id) ON DELETE CASCADE,
    CONSTRAINT unique_project_member_project_id_user_id UNIQUE (project_id, user_id)
);

CREATE INDEX index_workspaces_user_id ON workspaces (user_id);
CREATE INDEX index_workspace_project_project_id ON workspace_project (project_id);
CREATE INDEX index_workspace_project_workspace_id ON workspace_project (workspace_id);
CREATE INDEX index_columns_project_id ON columns (project_id);
CREATE INDEX index_columns_position ON columns (project_id, position);
CREATE INDEX index_tasks_column_id ON tasks (column_id);
CREATE INDEX index_tasks_position ON tasks (column_id, position);
CREATE INDEX index_tasks_assignee_id ON tasks (assignee_id);
CREATE INDEX index_project_member_project_id_user_id ON project_member (project_id, user_id);
CREATE INDEX index_outbox_events_polling ON outbox_events(status, retry_count, created_at);