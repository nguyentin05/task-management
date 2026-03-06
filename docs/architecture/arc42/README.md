# Arc42 — Software Architecture Documentation

**Hệ thống:** Task Management
**Phiên bản:** 1.0.0
**Ngày cập nhật:** 2026-03-06
**Nhóm:** TTT — ITEC2313 Kiến trúc Phần mềm

---

## Giới thiệu

Tài liệu này mô tả kiến trúc phần mềm của hệ thống **Task Management** theo chuẩn [Arc42](https://arc42.org/overview) —
một template tài liệu hóa kiến trúc phần mềm được sử dụng rộng rãi trong ngành công nghiệp.

Hệ thống được xây dựng theo kiến trúc **Microservices** gồm 5 service độc lập, giao tiếp qua REST API đồng bộ và
RabbitMQ bất đồng bộ.

---

## Cấu trúc tài liệu

```
docs/architecture/arc42/
├── index.md                          
├── 01_introduction_and_goals.md
├── 02_architecture_constraints.md
├── 03_system_scope_and_context.md
├── 04_solution_strategy.md
├── 05_building_block_view.md
├── 06_runtime_view.md
├── 07_deployment_view.md
├── 08_concepts.md
├── 09_architecture_decisions.md
├── 10_quality_requirements.md
├── 11_risks_and_technical_debt.md
├── 12_glossary.md
└── images/
    ├── business-context.png
    ├── technical-context.png
    ├── building-block-view-level-1.png
    ├── building-block-view-level-2-authentication-service.png
    ├── building-block-view-level-2-profile-service.png
    ├── building-block-view-level-2-task-service.png
    ├── building-block-view-level-2-comment-service.png
    └── building-block-view-level-2-notification-service.png
```

---

## Mục lục

| #                                    | Section                      | Tóm tắt                                                           |
|--------------------------------------|------------------------------|-------------------------------------------------------------------|
| [1](01_introduction_and_goals.md)    | **Introduction and Goals**   | Mục tiêu hệ thống, chức năng chính, quality goals và stakeholders |
| [2](02_architecture_constraints.md)  | **Architecture Constraints** | Ràng buộc kỹ thuật, tổ chức và quy ước phát triển                 |
| [3](03_system_scope_and_context.md)  | **System Scope and Context** | Business context, technical context và external systems           |
| [4](04_solution_strategy.md)         | **Solution Strategy**        | Quyết định công nghệ và mapping với quality goals                 |
| [5](05_building_block_view.md)       | **Building Block View**      | Phân rã hệ thống thành các building block, blackbox và whitebox   |
| [6](06_runtime_view.md)              | **Runtime View**             | Hành vi động qua 5 kịch bản quan trọng                            |
| [7](07_deployment_view.md)           | **Deployment View**          | Triển khai Docker Compose và Kubernetes                           |
| [8](08_concepts.md)                  | **Cross-Cutting Concepts**   | Security, Error Handling, Event-Driven, Naming Convention...      |
| [9](09_architecture_decisions.md)    | **Architecture Decisions**   | Tổng hợp 22 ADR theo nhóm và trade-off quan trọng                 |
| [10](10_quality_requirements.md)     | **Quality Requirements**     | Quality tree và quality scenarios                                 |
| [11](11_risks_and_technical_debt.md) | **Risks and Technical Debt** | Rủi ro và nợ kỹ thuật cần giải quyết                              |
| [12](12_glossary.md)                 | **Glossary**                 | Định nghĩa thuật ngữ và viết tắt                                  |

---

## Kiến trúc tổng quan

```
                    ┌─────────────┐
                    │  React SPA  │
                    │  (Browser)  │
                    └──────┬──────┘
                           │ HTTPS
                    ┌──────▼──────┐
                    │   Gateway   │        External Systems
                    │ Spring Cloud│ ──────────────────────────
                    │  Gateway    │        ┌──────────────┐
                    └──────┬──────┘        │  Cloudinary  │
                           │               │  (CDN/Media) │
         ┌─────────────────┼─────────────┐ └──────────────┘
         │                 │             │
   ┌─────▼──────┐  ┌───────▼──────┐  ┌──▼──────────┐
   │    Auth    │  │   Profile    │  │    Task     │
   │  Service   │  │   Service    │  │   Service   │
   │  :8081     │  │   :8082      │  │   :8083     │
   └─────┬──────┘  └───────┬──────┘  └──┬──────────┘
         │                 │            │
   ┌─────▼──────┐  ┌───────▼──────┐  ┌──▼──────────┐     ┌──────────────┐
   │ PostgreSQL │  │    Neo4j     │  │ PostgreSQL  │     │    Brevo     │
   │ (auth_db)  │  │ (profile_db) │  │ (task_db)   │     │    (SMTP)    │
   └────────────┘  └──────────────┘  └─────────────┘     └──────┬───────┘
                                                                │
         ┌─────────────┐  ┌──────────────────────────────────┐  │
         │   Comment   │  │       Notification Service       │◄─┘
         │   Service   │  │           :8085                  │
         │   :8084     │  └──────────────────────────────────┘
         └──────┬──────┘                  ▲
                │                         │
         ┌──────▼──────┐        ┌─────────┴────────┐
         │   MongoDB   │        │     RabbitMQ     │
         │(comment_db) │        │ (Message Broker) │
         └─────────────┘        └──────────────────┘
```

---

## ADR liên quan

Tài liệu Arc42 này được xây dựng dựa trên 22 Architecture Decision Records. Xem toàn bộ tại [
`../adrs/README.md`](../adrs/README.md).

Các ADR quan trọng nhất:

| ADR                                                                     | Quyết định           | Section liên quan |
|-------------------------------------------------------------------------|----------------------|-------------------|
| [ADR-005](../adrs/ADR-005-server-architecture-microservices.md)         | Microservices        | 4, 5, 7           |
| [ADR-008](../adrs/ADR-008-api-gateway-architecture-pattern.md)          | API Gateway          | 5.1.6, 8.1        |
| [ADR-009](../adrs/ADR-009-database-per-service-architecture-pattern.md) | Database-per-Service | 5, 8.5            |
| [ADR-010](../adrs/ADR-010-transactional-distributed-strategy.md)        | Saga + Outbox        | 6.1, 8.8          |
| [ADR-011](../adrs/ADR-011-security-strategy.md)                         | JWT + RBAC           | 6.3, 8.1          |
| [ADR-016](../adrs/ADR-016-error-handling-strategy.md)                   | Error Code           | 8.2               |

---

## Quick Reference

```
Câu hỏi vấn đáp                      Xem tại
──────────────────────────────────────────────────────────
Tại sao chọn Microservices?           §4.1, ADR-005
Tại sao Neo4j cho Profile Service?    §8.5, ADR-013
Security hoạt động thế nào?           §8.1, ADR-011
AuthN vs AuthZ khác gì?               §8.1
Luồng đăng ký tài khoản?              §6.1
Kéo thả Task hoạt động thế nào?       §6.4
Deploy lên K8s như thế nào?           §7.2
RabbitMQ dùng cho use case gì?        §8.4, §6.1
Lỗi được xử lý thế nào?               §8.2, ADR-016
Trade-off lớn nhất là gì?             §9.6, §11.4
Technical debt còn gì?                §11.2
```

---

*Viết theo chuẩn [Arc42](https://arc42.org/overview) Template v8. Tham khảo: [arc42.org](https://arc42.org)