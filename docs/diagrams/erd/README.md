# Entity-Relationship Diagrams (ERD)

Thư mục này chứa các sơ đồ thực thể quan hệ mô tả cấu trúc cơ sở dữ liệu của từng microservice trong hệ thống Task Management.

## Cấu trúc thư mục

```
erd/
├── dbdiagram/
│   ├── authentication-service.dbml   # Schema DBML của Authentication Service
│   └── task-service.dbml             # Schema DBML của Task Service
└── images/
    ├── authentication-service.png    # ERD ảnh của Authentication Service
    ├── task-service.png              # ERD ảnh của Task Service
    ├── comment-service.png           # ERD ảnh của Comment Service
    └── data-context-diagram.png      # Tổng quan data context toàn hệ thống
```

## Tổng quan các cơ sở dữ liệu

Hệ thống áp dụng mỗi service sở hữu cơ sở dữ liệu riêng biệt.

| Service                | Database Engine | Loại               |
| ---------------------- | --------------- | ------------------ |
| Authentication Service | PostgreSQL      | Relational (RDBMS) |
| Task Service           | PostgreSQL      | Relational (RDBMS) |
| Profile Service        | Neo4j           | Graph DBMS         |
| Comment Service        | MongoDB         | Document Store     |

---

## Authentication Service — PostgreSQL

**File DBML:** [`dbdiagram/authentication-service.dbml`](./dbdiagram/authentication-service.dbml)

### Các bảng

| Bảng                 | Mô tả                                                                 |
| -------------------- | --------------------------------------------------------------------- |
| `users`              | Lưu thông tin tài khoản: `email`, `password` (đã hash), thời gian tạo |
| `roles`              | Danh sách vai trò (`ADMIN`, `USER`, ...)                              |
| `user_role`          | Bảng trung gian Many-to-Many giữa `users` và `roles`                  |
| `invalidated_tokens` | Lưu các JWT token đã bị thu hồi (logout/refresh)                      |
| `outbox_events`      | Sự kiện chờ publish lên RabbitMQ (Outbox Pattern)                     |

### Quan hệ

```
users (1) ──< user_role (N) >── (1) roles
```

- `user_role.user_id` → `users.id` (DELETE CASCADE)
- `user_role.role_name` → `roles.name` (DELETE CASCADE)

---

## Task Service — PostgreSQL

**File DBML:** [`dbdiagram/task-service.dbml`](./dbdiagram/task-service.dbml)

### Các bảng

| Bảng                | Mô tả                                                              |
| ------------------- | ------------------------------------------------------------------ |
| `workspaces`        | Mỗi user có 1 workspace cá nhân (1-to-1 với `user_id`)             |
| `projects`          | Dự án thuộc workspace, có thời gian bắt đầu/kết thúc               |
| `columns`           | Cột Kanban thuộc project, có `position` (số thực để sắp xếp)       |
| `tasks`             | Task thuộc column, có `position`, `label`, `assignee_id`, `due_at` |
| `workspace_project` | Bảng trung gian Many-to-Many giữa workspace và project             |
| `project_member`    | Thành viên của project với vai trò (`MANAGER` / `MEMBER`)          |
| `outbox_events`     | Sự kiện chờ publish (Outbox Pattern)                               |

### Quan hệ

```
workspaces (1) ──< workspace_project (N) >── (N) projects
projects (1) ──< columns (N)
columns (1) ──< tasks (N)
projects (1) ──< project_member (N)
```

- `columns.project_id` → `projects.id` (DELETE CASCADE)
- `tasks.column_id` → `columns.id` (DELETE CASCADE)
- `workspace_project.project_id` → `projects.id` (DELETE CASCADE)
- `workspace_project.workspace_id` → `workspaces.id` (DELETE CASCADE)
- `project_member.project_id` → `projects.id` (DELETE CASCADE)

---

## Comment Service — MongoDB

Comment Service sử dụng MongoDB là document store.

**Collection chính:** `comments`

| Trường            | Kiểu    | Mô tả                         |
| ----------------- | ------- | ----------------------------- |
| `id`              | String  | ID tài liệu                   |
| `taskId`          | String  | ID của Task liên quan         |
| `userId`          | String  | ID người viết comment         |
| `content`         | String  | Nội dung comment              |
| `isEdited`        | Boolean | Đã được chỉnh sửa chưa        |
| `parentCommentId` | String  | ID comment cha (hỗ trợ reply) |
| `createdAt`       | Instant | Thời gian tạo                 |
| `updatedAt`       | Instant | Thời gian cập nhật cuối       |

---

## Xem sơ đồ ảnh

| Sơ đồ                      | Ảnh                                                                        |
| -------------------------- | -------------------------------------------------------------------------- |
| Authentication Service ERD | [`images/authentication-service.png`](./images/authentication-service.png) |
| Task Service ERD           | [`images/task-service.png`](./images/task-service.png)                     |
| Comment Service ERD        | [`images/comment-service.png`](./images/comment-service.png)               |
| Data Context Overview      | [`images/data-context-diagram.png`](./images/data-context-diagram.png)     |
