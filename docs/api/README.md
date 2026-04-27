# API Documentation

Tài liệu API của hệ thống Task Management

## Cấu trúc

```
api/
├── openapi/    # OpenAPI 3.0 specs, tự động sinh từ CI pipeline
└── manual/     # Tài liệu API thủ công dạng markdown
```

## OpenAPI Specs

Các file spec được tự động sinh mỗi khi có thay đổi code, thông qua CI pipeline.

| Service                | Spec                                                                           |
|------------------------|--------------------------------------------------------------------------------|
| Authentication Service | [`openapi/authentication-service.yaml`](./openapi/authentication-service.yaml) |
| Profile Service        | [`openapi/profile-service.yaml`](./openapi/profile-service.yaml)               |
| Task Service           | [`openapi/task-service.yaml`](./openapi/task-service.yaml)                     |
| Comment Service        | [`openapi/comment-service.yaml`](./openapi/comment-service.yaml)               |
| Notification Service   | [`openapi/notification-service.yaml`](./openapi/notification-service.yaml)     |

## Manual Docs

Tài liệu viết tay bao gồm mô tả chi tiết các endpoint, request/response, mã lỗi và data models, xem thêm tại [
README.md](./manual/README.md).