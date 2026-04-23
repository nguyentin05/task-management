# 9. Architecture Decisions

Toàn bộ quyết định kiến trúc của hệ thống được ghi lại theo chuẩn ADR và lưu trữ tại`docs/adrs/`. Section này tổng hợp
các quyết định quan trọng nhất theo nhóm.

---

## 9.1. Quyết định kiến trúc tổng thể

| ADR                                                                     | Quyết định                          | Lý do cốt lõi                                              |
|-------------------------------------------------------------------------|-------------------------------------|------------------------------------------------------------|
| [ADR-004](../adrs/ADR-004-overall-architecture-client-server.md)        | Client-Server là kiến trúc tổng thể | Tách biệt Frontend và Backend, hỗ trợ nhiều client         |
| [ADR-005](../adrs/ADR-005-server-architecture-microservices.md)         | Microservices cho Server Tier       | Scale độc lập, polyglot persistence, học kiến trúc thực tế |
| [ADR-006](../adrs/ADR-006-hybrid-architecture.md)                       | Layered + Event-Driven hybrid       | Tách biệt concerns trong service, async giữa các service   |
| [ADR-007](../adrs/ADR-007-communication-architecture-style.md)          | RESTful + Stateless                 | Chuẩn giao tiếp phổ biến, dễ scale horizontal              |
| [ADR-008](../adrs/ADR-008-api-gateway-architecture-pattern.md)          | API Gateway pattern                 | Single entry point, centralized authentication             |
| [ADR-009](../adrs/ADR-009-database-per-service-architecture-pattern.md) | Database-per-Service                | Loose coupling, mỗi service tự chủ về data                 |
| [ADR-010](../adrs/ADR-010-transactional-distributed-strategy.md)        | Saga + Transactional Outbox         | Eventual consistency thay ACID cross-service               |

---

## 9.2. Quyết định bảo mật

| ADR                                                   | Quyết định                           | Lý do cốt lõi                                  |
|-------------------------------------------------------|--------------------------------------|------------------------------------------------|
| [ADR-011](../adrs/ADR-011-security-strategy.md)       | JWT HS512 + RBAC                     | Stateless authentication, phân quyền theo role |
| [ADR-020](../adrs/ADR-020-secret-storage-strategy.md) | `.env` local + K8s Secret production | Không bao giờ commit secret vào git            |
| [ADR-031](../adrs/ADR-031-gitleaks-sonarcloud-code-quality-check.md) | Gitleaks + SonarCloud | Kiểm tra chất lượng mã nguồn và bảo mật tự động |

---

## 9.3. Quyết định công nghệ

| ADR                                                      | Quyết định                              | Lý do cốt lõi                                        |
|----------------------------------------------------------|-----------------------------------------|------------------------------------------------------|
| [ADR-012](../adrs/ADR-012-tech-stack-choice.md)          | Java/Spring Boot + React + Docker + K8s | Ecosystem mạnh, phù hợp Microservices                |
| [ADR-013](../adrs/ADR-013-database-technology-choice.md) | PostgreSQL + Neo4j + MongoDB            | Polyglot — mỗi DB phù hợp đặc thù từng service       |
| [ADR-017](../adrs/ADR-017-file-storage-strategy.md)      | Cloudinary cho file storage             | CDN sẵn có, không tốn disk server, free tier đủ dùng |

---

## 9.4. Quyết định vận hành

| ADR                                                              | Quyết định                   | Lý do cốt lõi                                         |
|------------------------------------------------------------------|------------------------------|-------------------------------------------------------|
| [ADR-015](../adrs/ADR-015-api-response-structure.md)             | Unified JSON wrapper         | Frontend xử lý response bằng một interceptor duy nhất |
| [ADR-032](../adrs/ADR-032-centralized-error-handling.md)         | Centralized Error Handling   | Thay thế ADR-016, xử lý lỗi tập trung qua GlobalExceptionHandler |
| [ADR-018](../adrs/ADR-018-repository-structure-monorepo.md)      | Monorepo                     | Atomic commit, chia sẻ tài liệu, phù hợp team nhỏ     |
| [ADR-019](../adrs/ADR-019-naming-convention.md)                  | Naming convention thống nhất | Nhất quán giữa API, DB, code, message broker          |
| [ADR-021](../adrs/ADR-021-environment-variable-configuration.md) | Spring Profile + env var     | Tách config theo môi trường, không hardcode           |
| [ADR-022](../adrs/ADR-022-ci-github-action.md)                   | GitHub Actions CI/CD         | Tự động hóa build, test, deploy                       |

---

## 9.5. Quyết định quy trình

| ADR                                                         | Quyết định                  | Lý do cốt lõi                                            |
|-------------------------------------------------------------|-----------------------------|----------------------------------------------------------|
| [ADR-001](../adrs/ADR-001-record-architecture-decisions.md) | Dùng ADR ghi lại quyết định | Tạo lịch sử quyết định, dễ onboard thành viên mới        |
| [ADR-002](../adrs/ADR-002-markdown-format.md)               | Markdown cho ADR            | Version control thân thiện, render trực tiếp trên GitHub |
| [ADR-003](../adrs/ADR-003-use-iso-8601-format.md)           | ISO 8601 cho datetime       | Chuẩn quốc tế, không nhập nhằng timezone                 |
| [ADR-014](../adrs/ADR-014-code-style-choice.md)             | Database-First development  | Schema là source of truth, tránh ORM mismatch            |

---

## 9.6. Tóm tắt Trade-offs quan trọng

Các quyết định có trade-off lớn nhất, cần hiểu rõ khi vấn đáp:

| Quyết định                  | Lợi ích                          | Đánh đổi                                   |
|-----------------------------|----------------------------------|--------------------------------------------|
| Microservices thay Monolith | Scale độc lập, fault isolation   | Phức tạp hơn, cần RabbitMQ, Saga           |
| Database-per-Service        | Loose coupling, polyglot         | Không có ACID cross-service                |
| JWT Stateless               | Không cần session storage        | Blacklist cần query DB mỗi request         |
| Choreography Saga           | Không có single point of failure | Khó debug luồng phân tán                   |
| Neo4j cho Profile           | Graph queries nhanh              | Learning curve, ít tài liệu hơn PostgreSQL |
| Cloudinary cho file         | CDN sẵn, không tốn disk          | Phụ thuộc third-party, chi phí scale       |

---

> 📄 Xem toàn bộ 33 ADR tại: [`docs/adrs/README.md`](../adrs/README.md)