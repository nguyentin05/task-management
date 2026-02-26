CREATE TABLE users (
    id VARCHAR(255) PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE role (
    name VARCHAR(255) PRIMARY KEY,
    description TEXT
);

CREATE TABLE permission (
    name VARCHAR(255) PRIMARY KEY,
    description TEXT
);

CREATE TABLE invalidated_token (
    id VARCHAR(255) PRIMARY KEY,
    expiry_time TIMESTAMPTZ
);

CREATE TABLE user_role (
    user_id VARCHAR(255),
    role_name VARCHAR(255),
    PRIMARY KEY (user_id, role_name),
    CONSTRAINT fk_ur_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_ur_role FOREIGN KEY (role_name) REFERENCES role(name) ON DELETE CASCADE
);

CREATE TABLE role_permission (
    role_name VARCHAR(255),
    permission_name VARCHAR(255),
    PRIMARY KEY (role_name, permission_name),
    CONSTRAINT fk_rp_role FOREIGN KEY (role_name) REFERENCES role(name) ON DELETE CASCADE,
    CONSTRAINT fk_rp_permission FOREIGN KEY (permission_name) REFERENCES permission(name) ON DELETE CASCADE
);

CREATE INDEX idx_users_email ON users(email);