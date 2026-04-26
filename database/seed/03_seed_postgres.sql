SET
search_path TO auth_schema;

INSERT INTO roles (name, description)
VALUES ('ROLE_USER', 'Standard User'),
       ('ROLE_ADMIN', 'Administrator') ON CONFLICT DO NOTHING;

--mật khẩu măc định là 'Test1234!' nhé--
INSERT INTO users (id, email, password)
VALUES ('alice-123', 'alice@example.com', '$2a$12$R9h/cIPz0gi.URNNX3kh2OPST9/PgBkqquzi.Ss7KIUgO2t0jWMUW'),
       ('bob-456', 'bob@example.com',
        '$2a$12$R9h/cIPz0gi.URNNX3kh2OPST9/PgBkqquzi.Ss7KIUgO2t0jWMUW') ON CONFLICT DO NOTHING;

INSERT INTO user_role (user_id, role_name)
VALUES ('alice-123', 'ROLE_ADMIN'),
       ('bob-456', 'ROLE_USER') ON CONFLICT DO NOTHING;

SET
search_path TO task_schema;

INSERT INTO workspaces (id, user_id, name, description)
VALUES ('workspace-alice', 'alice-123', 'Alice Workspace', 'Private workspace of Alice'),
       ('workspace-bob', 'bob-456', 'Bob Workspace', 'Private workspace of Bob') ON CONFLICT DO NOTHING;

INSERT INTO projects (id, name, description, created_by, start_at, end_at)
VALUES ('project-1', 'Capstone Project', 'Task Management System Capstone', 'alice-123', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP + INTERVAL '30 days') ON CONFLICT DO NOTHING;

INSERT INTO workspace_project (project_id, workspace_id)
VALUES ('project-1', 'workspace-alice') ON CONFLICT DO NOTHING;

INSERT INTO project_member (id, project_id, user_id, role)
VALUES ('pm-1', 'project-1', 'alice-123', 'PROJECT_MANAGER'),
       ('pm-2', 'project-1', 'bob-456', 'MEMBER') ON CONFLICT DO NOTHING;

INSERT INTO columns (id, project_id, name, position, is_done_column)
VALUES ('col-1', 'project-1', 'To Do', 100.0, FALSE),
       ('col-2', 'project-1', 'In Progress', 200.0, FALSE),
       ('col-3', 'project-1', 'Done', 300.0, TRUE) ON CONFLICT DO NOTHING;

INSERT INTO tasks (id, column_id, title, description, position, created_by, assignee_id, label)
VALUES ('task-1', 'col-1', 'Cấu trúc thư mục Microservices', 'Sử dụng Spring Boot và Maven Multi-module', 100.0,
        'alice-123', 'alice-123', 'BACKEND'),
       ('task-2', 'col-1', 'Thiết kế Database (ERD)', 'Sắp xếp schema cho Postgre, Neo4j, MongoDB', 200.0, 'alice-123',
        'bob-456', 'DATABASE'),
       ('task-3', 'col-2', 'Implement API Gateway', 'Sử dụng Spring Cloud Gateway + WebFlux Filter', 100.0, 'alice-123',
        'alice-123', 'GATEWAY'),
       ('task-4', 'col-3', 'Setup CI/CD Github Actions', 'Cấu hình build tự động Docker image', 100.0, 'alice-123',
        'bob-456', 'DEVOPS') ON CONFLICT DO NOTHING;
