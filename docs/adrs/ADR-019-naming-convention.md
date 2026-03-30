# ADR-019: Phong cách đặt tên

Date: 2026-03-04 - Accepted
Date: 2026-03-08 - Implemented

## Status

Implemented

## Context

Hệ thống Task Management gồm nhiều thành phần: backend Java, frontend React, database, API endpoint, và tài liệu kỹ
thuật. Mỗi thành phần có quy ước đặt tên riêng nếu không được thống nhất sẽ dẫn đến code không nhất quán, khó đọc và khó
bảo trì.

## Decision

Áp dụng quy tắc đặt tên theo từng lớp kỹ thuật như sau:

### Java (Backend)

| Thành phần             | Quy tắc                  | Ví dụ                                    |
|------------------------|--------------------------|------------------------------------------|
| Class, Interface, Enum | PascalCase               | `UserService`, `ProjectStatus`           |
| Method, variable       | camelCase                | `findById()`, `accessToken`              |
| Constant               | UPPER_SNAKE_CASE         | `MAX_PAGE_SIZE`, `DEFAULT_ROLE`          |
| Package                | lowercase, dot-separated | `com.ntt.authentication.service`         |
| Module / Service       | kebab-case               | `authentication-service`, `task-service` |

Quy tắc đặt tên Class theo chức năng:

```
Controller   →  UserController
Service      →  UserService
Repository   →  UserRepository
Domain       →  User, Project, Task
DTO Request  →  UserCreationRequest, UserUpdateRequest
DTO Response →  UserResponse
Mapper       →  UserMapper
Config       →  SecurityConfig
```

### Database

| Thành phần     | Quy tắc                | Ví dụ                                |
|----------------|------------------------|--------------------------------------|
| Table          | snake_case, số nhiều   | `users`, `project_members`           |
| Column         | snake_case             | `created_at`, `user_id`              |
| Foreign key    | `{table_singular}_id`  | `project_id`, `user_id`              |
| Index          | `idx_{table}_{column}` | `idx_tasks_column_id`                |
| Junction table | `{table1}_{table2}`    | `workspace_project`, `task_assignee` |

### API Endpoint

| Thành phần        | Quy tắc              | Ví dụ                                        |
|-------------------|----------------------|----------------------------------------------|
| Path              | kebab-case, số nhiều | `/projects`, `/project-members`              |
| Path param        | camelCase trong `{}` | `{projectId}`, `{userId}`                    |
| Query param       | camelCase            | `?page=0&size=10`                            |
| Internal endpoint | prefix `/internal/`  | `/internal/profiles`, `/internal/workspaces` |

### JSON Request / Response

| Thành phần | Quy tắc          | Ví dụ                        |
|------------|------------------|------------------------------|
| Field name | camelCase        | `firstName`, `projectStatus` |
| Enum value | UPPER_SNAKE_CASE | `"PLANNING"`, `"MANAGER"`    |
| Date/time  | ISO 8601         | `"2026-03-04T10:00:00Z"`     |

### React

| Thành phần         | Quy tắc                      | Ví dụ                          |
|--------------------|------------------------------|--------------------------------|
| Component file     | PascalCase                   | `UserProfile.jsx`, `Login.jsx` |
| Hook               | camelCase, prefix `use`      | `useAuth()`, `useProject()`    |
| Context            | PascalCase, suffix `Context` | `MyContexts.jsx`               |
| Variable, function | camelCase                    | `handleSubmit`, `userData`     |
| CSS class          | kebab-case                   | `user-profile`, `nav-header`   |

### File & Thư mục

| Thành phần       | Quy tắc                      | Ví dụ                                |
|------------------|------------------------------|--------------------------------------|
| Java source file | PascalCase                   | `UserService.java`                   |
| React component  | PascalCase                   | `Header.jsx`                         |
| Config file      | kebab-case                   | `docker-compose.yml`, `nginx.conf`   |
| ADR file         | kebab-case với số thứ tự     | `ADR-019-naming-convention.md`       |
| Migration script | `{number}_{description}.sql` | `01_init_authentication_service.sql` |

## Consequences

**Tích cực:**

- Code nhất quán giữa các service và các developer, giảm thời gian đọc hiểu code của người khác.
- Dễ tìm kiếm và điều hướng trong IDE nhờ quy tắc rõ ràng theo chức năng.
- Giảm thời gian review code vì không phải tranh luận về cách đặt tên.
- Onboarding developer mới nhanh hơn — chỉ cần đọc ADR này là hiểu toàn bộ quy ước.

**Tiêu cực:**

- Phải cẩn thận khi refactor tên cũ không đúng chuẩn trong code đã viết.
- Một số quy tắc cần nhớ nhiều.