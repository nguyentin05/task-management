CREATE SCHEMA IF NOT EXISTS auth_schema;
SET
search_path TO auth_schema;

CREATE TABLE users
(
    id       VARCHAR(255) PRIMARY KEY,
    email    VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE roles
(
    name        VARCHAR(255) PRIMARY KEY,
    description TEXT
);

CREATE TABLE permissions
(
    name        VARCHAR(255) PRIMARY KEY,
    description TEXT
);

CREATE TABLE invalidated_tokens
(
    id          VARCHAR(255) PRIMARY KEY,
    expiry_time TIMESTAMPTZ
);

CREATE TABLE outbox_events
(
    id          VARCHAR(255) PRIMARY KEY,
    routing_key VARCHAR(255) NOT NULL,
    payload     TEXT         NOT NULL,
    status      VARCHAR(50)  NOT NULL,
    created_at  TIMESTAMPTZ           DEFAULT CURRENT_TIMESTAMP,
    retry_count INTEGER      NOT NULL DEFAULT 0
);

CREATE TABLE user_role
(
    user_id   VARCHAR(255),
    role_name VARCHAR(255),
    PRIMARY KEY (user_id, role_name),
    CONSTRAINT fk_ur_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_ur_role FOREIGN KEY (role_name) REFERENCES roles (name) ON DELETE CASCADE
);

CREATE TABLE role_permission
(
    role_name       VARCHAR(255),
    permission_name VARCHAR(255),
    PRIMARY KEY (role_name, permission_name),
    CONSTRAINT fk_rp_role FOREIGN KEY (role_name) REFERENCES roles (name) ON DELETE CASCADE,
    CONSTRAINT fk_rp_permission FOREIGN KEY (permission_name) REFERENCES permissions (name) ON DELETE CASCADE
);

CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_outbox_status_created_at ON outbox_events (status, created_at);