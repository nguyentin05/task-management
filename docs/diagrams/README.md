# Diagrams

Thư mục này chứa toàn bộ tài liệu **kiến trúc trực quan** của hệ thống Task Management, được tổ chức theo từng loại sơ đồ để dễ tra cứu và bảo trì.

## Cấu trúc thư mục

```
diagrams/
├── deployment/          # Sơ đồ triển khai hệ thống (Deployment Diagram)
├── erd/                 # Sơ đồ thực thể quan hệ (Entity-Relationship Diagram)
├── sequence/            # Sơ đồ tuần tự luồng nghiệp vụ (Sequence Diagram)
├── class/               # Sơ đồ lớp từng microservice (Class Diagram)
└── usecase/             # Sơ đồ use case
```

## Mô tả các loại sơ đồ

| Thư mục | Loại sơ đồ | Mô tả |
|---|---|---|
| [`deployment/`](./deployment/README.md) | Deployment Diagram | Mô tả cách các container/service được triển khai trên môi trường Docker Compose, bao gồm mạng nội bộ, cơ sở dữ liệu và dịch vụ ngoài |
| [`erd/`](./erd/README.md) | Entity-Relationship Diagram | Mô tả cấu trúc bảng và các quan hệ giữa các thực thể dữ liệu trong từng service |
| [`sequence/`](./sequence/README.md) | Sequence Diagram | Mô tả luồng tương tác giữa các thành phần hệ thống khi thực hiện các ca sử dụng chính |
| [`class/`](./class/class-diagram.md) | Class Diagram | Mô tả cấu trúc các lớp domain model trong từng microservice |
| `usecase/` | Use Case Diagram | Mô tả các ca sử dụng của hệ thống |

## Công cụ sử dụng

Các sơ đồ trong dự án được tạo và quản lý bằng:

- **[Structurizr](https://structurizr.com/)** — Công cụ mô hình hóa kiến trúc theo mô hình C4 (Context, Container, Component, Code). Đây là nguồn sự thật chính (source of truth) cho toàn bộ kiến trúc.
- **[Mermaid](https://mermaid.js.org/)** (`.mmd`) — Sơ đồ dạng text-as-code, render trực tiếp trên GitHub.
- **[PlantUML](https://plantuml.com/)** (`.puml`) — Sơ đồ dạng văn bản, hỗ trợ xuất ảnh với nhiều theme.
- **[WebSequenceDiagrams](https://www.websequencediagrams.com/)** (`.wsd`) — File nguồn cho sơ đồ tuần tự.
- **[DBML](https://dbml.dbdiagram.io/)** (`.dbml`) — Định nghĩa schema cơ sở dữ liệu dạng văn bản, dùng trên [dbdiagram.io](https://dbdiagram.io/).

## Quy ước

- Mỗi thư mục con có thư mục `images/` chứa ảnh render sẵn (`.png`, `.svg`) để nhúng vào tài liệu.
- Các file nguồn (`.mmd`, `.puml`, `.wsd`, `.dbml`) là nguồn có thể chỉnh sửa và render lại khi cần.
- Tên file theo pattern: `structurizr-<ViewName>.<ext>` đối với các sơ đồ sinh từ Structurizr.

---

> Xem thêm: [ADRs](../adrs/) | [Tài liệu gốc dự án](../../README.md)
