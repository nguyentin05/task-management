# Diagrams

Thư mục này chứa toàn bộ các sơ đồ kỹ thuật của hệ thống **Task Management** — một Trello Clone được xây dựng theo kiến trúc microservices.

## Cấu trúc thư mục

```
diagrams/
├── class/          # Sơ đồ lớp (Class Diagram) cho từng microservice
├── deployment/     # Sơ đồ triển khai (Deployment Diagram) — Docker Compose
├── erd/            # Entity Relationship Diagram cho từng database
├── sequence/       # Sơ đồ luồng nghiệp vụ (Sequence / Dynamic Diagram)
└── usecase/        # Sơ đồ use case (Use Case Diagram)
```

## Tổng quan các sơ đồ

| Thư mục                          | Loại sơ đồ          | Công cụ                             | Mô tả |
|----------------------------------|---------------------|-------------------------------------|--------|
| [`class/`](./class/)             | Class Diagram        | Mermaid                             | Mô hình lớp domain của từng service |
| [`deployment/`](./deployment/)   | Deployment Diagram   | Structurizr, Mermaid, PlantUML      | Kiến trúc triển khai Docker Compose |
| [`erd/`](./erd/)                 | ERD                  | dbdiagram.io (DBML)                 | Lược đồ cơ sở dữ liệu từng service |
| [`sequence/`](./sequence/)       | Sequence Diagram     | Structurizr, PlantUML, Mermaid, WSD | Luồng xử lý nghiệp vụ chính |
| [`usecase/`](./usecase/)         | Use Case Diagram     | —                                   | Các ca sử dụng hệ thống |

## Kiến trúc hệ thống (tóm tắt)

Hệ thống gồm các thành phần chính sau:

| Service / Thành phần       | Công nghệ              | Vai trò |
|----------------------------|------------------------|---------|
| **Frontend**               | React (Vite)           | Single Page Application, giao diện người dùng |
| **API Gateway**            | Spring Cloud Gateway   | Điều phối request, xác thực JWT |
| **Authentication Service** | Spring Boot            | Quản lý tài khoản, phát JWT, Outbox Pattern |
| **Profile Service**        | Spring Boot            | Quản lý hồ sơ người dùng, upload ảnh |
| **Task Service**           | Spring Boot            | Workspace, Project, Column, Task |
| **Comment Service**        | Spring Boot            | Bình luận trên Task |
| **Notification Service**   | Spring Boot            | Gửi email thông báo qua Brevo |
| **RabbitMQ**               | RabbitMQ               | Message broker giao tiếp bất đồng bộ |
| **PostgreSQL**             | PostgreSQL 16          | RDBMS cho Authentication và Task |
| **Neo4j**                  | Neo4j Community        | Graph DB cho Profile và quan hệ bạn bè |
| **MongoDB**                | MongoDB 7.0            | Document Store cho Comment |
| **Cloudinary**             | Cloudinary SaaS        | Lưu trữ và quản lý media/avatar |
| **Brevo**                  | Brevo SMTP             | Email notification |
| **Ngrok**                  | Ngrok Tunnel           | Expose API Gateway ra public URL |

## Liên kết nhanh

- [Class Diagrams](./class/class-diagram.md)
- [Deployment Diagram README](./deployment/README.md)
- [ERD README](./erd/README.md)
- [Sequence Diagrams README](./sequence/README.md)
