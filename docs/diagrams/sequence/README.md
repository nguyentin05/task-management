# Sequence Diagrams (Dynamic Views)

Thư mục này chứa các **sơ đồ luồng nghiệp vụ** (Sequence / Dynamic Diagram) của hệ thống **Task Management**, được tạo từ **Structurizr** và export ra nhiều định dạng khác nhau.

Các sơ đồ được xây dựng theo chuẩn **C4 Model — Dynamic View**, mô tả cách các container (microservice) tương tác với nhau trong từng luồng xử lý cụ thể.

## Danh sách sơ đồ

| Tên luồng | Mô tả | Xem hình |
|-----------|-------|----------|
| [Register User](#1-register-user--đăng-ký-tài-khoản) | Đăng ký tài khoản và tạo profile mặc định | [SVG](./images/structurizr-RegisterUser.svg) |
| [Delete Task](#2-delete-task--xóa-task) | Xóa task và cascade xóa comment qua event | [SVG](./images/structurizr-DeleteTask.svg) |
| [Update Avatar](#3-update-avatar--cập-nhật-ảnh-đại-diện) | Upload ảnh đại diện lên Cloudinary | [SVG](./images/structurizr-UpdateAvatar.svg) |
| [Get All Columns](#4-get-all-columns--lấy-danh-sách-cột-và-task) | Lấy danh sách Column kèm Task (nội bộ service) | [SVG](./images/structurizr-GetAllColumns.svg) |
| [Search To Invite](#5-search-to-invite--tìm-kiếm-user-để-mời) | Tìm kiếm user qua OpenFeign để mời vào project | [SVG](./images/structurizr-SearchToInvite.svg) |

---

## 1. Register User — Đăng ký tài khoản

**File:** [`images/structurizr-RegisterUser.svg`](./images/structurizr-RegisterUser.svg)

**Mô tả:** Luồng người dùng điền form đăng ký, hệ thống lưu tài khoản vào PostgreSQL, sau đó publish sự kiện `user.created` lên RabbitMQ. Profile Service và Task Service lần lượt subscribe và tự động tạo **default Profile** và **default Workspace**.

**Bước xử lý:**

| # | Actor / Service          | Hành động |
|---|--------------------------|------------|
| 1 | User → UI                | Submit form đăng ký |
| 2 | UI → API Gateway         | `POST /api/auth/register` (JSON/HTTP) |
| 3 | API Gateway → Auth Service | Forward request |
| 4 | Auth Service → Auth DB   | Lưu thông tin User mới (JDBC) |
| 5 | Auth Service → RabbitMQ  | Publish `user.created` event |
| 6 | Profile Service → RabbitMQ | Subscribe `user.created` event |
| 7 | Profile Service → Profile DB | Tạo default Profile Node (Bolt) |
| 8 | Task Service → RabbitMQ  | Subscribe `user.created` event |
| 9 | Task Service → Task DB   | Tạo default Workspace (JDBC) |

> Pattern: **Transactional Outbox** — Auth Service ghi event vào bảng `outbox_events` trước khi publish, đảm bảo tính nhất quán.

---

## 2. Delete Task — Xóa Task

**File:** [`images/structurizr-DeleteTask.svg`](./images/structurizr-DeleteTask.svg)

**Mô tả:** Luồng người dùng xóa một Task. Task Service xóa task khỏi PostgreSQL, sau đó publish sự kiện `task.deleted`. Comment Service subscribe và tự động **xóa toàn bộ comment** của task đó trong MongoDB.

**Bước xử lý:**

| # | Actor / Service          | Hành động |
|---|--------------------------|------------|
| 1 | User → UI                | Bấm xóa Task |
| 2 | UI → API Gateway         | `DELETE /api/tasks/{id}` (JSON/HTTP) |
| 3 | API Gateway → Task Service | Forward request |
| 4 | Task Service → Task DB   | Xóa Task (JDBC) |
| 5 | Task Service → RabbitMQ  | Publish `task.deleted` event |
| 6 | Comment Service → RabbitMQ | Subscribe event |
| 7 | Comment Service → Comment DB | Xóa toàn bộ Comment của Task (MongoDB Wire Protocol) |

> Pattern: **Event-driven cascade delete** — không dùng foreign key xuyên service, mà dùng event để xóa comment liên quan.

---

## 3. Update Avatar — Cập nhật ảnh đại diện

**File:** [`images/structurizr-UpdateAvatar.svg`](./images/structurizr-UpdateAvatar.svg)

**Mô tả:** Luồng người dùng upload ảnh đại diện mới. Profile Service nhận request, upload ảnh lên **Cloudinary** và lưu lại `secure_url` vào Profile Node trong Neo4j.

**Bước xử lý:**

| # | Actor / Service           | Hành động |
|---|---------------------------|------------|
| 1 | User → UI                 | Chọn ảnh và upload |
| 2 | UI → API Gateway          | `POST /api/profiles/avatar` (JSON/HTTP) |
| 3 | API Gateway → Profile Service | Forward request |
| 4 | Profile Service → Cloudinary | Upload ảnh & nhận `secure_url` (HTTPS/REST) |
| 5 | Profile Service → Profile DB | Cập nhật URL vào Graph Node (Bolt) |

> Tích hợp: **Cloudinary SDK** — Profile Service gọi trực tiếp Cloudinary API để upload file, không qua CDN thủ công.

---

## 4. Get All Columns — Lấy danh sách Cột và Task

**File:** [`images/structurizr-GetAllColumns.svg`](./images/structurizr-GetAllColumns.svg)

**Mô tả:** Luồng nội bộ trong **Task Service** khi client yêu cầu danh sách Columns kèm toàn bộ Tasks. Mô tả chi tiết đến tầng Component (Security → Controller → Service → Repository).

**Bước xử lý:**

| # | Actor / Component                        | Hành động |
|---|------------------------------------------|------------|
| 1 | API Gateway → Security Filter Chain       | `GET /api/projects/{id}/columns` |
| 2 | Security Filter Chain → Controller        | Forward request sau khi xác thực JWT |
| 3 | Controller → Service                      | `getColumnsWithTasks(projectId)` |
| 4 | Service → Repository                      | Query joined data |
| 5 | Repository → Task DB                      | `SELECT columns, tasks...` (JDBC) |

> Kiến trúc: **Layered Architecture** trong mỗi microservice — Security → Controller → Service → Repository.

---

## 5. Search To Invite — Tìm kiếm User để mời

**File:** [`images/structurizr-SearchToInvite.svg`](./images/structurizr-SearchToInvite.svg)

**Mô tả:** Luồng người dùng gõ keyword (email hoặc tên) để tìm kiếm user nhằm mời vào project. Task Service gọi đồng thời hai service khác qua **OpenFeign** để lấy thông tin xác thực và profile.

**Bước xử lý:**

| # | Actor / Service              | Hành động |
|---|------------------------------|------------|
| 1 | User → UI                    | Gõ keyword (email/name) |
| 2 | UI → API Gateway             | `GET /api/tasks/search-users` (JSON/HTTP) |
| 3 | API Gateway → Task Service   | Forward request |
| 4 | Task Service → Auth Service  | Fetch Auth info via **Feign Client** (OpenFeign/HTTP) |
| 5 | Task Service → Profile Service | Fetch Profile info via **Feign Client** (OpenFeign/HTTP) |

> Pattern: **Service-to-Service (Sync)** qua OpenFeign — Task Service tổng hợp kết quả từ Auth và Profile trước khi trả về client.

---

## Source Files

Mỗi sơ đồ được cung cấp ở nhiều định dạng để phù hợp với các công cụ khác nhau:

| Luồng             | PlantUML (C4)                                                       | Mermaid                                                           | WebSequenceDiagram                              | SVG Image                                              |
|-------------------|----------------------------------------------------------------------|-------------------------------------------------------------------|-------------------------------------------------|--------------------------------------------------------|
| Register User     | [`.puml`](./plantuml/structurizr-RegisterUser.puml)                 | [`.mmd`](./mermaid/structurizr-RegisterUser.mmd)                  | [`.wsd`](./wsd/structurizr-RegisterUser.wsd)    | [`.svg`](./images/structurizr-RegisterUser.svg)        |
| Delete Task       | [`.puml`](./plantuml/structurizr-DeleteTask.puml)                   | [`.mmd`](./mermaid/structurizr-DeleteTask.mmd)                    | [`.wsd`](./wsd/structurizr-DeleteTask.wsd)      | [`.svg`](./images/structurizr-DeleteTask.svg)          |
| Update Avatar     | [`.puml`](./plantuml/structurizr-UpdateAvatar.puml)                 | [`.mmd`](./mermaid/structurizr-UpdateAvatar.mmd)                  | [`.wsd`](./wsd/structurizr-UpdateAvatar.wsd)    | [`.svg`](./images/structurizr-UpdateAvatar.svg)        |
| Get All Columns   | [`.puml`](./plantuml/structurizr-GetAllColumns.puml)                | [`.mmd`](./mermaid/structurizr-GetAllColumns.mmd)                 | [`.wsd`](./wsd/structurizr-GetAllColumns.wsd)   | [`.svg`](./images/structurizr-GetAllColumns.svg)       |
| Search To Invite  | [`.puml`](./plantuml/structurizr-SearchToInvite.puml)               | [`.mmd`](./mermaid/structurizr-SearchToInvite.mmd)                | [`.wsd`](./wsd/structurizr-SearchToInvite.wsd)  | [`.svg`](./images/structurizr-SearchToInvite.svg)      |

## Công cụ

| Định dạng | Công cụ render |
|-----------|---------------|
| `.puml`   | [PlantUML](https://plantuml.com) (C4-PlantUML) |
| `.mmd`    | [Mermaid](https://mermaid.js.org) |
| `.wsd`    | [WebSequenceDiagrams](https://www.websequencediagrams.com) |
| `.svg`    | Export từ [Structurizr](https://structurizr.com) |

## Tham khảo

- [C4 Model — Dynamic Diagram](https://c4model.com/#DynamicDiagram)
- [Structurizr](https://structurizr.com)
- [C4-PlantUML](https://github.com/plantuml-stdlib/C4-PlantUML)
- [Outbox Pattern](https://microservices.io/patterns/data/transactional-outbox.html)
- [OpenFeign](https://spring.io/projects/spring-cloud-openfeign)
