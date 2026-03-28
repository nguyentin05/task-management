# Documentation

Tài liệu dự án **Task Management** — hệ thống quản lý công việc trello clone.

## Cấu trúc thư mục

```
docs/
├── adrs/           # Architecture Decision Records — lưu lại các quyết định kiến trúc
├── api/            # Tài liệu API (OpenAPI specs, PDF)
├── architecture/   # Tài liệu kiến trúc hệ thống (arc42, C4 Model)
└── diagrams/       # Các loại sơ đồ bổ sung (class, sequence, deployment, usecase)
```

## Tổng quan

| Thư mục                            | Mô tả                                           | Công cụ                                 |
|------------------------------------|-------------------------------------------------|-----------------------------------------|
| [`adrs/`](./adrs/)                 | Các quyết định kiến trúc theo chuẩn ADR         | Markdown                                |
| [`api/`](./api/)                   | OpenAPI specs và API docs tổng hợp              | springdoc, PDF                          |
| [`architecture/`](./architecture/) | Tài liệu kiến trúc theo chuẩn arc42 và C4 Model | Structurizr, PlantUML, Mermaid, draw.io |
| [`diagrams/`](./diagrams/)         | Sơ đồ class, sequence, deployment, usecase      | Structurizr, PlantUML, Mermaid          |

## Liên kết nhanh

- [ADR Index](./adrs/README.md)
- [API Documentation](./api/README.md)
- [Architecture Overview](./architecture/README.md)
- [C4 Model](./architecture/c4/README.md)
- [Sequence Diagrams](./diagrams/sequence/README.md)

## Tài liệu tham khảo

- [arc42](https://-arc42.org)
- [C4 Model](https://c4model.com)
- [Structurizr](https://structurizr.com)
- [ADR — Michael Nygard](https://cognitect.com/blog/2011/11/15/documenting-architecture-decisions)
- [Microservices Patterns](https://microservices.io)
- Software Architecture for Developers - Simon Brown 
- Fundamentals of Software Architecture - Mark Richards & Neal Ford (O'Reilly)
 