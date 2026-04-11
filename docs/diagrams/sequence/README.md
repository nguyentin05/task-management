# Sequence Diagrams

Thư mục này chứa các sơ đồ tuần tự mô tả luồng tương tác giữa các thành phần hệ thống khi thực hiện các thao tác quan trọng trong hệ thống Task Management.

Các sơ đồ được sinh từ Structurizr và lưu dưới nhiều định dạng để phục vụ các công cụ khác nhau.

## Cấu trúc thư mục

```
sequence/
├── images/
│   ├── structurizr-RegisterUser.svg
│   └── structurizr-UpdateAvatar.svg
│
├── mermaid/
│   ├── structurizr-RegisterUser.mmd
│   └── structurizr-UpdateAvatar.mmd
├── plantuml/
│   ├── structurizr-RegisterUser.puml
│   └── structurizr-UpdateAvatar.puml
├── wsd/
│    ├── structurizr-RegisterUser.wsd
│    └── structurizr-UpdateAvatar.wsd
└── ...
```

---

## Danh sách sơ đồ

### 1. Register User — Đăng ký người dùng mới

**File:** `structurizr-RegisterUser.*`

Mô tả luồng đăng ký tài khoản và khởi tạo dữ liệu mặc định sau khi đăng ký thành công.

| Bước | Từ                     | Đến                    | Mô tả                            |
| ---- | ---------------------- | ---------------------- | -------------------------------- |
| 1    | User                   | React UI               | Submit form đăng ký              |
| 2    | React UI               | API Gateway            | `POST /api/auth/register`        |
| 3    | API Gateway            | Authentication Service | Forward request                  |
| 4    | Authentication Service | Authentication DB      | Lưu thông tin User mới (JDBC)    |
| 5    | Authentication Service | RabbitMQ               | Publish sự kiện `user.created`   |
| 6    | Profile Service        | RabbitMQ               | Subscribe sự kiện `user.created` |
| 7    | Profile Service        | Profile DB (Neo4j)     | Tạo Profile mặc định (Bolt)      |
| 8    | Task Service           | RabbitMQ               | Subscribe sự kiện `user.created` |
| 9    | Task Service           | Task DB (PostgreSQL)   | Tạo Workspace mặc định (JDBC)    |

> Luồng này thể hiện **Outbox Pattern + Event-Driven Architecture**: Authentication Service không gọi trực tiếp Profile/Task Service, mà publish event lên RabbitMQ để đảm bảo tính decoupled và fault-tolerance.

---

### 2. Delete Task — Xóa task

**File:** `structurizr-DeleteTask.*`

Mô tả luồng xóa một task, kèm dọn dẹp toàn bộ comment liên quan bất đồng bộ.

| Bước | Từ              | Đến                  | Mô tả                            |
| ---- | --------------- | -------------------- | -------------------------------- |
| 1    | User            | React UI             | Bấm xóa Task                     |
| 2    | React UI        | API Gateway          | `DELETE /api/tasks/{id}`         |
| 3    | API Gateway     | Task Service         | Forward request                  |
| 4    | Task Service    | Task DB (PostgreSQL) | Xóa bản ghi Task (JDBC)          |
| 5    | Task Service    | RabbitMQ             | Publish sự kiện `task.deleted`   |
| 6    | Comment Service | RabbitMQ             | Subscribe sự kiện `task.deleted` |
| 7    | Comment Service | Comment DB (MongoDB) | Xóa toàn bộ Comment của Task     |

> Comment Service không bị gọi trực tiếp — đảm bảo **loose coupling**. Việc xóa comment xảy ra bất đồng bộ sau khi Task đã xóa.

---

### 3. Get All Columns — Lấy danh sách cột Kanban

**File:** `structurizr-GetAllColumns.*`

Mô tả luồng lấy toàn bộ cột (Columns) kèm danh sách Task bên trong một Project, bao gồm xác thực JWT tại Security Filter Chain.

| Bước | Từ                    | Đến                   | Mô tả                                |
| ---- | --------------------- | --------------------- | ------------------------------------ |
| 1    | API Gateway           | Security Filter Chain | `GET /api/projects/{id}/columns`     |
| 2    | Security Filter Chain | Controller            | Forward request sau khi xác thực JWT |
| 3    | Controller            | Service               | Gọi `getColumnsWithTasks(projectId)` |
| 4    | Service               | Repository            | Query joined data                    |
| 5    | Repository            | Task DB (PostgreSQL)  | `SELECT columns, tasks...` (JDBC)    |

> Luồng nội bộ trong **Task Service** theo kiến trúc phân lớp: Controller → Service → Repository → DB.

---

### 4. Search To Invite — Tìm kiếm user để mời vào dự án

**File:** `structurizr-SearchToInvite.*`

Mô tả luồng tìm kiếm người dùng theo email/tên để mời vào project, sử dụng OpenFeign gọi chéo service.

| Bước | Từ           | Đến                    | Mô tả                               |
| ---- | ------------ | ---------------------- | ----------------------------------- |
| 1    | User         | React UI               | Gõ keyword (email/name)             |
| 2    | React UI     | API Gateway            | `GET /api/tasks/search-users`       |
| 3    | API Gateway  | Task Service           | Forward request                     |
| 4    | Task Service | Authentication Service | Fetch Auth info (OpenFeign/HTTP)    |
| 5    | Task Service | Profile Service        | Fetch Profile info (OpenFeign/HTTP) |

> Task Service tổng hợp thông tin từ 2 service khác nhau bằng **OpenFeign Client** để trả về kết quả đầy đủ cho client.

---

### 5. Update Avatar — Cập nhật ảnh đại diện

**File:** `structurizr-UpdateAvatar.*`

Mô tả luồng người dùng upload ảnh đại diện, sử dụng Cloudinary làm CDN lưu trữ ảnh.

| Bước | Từ              | Đến                | Mô tả                                        |
| ---- | --------------- | ------------------ | -------------------------------------------- |
| 1    | User            | React UI           | Chọn ảnh và upload                           |
| 2    | React UI        | API Gateway        | `POST /api/profiles/avatar`                  |
| 3    | API Gateway     | Profile Service    | Forward request                              |
| 4    | Profile Service | Cloudinary         | Upload file & nhận `secure_url` (HTTPS/REST) |
| 5    | Profile Service | Profile DB (Neo4j) | Cập nhật URL vào Graph Node (Bolt)           |

> Profile Service lưu dữ liệu trong **Neo4j (Graph DB)** vì model thông tin người dùng phù hợp với cấu trúc graph (bạn bè, kết nối).

---

## Xem sơ đồ ảnh

| Ca sử dụng       | SVG                                                                                |
| ---------------- | ---------------------------------------------------------------------------------- |
| Register User    | [`images/structurizr-RegisterUser.svg`](./images/structurizr-RegisterUser.svg)     |
| Delete Task      | [`images/structurizr-DeleteTask.svg`](./images/structurizr-DeleteTask.svg)         |
| Get All Columns  | [`images/structurizr-GetAllColumns.svg`](./images/structurizr-GetAllColumns.svg)   |
| Search To Invite | [`images/structurizr-SearchToInvite.svg`](./images/structurizr-SearchToInvite.svg) |
| Update Avatar    | [`images/structurizr-UpdateAvatar.svg`](./images/structurizr-UpdateAvatar.svg)     |
