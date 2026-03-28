# Architecture Documentation

Tài liệu kiến trúc hệ thống **Task Management** theo hai chuẩn: **arc42** và **C4 Model**.

## Cấu trúc

```
architecture/
├── arc42/      # Tài liệu kiến trúc theo template arc42
└── c4/         # Sơ đồ kiến trúc theo C4 Model
```

## arc42

[arc42](./arc42/README.md) là template tài liệu kiến trúc phần mềm gồm 12 chương tiêu chuẩn:

| Chương                                                                | Nội dung                         |
|-----------------------------------------------------------------------|----------------------------------|
| [01 Introduction and Goals](./arc42/01_introduction_and_goals.md)     | Mục tiêu và yêu cầu chất lượng   |
| [02 Architecture Constraints](./arc42/02_architecture_constraints.md) | Ràng buộc kiến trúc              |
| [03 System Scope and Context](./arc42/03_system_scope_and_context.md) | Phạm vi và ngữ cảnh hệ thống     |
| [04 Solution Strategy](./arc42/04_solution_strategy.md)               | Chiến lược giải pháp             |
| [05 Building Block View](./arc42/05_building_block_view.md)           | Cấu trúc thành phần              |
| [06 Runtime View](./arc42/06_runtime_view.md)                         | Luồng thực thi runtime           |
| [07 Deployment View](./arc42/07_deployment_view.md)                   | Cấu hình triển khai              |
| [08 Concepts](./arc42/08_concepts.md)                                 | Các khái niệm xuyên suốt         |
| [09 Architecture Decisions](./arc42/09_architecture_decisions.md)     | Tóm tắt các quyết định kiến trúc |
| [10 Quality Requirements](./arc42/10_quality_requirements.md)         | Yêu cầu chất lượng               |
| [11 Risks and Technical Debt](./arc42/11_risks_and_technical_debt.md) | Rủi ro và nợ kỹ thuật            |
| [12 Glossary](./arc42/12_glossary.md)                                 | Bảng thuật ngữ                   |

## C4 Model

[C4 Model](./c4/README.md) mô tả kiến trúc theo 3 cấp độ:

- **Context** — hệ thống trong môi trường tổng thể
- **Container** — các service và thành phần chính
- **Component** — chi tiết bên trong từng service

Xem thêm tại [ADR-005](../adrs/ADR-005-server-architecture-microservices.md)
và [ADR-006](../adrs/ADR-006-hybrid-architecture.md).