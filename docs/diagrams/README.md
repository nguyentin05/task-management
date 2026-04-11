# Diagrams

Thư mục này chứa các sơ đồ mô hình hóa hệ thống Task Management, được tổ chức theo từng loại sơ đồ để dễ tra cứu và bảo trì.

## Cấu trúc thư mục

```
diagrams/
├── deployment/
├── erd/
├── sequence/
├── class/
└── usecase/
```

## Danh sách các loại sơ đồ

| Thư mục     | Loại sơ đồ             | Mô tả                                                                                                                                | README                           |
| ----------- | ---------------------- | ------------------------------------------------------------------------------------------------------------------------------------ | -------------------------------- |
| deployment/ | Sơ đồ triển khai       | Mô tả cách các container/service được triển khai trên môi trường Docker Compose, bao gồm mạng nội bộ, cơ sở dữ liệu và dịch vụ ngoài | [README](./deployment/README.md) |
| erd/        | Sơ đồ thực thể quan hệ | Mô tả cấu trúc bảng và các quan hệ giữa các thực thể dữ liệu trong từng service                                                      | [README](./erd/README.md)        |
| sequence/   | Sơ đồ tuần tự          | Mô tả luồng tương tác giữa các thành phần hệ thống khi thực hiện các ca sử dụng chính                                                | [README](./sequence/README.md)   |
| class/      | Sơ đồ lớp              | Mô tả cấu trúc các lớp domain model trong từng microservice                                                                          | [README](./class/README.md)      |
| usecase/    | Sơ đồ chức năng        | Mô tả các ca sử dụng của hệ thống                                                                                                    | [README](./usecase/README.md)    |

## Công cụ sử dụng

Các sơ đồ trong dự án được tạo và quản lý bằng:

- **Structurizr** — Công cụ mô hình hóa kiến trúc theo mô hình C4. Đây là nguồn chính cho toàn bộ kiến trúc.
- **Mermaid** — Sơ đồ dạng text-as-code, render trực tiếp trên GitHub.
- **PlantUML** — Sơ đồ dạng văn bản, hỗ trợ xuất ảnh với nhiều theme.
- **WebSequenceDiagrams** — File nguồn cho sơ đồ tuần tự.
- **DBML** — Định nghĩa schema cơ sở dữ liệu dạng văn bản, dùng trên [dbdiagram.io](https://dbdiagram.io/).

## Quy ước

- Mỗi thư mục con có thư mục images/ chứa ảnh render sẵn (`.png`, `.svg`) để nhúng vào tài liệu.
- Các file nguồn (.mmd, .puml, .wsd, .dbml) là nguồn có thể chỉnh sửa và render lại khi cần.
- Tên file theo pattern: `structurizr-<ViewName>.<ext>` đối với các sơ đồ sinh từ Structurizr.
