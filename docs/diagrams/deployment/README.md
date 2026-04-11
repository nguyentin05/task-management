# Deployment Diagrams

Thư mục này chứa các sơ đồ triển khai (**Deployment Diagram**) mô tả cách toàn bộ hệ thống Task Management được đóng gói và chạy trên môi trường **Docker Compose (Local)**.

## Cấu trúc thư mục

```
deployment/
├── images/
│   └── structurizr-DeploymentView.svg   # Ảnh SVG render từ Structurizr
├── mermaid/
│   └── structurizr-DeploymentView.mmd   # Sơ đồ Mermaid (text-as-code)
└── plantuml/
    └── structurizr-DeploymentView.puml  # Sơ đồ PlantUML
```

## Deployment View

Sơ đồ mô tả kiến trúc triển khai cục bộ (local) của hệ thống trên **Docker Desktop**, bao gồm hai mạng container riêng biệt:

### app-network — Mạng ứng dụng

| Container                | Công nghệ            | Mô tả                                         |
| ------------------------ | -------------------- | --------------------------------------------- |
| `task_frontend`          | React (SPA)          | Giao diện người dùng duy nhất                 |
| `api-gateway`            | Spring Cloud Gateway | Entry point, định tuyến toàn bộ request       |
| `authentication-service` | Spring Boot          | Xác thực, phân quyền JWT                      |
| `profile-service`        | Spring Boot          | Quản lý thông tin cá nhân, avatar             |
| `task-service`           | Spring Boot          | Quản lý workspace, project, column, task      |
| `comment-service`        | Spring Boot          | Quản lý comment theo task                     |
| `notification-service`   | Spring Boot          | Gửi thông báo qua email                       |
| `rabbitmq`               | RabbitMQ             | Message Broker trung gian giao tiếp async     |
| `ngrok`                  | Ngrok                | Tunnel public URL vào API Gateway (local dev) |

### 🗄️ db-network — Mạng cơ sở dữ liệu

| Container                      | Công nghệ        | Dùng bởi                 |
| ------------------------------ | ---------------- | ------------------------ |
| `postgres` (Authentication DB) | PostgreSQL       | `authentication-service` |
| `postgres` (Task DB)           | PostgreSQL       | `task-service`           |
| `neo4j` (Profile DB)           | Neo4j (Graph DB) | `profile-service`        |
| `mongodb` (Comment DB)         | MongoDB          | `comment-service`        |

### ☁️ External Cloud Services

| Dịch vụ          | Mô tả                           | Dùng bởi               |
| ---------------- | ------------------------------- | ---------------------- |
| **Brevo** (SMTP) | Gửi email thông báo             | `notification-service` |
| **Cloudinary**   | Lưu trữ và quản lý ảnh đại diện | `profile-service`      |

## 🔗 Luồng giao tiếp chính

```
User → [React UI] → [API Gateway] → [Service X]
                                  ↘ [authentication-service]
                                  ↘ [profile-service]
                                  ↘ [task-service]
                                  ↘ [comment-service]

[authentication-service] → publish event → [RabbitMQ]
[profile-service]        → subscribe      ← [RabbitMQ]
[task-service]           → subscribe      ← [RabbitMQ]
[comment-service]        → subscribe      ← [RabbitMQ]
[notification-service]   → subscribe      ← [RabbitMQ]
```

- **Giao thức đồng bộ**: JSON/HTTP (REST), OpenFeign (service-to-service), JDBC, Bolt, MongoDB Wire Protocol
- **Giao thức bất đồng bộ**: RabbitMQ (AMQP) qua Outbox Pattern

## Xem sơ đồ

![Deployment View](./images/structurizr-DeploymentView.svg)

---

> 📌 Nguồn file: [`mermaid/structurizr-DeploymentView.mmd`](./mermaid/structurizr-DeploymentView.mmd) | [`plantuml/structurizr-DeploymentView.puml`](./plantuml/structurizr-DeploymentView.puml)
