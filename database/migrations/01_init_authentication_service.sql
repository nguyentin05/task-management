CREATE SCHEMA IF NOT EXISTS auth_schema;
SET search_path TO auth_schema;

CREATE TABLE roles (
                       name VARCHAR(255) PRIMARY KEY,
                       description TEXT
);

CREATE TABLE users (
                       id VARCHAR(255) PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       created_at  TIMESTAMPTZ           DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE invalidated_tokens (
                                    id VARCHAR(255) PRIMARY KEY,
                                    expiry_time TIMESTAMPTZ
);

CREATE TABLE outbox_events (
                               id VARCHAR(255) PRIMARY KEY,
                               routing_key VARCHAR(255) NOT NULL,
                               payload TEXT NOT NULL,
                               status VARCHAR(50) NOT NULL,
                               created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                               retry_count INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE user_role (
                           user_id VARCHAR(255),
                           role_name VARCHAR(255),
                           PRIMARY KEY (user_id, role_name),
                           CONSTRAINT foreign_key_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                           CONSTRAINT foreign_key_role FOREIGN KEY (role_name) REFERENCES roles(name) ON DELETE CASCADE
);

CREATE INDEX index_users_email ON users(email);
CREATE INDEX index_invalidated_tokens_expiry_time ON invalidated_tokens(expiry_time);
CREATE INDEX index_outbox_events_polling ON outbox_events(status, retry_count, created_at);
