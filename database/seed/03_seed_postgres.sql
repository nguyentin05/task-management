SET search_path TO auth_schema;

INSERT INTO roles (name, description)
VALUES ('ROLE_USER', 'Standard User'),
       ('ROLE_ADMIN', 'Administrator') ON CONFLICT DO NOTHING;

-- Mật khẩu mặc định: 'Test1234!'
INSERT INTO users (id, email, password)
VALUES ('89a54de8-6090-4248-ba8b-7a8399b918c8', 'alice@example.com', '$2a$10$iuNVN6rIBtJes/Ip.XlU8e0cJXiYtbNOKfOCsnuksD5z3.urIwCLK'),
       ('aaf5d938-8871-41fd-ba32-8c87218d50e0', 'bob@example.com', '$2a$10$QJaX14.jhQRc8weGOEyOE.NDYnB7WonvHKBkmkPMi1f2nFB3sJvfm')
ON CONFLICT DO NOTHING;

INSERT INTO user_role (user_id, role_name)
VALUES ('89a54de8-6090-4248-ba8b-7a8399b918c8', 'ROLE_ADMIN'),
       ('aaf5d938-8871-41fd-ba32-8c87218d50e0', 'ROLE_USER')
ON CONFLICT DO NOTHING;

SET search_path TO task_schema;

INSERT INTO workspaces (id, user_id, name, description)
VALUES ('2a49a7a0-9649-41a7-b57c-abcdc36d1d3d', '89a54de8-6090-4248-ba8b-7a8399b918c8', 'Alice Workspace', 'Private workspace of Alice'),
       ('2a368eb9-bb9a-4f5c-9e60-517e46c369dd', 'aaf5d938-8871-41fd-ba32-8c87218d50e0', 'Bob Workspace', 'Private workspace of Bob')
ON CONFLICT DO NOTHING;

INSERT INTO projects (id, name, description, created_by, start_at, end_at)
VALUES ('dcf76e70-ab22-406c-9cdd-dcb0d11d7847', 'Capstone Project', 'Task Management System Capstone',
        '89a54de8-6090-4248-ba8b-7a8399b918c8', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days')
ON CONFLICT DO NOTHING;

INSERT INTO workspace_project (project_id, workspace_id)
VALUES ('dcf76e70-ab22-406c-9cdd-dcb0d11d7847', '2a49a7a0-9649-41a7-b57c-abcdc36d1d3d')
ON CONFLICT DO NOTHING;

INSERT INTO project_member (id, project_id, user_id, role)
VALUES ('09c01652-4b96-41ba-9a5d-e6e3dffea7d6', 'dcf76e70-ab22-406c-9cdd-dcb0d11d7847',
        '89a54de8-6090-4248-ba8b-7a8399b918c8', 'PROJECT_MANAGER'),
       ('4a583ce0-af73-48e9-beef-351b38387455', 'dcf76e70-ab22-406c-9cdd-dcb0d11d7847',
        'aaf5d938-8871-41fd-ba32-8c87218d50e0', 'MEMBER')
ON CONFLICT DO NOTHING;

INSERT INTO columns (id, project_id, name, position, is_done_column)
VALUES ('d93c2476-af82-4a4f-89d6-a1dfaea9844e', 'dcf76e70-ab22-406c-9cdd-dcb0d11d7847', 'To Do', 100.0, FALSE),
       ('9cdf2fec-640a-46fd-b7b9-65867038c444', 'dcf76e70-ab22-406c-9cdd-dcb0d11d7847', 'In Progress', 200.0, FALSE),
       ('3d30b5b5-692d-4fe1-94bf-318a9e28daee', 'dcf76e70-ab22-406c-9cdd-dcb0d11d7847', 'Done', 300.0, TRUE)
ON CONFLICT DO NOTHING;

INSERT INTO tasks (id, column_id, title, description, position, created_by, assignee_id, label)
VALUES ('8a3f5bca-5195-4af6-bf2a-a0adc482452a', 'd93c2476-af82-4a4f-89d6-a1dfaea9844e',
        'Cấu trúc thư mục Microservices', 'Sử dụng Spring Boot và Maven Multi-module', 100.0,
        '89a54de8-6090-4248-ba8b-7a8399b918c8', '89a54de8-6090-4248-ba8b-7a8399b918c8', 'BACKEND'),
       ('4afbe345-54f8-4751-844d-2b84c77a78ed', 'd93c2476-af82-4a4f-89d6-a1dfaea9844e',
        'Thiết kế Database (ERD)', 'Sắp xếp schema cho Postgre, Neo4j, MongoDB', 200.0,
        '89a54de8-6090-4248-ba8b-7a8399b918c8', 'aaf5d938-8871-41fd-ba32-8c87218d50e0', 'DATABASE'),
       ('950ebe06-499e-4d49-b9c8-ea095d753010', '9cdf2fec-640a-46fd-b7b9-65867038c444',
        'Implement API Gateway', 'Sử dụng Spring Cloud Gateway + WebFlux Filter', 100.0,
        '89a54de8-6090-4248-ba8b-7a8399b918c8', '89a54de8-6090-4248-ba8b-7a8399b918c8', 'GATEWAY'),
       ('89998628-ae9a-4890-b32c-535e785e92bb', '3d30b5b5-692d-4fe1-94bf-318a9e28daee',
        'Setup CI/CD Github Actions', 'Cấu hình build tự động Docker image', 100.0,
        '89a54de8-6090-4248-ba8b-7a8399b918c8', 'aaf5d938-8871-41fd-ba32-8c87218d50e0', 'DEVOPS')
ON CONFLICT DO NOTHING;
