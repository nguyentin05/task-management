# Deployment Diagrams

Thư mục này chứa các sơ đồ triển khai mô tả cách toàn bộ hệ thống Task Management được đóng gói và triển khai.

## Cấu trúc thư mục

```
deployment/
├── images/
│   └── structurizr-DeploymentView.svg
├── mermaid/
│   └── structurizr-DeploymentView.mmd
└── plantuml/
    └── structurizr-DeploymentView.puml
```

## Deployment View

Sơ đồ mô tả kiến trúc triển khai của hệ thống bằng Docker, bao gồm hai mạng container riêng biệt:

### app-network — Mạng ứng dụng

| Container                | Image                                                             |
| ------------------------ | ----------------------------------------------------------------- |
| `frontend`               | ghcr.io/nguyentin05/task-management/frontend:latest               |
| `api-gateway`            | ghcr.io/nguyentin05/task-management/api-gateway:latest            |
| `authentication-service` | ghcr.io/nguyentin05/task-management/authentication-service:latest |
| `profile-service`        | ghcr.io/nguyentin05/task-management/profile-service:latest        |
| `task-service`           | ghcr.io/nguyentin05/task-management/task-service:latest           |
| `comment-service`        | ghcr.io/nguyentin05/task-management/comment-service:latest        |
| `notification-service`   | ghcr.io/nguyentin05/task-management/notification-service:latest   |
| `rabbitmq`               | rabbitmq:3-management                                             |
| `ngrok`                  | ngrok/ngrok:latest                                                |

### db-network — Mạng cơ sở dữ liệu

| Container                      | Image                           |
| ------------------------------ | ------------------------------- |
| `postgres` (Authentication DB) | postgres:16                     |
| `postgres` (Task DB)           | postgres:16                     |
| `neo4j` (Profile DB)           | neo4j:2026.01.4-community-ubi10 |
| `mongodb` (Comment DB)         | mongo:7.0                       |

### External Cloud Services

| Dịch vụ        | Mô tả                           |
| -------------- | ------------------------------- |
| **Brevo**      | Gửi email thông báo             |
| **Cloudinary** | Lưu trữ và quản lý ảnh đại diện |
