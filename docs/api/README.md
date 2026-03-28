# API Documentation

Tài liệu API của hệ thống Task Management.

## Cấu trúc

```
api/
├── openapi/    # OpenAPI 3.0 specs, tự động sinh từ CI pipeline
└── pdf/        # Tài liệu API tổng hợp dạng PDF
```

## OpenAPI Specs

Các file spec được tự động sinh mỗi khi có thay đổi code, thông qua CI pipeline sử dụng
`springdoc-openapi-maven-plugin`.

| Service                | Spec                                                                           |
|------------------------|--------------------------------------------------------------------------------|
| Authentication Service | [`openapi/authentication-service.yaml`](./openapi/authentication-service.yaml) |
| Profile Service        | [`openapi/profile-service.yaml`](./openapi/profile-service.yaml)               |
| Task Service           | [`openapi/task-service.yaml`](./openapi/task-service.yaml)                     |
| Comment Service        | [`openapi/profile-service.yaml`](./openapi/comment-service.yaml)               |
| Notification Service   | [`openapi/task-service.yaml`](./openapi/notification-service.yaml)             |

## API Response Structure

Tất cả API trả về theo cấu trúc thống nhất, xem thêm tại [ADR-015](../adrs/ADR-015-api-response-structure.md).