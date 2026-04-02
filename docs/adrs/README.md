# Architectural Decision Records (ADRs)

Thư mục này chứa toàn bộ Architecture Decision Records của dự án **Task Management System**.

## ADR Index

| ADR                                                             | Title                                               | Status                | Date       | Category       |
| --------------------------------------------------------------- | --------------------------------------------------- | --------------------- | ---------- | -------------- |
| [ADR-001](ADR-001-record-architecture-decisions.md)             | Ghi lại các quyết định về kiến trúc bằng ADR        | Implemented           | 2026-03-01 | Process        |
| [ADR-002](ADR-002-markdown-format.md)                           | Sử dụng định dạng Markdown cho ADR                  | Implemented           | 2026-03-01 | Process        |
| [ADR-003](ADR-003-use-iso-8601-format.md)                       | Sử dụng chuẩn thời gian ISO 8601                    | Implemented           | 2026-03-01 | Process        |
| [ADR-004](ADR-004-overall-architecture-client-server.md)        | Lựa chọn kiến trúc tổng thể là Client-Server        | Implemented           | 2026-03-01 | Architecture   |
| [ADR-005](ADR-005-server-architecture-microservices.md)         | Lựa chọn kiến trúc Microservices cho phía Server    | Implemented           | 2026-03-02 | Architecture   |
| [ADR-006](ADR-006-hybrid-architecture.md)                       | Áp dụng kiến trúc lai Layered và Event-Driven       | Implemented           | 2026-03-03 | Architecture   |
| [ADR-007](ADR-007-communication-architecture-style.md)          | Phong cách kiến trúc RESTful và giao tiếp Stateless | Implemented           | 2026-03-03 | Architecture   |
| [ADR-008](ADR-008-api-gateway-architecture-pattern.md)          | Lựa chọn mẫu kiến trúc API Gateway                  | Implemented           | 2026-03-03 | Architecture   |
| [ADR-009](ADR-009-database-per-service-architecture-pattern.md) | Lựa chọn kiến trúc Database-per-Service             | Implemented           | 2026-03-03 | Architecture   |
| [ADR-010](ADR-010-transactional-distributed-strategy.md)        | Chiến lược giao dịch phân tán                       | Implemented           | 2026-03-03 | Architecture   |
| [ADR-011](ADR-011-security-strategy.md)                         | Chiến lược bảo mật                                  | Implemented           | 2026-03-03 | Security       |
| [ADR-012](ADR-012-tech-stack-choice.md)                         | Lựa chọn Tech Stack ứng dụng                        | Implemented           | 2026-03-03 | Technology     |
| [ADR-013](ADR-013-database-technology-choice.md)                | Lựa chọn công nghệ Database                         | Implemented - Partial | 2026-03-03 | Technology     |
| [ADR-014](ADR-014-code-style-choice.md)                         | Lựa chọn phong cách phát triển Database-First       | Implemented           | 2026-03-03 | Process        |
| [ADR-015](ADR-015-api-response-structure.md)                    | Cấu trúc API Response thống nhất                    | Implemented           | 2026-03-03 | Architecture   |
| [ADR-016](ADR-016-error-handling-strategy.md)                   | Chiến lược xử lý lỗi                                | Implemented           | 2026-03-03 | Architecture   |
| [ADR-017](ADR-017-file-storage-strategy.md)                     | Chiến lược lưu trữ file bằng Cloudinary             | Implemented           | 2026-03-03 | Infrastructure |
| [ADR-018](ADR-018-repository-structure-monorepo.md)             | Lưu trữ mã nguồn trong 1 repository                 | Implemented           | 2026-03-04 | Process        |
| [ADR-019](ADR-019-naming-convention.md)                         | Phong cách đặt tên                                  | Implemented           | 2026-03-04 | Process        |
| [ADR-020](ADR-020-secret-storage-strategy.md)                   | Chiến lược lưu trữ Secret                           | Implemented           | 2026-03-03 | Security       |
| [ADR-021](ADR-021-environment-variable-configuration.md)        | Cấu hình biến môi trường                            | Implemented           | 2026-03-04 | DevOps         |
| [ADR-022](ADR-022-ci-github-action.md)                          | CI/CD pipeline với GitHub Actions                   | Implemented           | 2026-03-04 | DevOps         |
| [ADR-023](ADR-023-test-documation-strategy.md)                  | Chiến lược tài liệu hóa kiểm thử                    | Accepted              | 2026-03-15 | Process        |
| [ADR-024](ADR-024-fractional-indexing-technique.md)             | Kĩ thuật đánh chỉ mục phân số                       | Implemented           | 2026-03-15 | Architecture   |
| [ADR-025](ADR-025-jacoco-test-coverage.md)                      | Lựa chọn Jacoco đánh giá độ bao phủ test            | Implemented           | 2026-03-24 | DevOps         |
| [ADR-026](ADR-026-dependabot-dependencies-management.md)        | Quản lý dependency tự động bằng Dependabot          | Implemented           | 2026-03-21 | DevOps         |
| [ADR-027](ADR-027-cicd-detect-changes-strategy.md)              | Chiến lược phát hiện sự thay đổi theo service       | Implemented           | 2026-03-21 | DevOps         |
| [ADR-028](ADR-028-observability-strategy.md)                    | Chiến lực giám sát hệ thống                         | Accepted              | 2026-03-28 | Infrastructure |
| [ADR-029](ADR-029-kubernetes-migration-strategy.md)             | Chiến lược nâng cấp lên kubernetes                  | Proposed              | 2026-03-24 | Infrastructure |
| [ADR-030](ADR-030-api-versioning-strategy.md)                   | Chiến lược phiên bản hóa các API                    | Implemented           | 2026-03-10 | Architecture   |

## Status Summary

| Status          | Số lượng | Mô tả                                         |
| --------------- | -------- | --------------------------------------------- |
| **Implemented** | 27       | Đã quyết định và implement xong trong code    |
| **Accepted**    | 2        | Đã quyết định, đang trong quá trình implement |
| **Proposed**    | 1        | Đang thảo luận, chưa quyết định               |
| **Deprecated**  | 0        | Đã bị thay thế                                |

> **Last Review**: 2026-04-02
> **Next Review**: 2026-05-02

---

## ADR Categories

### Process

- **ADR-001**: Ghi lại các quyết định về kiến trúc bằng ADR
- **ADR-002**: Sử dụng định dạng Markdown cho ADR
- **ADR-003**: Sử dụng chuẩn thời gian ISO 8601
- **ADR-014**: Lựa chọn phong cách phát triển Database-First
- **ADR-018**: Lưu trữ mã nguồn trong 1 repository (Monorepo)
- **ADR-019**: Phong cách đặt tên
- **ADR-023**: Chiến lược tài liệu hóa kiểm thử

### Architecture

- **ADR-004**: Lựa chọn kiến trúc tổng thể là Client-Server
- **ADR-005**: Lựa chọn kiến trúc Microservices cho phía Server
- **ADR-006**: Áp dụng kiến trúc lai Layered và Event-Driven
- **ADR-007**: Phong cách kiến trúc RESTful và giao tiếp Stateless
- **ADR-008**: Lựa chọn mẫu kiến trúc API Gateway
- **ADR-009**: Lựa chọn kiến trúc Database-per-Service
- **ADR-010**: Chiến lược giao dịch phân tán (Saga + Transactional Outbox)
- **ADR-015**: Cấu trúc API Response thống nhất
- **ADR-016**: Chiến lược xử lý lỗi (mã định danh 6 chữ số)
- **ADR-024**: Kĩ thuật đánh chỉ mục phân số
- **ADR-030**: Chiến lược phiên bản hóa các API

### Security

- **ADR-011**: Chiến lược bảo mật (JWT HS512, 1 Token - 2 Vai trò)
- **ADR-020**: Chiến lược lưu trữ Secret

### Technology

- **ADR-012**: Lựa chọn Tech Stack ứng dụng
- **ADR-013**: Lựa chọn công nghệ Database (Polyglot Persistence)

### Infrastructure

- **ADR-017**: Chiến lược lưu trữ file bằng Cloudinary
- **ADR-028**: Chiến lực giám sát hệ thống
- **ADR-029**: Chiến lược nâng cấp lên kubernetes

### DevOps

- **ADR-021**: Cấu hình biến môi trường
- **ADR-022**: CI/CD pipeline với GitHub Actions
- **ADR-025**: Lựa chọn Jacoco đánh giá độ bao phủ test
- **ADR-026**: Quản lý dependency tự động bằng dependabot
- **ADR-027**: Chiến lược phát hiện sự thay đổi theo service

---

## ADR Relationships

Sơ đồ phụ thuộc giữa các ADR:

```
FOUNDATION
───────────────────────────────────────────────────────────────
ADR-001 (Dùng ADR)
ADR-002 (Markdown)
ADR-003 (ISO 8601)

ARCHITECTURE
───────────────────────────────────────────────────────────────
ADR-004 (Client-Server)
  └──► ADR-005 (Microservices)
         ├──► ADR-006 (Layered + Event-Driven)
         │      │──► ADR-015 (API Response)
         │      │        └──► ADR-016 (Error Handling)
         │      └───────────► ADR-030 (API Versioning)
         ├──► ADR-007 (RESTful + Stateless)
         ├──► ADR-008 (API Gateway)
         │      └──► ADR-011 (Security / JWT)
         ├──► ADR-009 (Database-per-Service)
         │      ├──► ADR-010 (Saga + Outbox)
         │      └──► ADR-013 (Polyglot Persistence)
         └──► ADR-017 (File Storage / Cloudinary)

CROSS-CUTTING
───────────────────────────────────────────────────────────────
ADR-012 (Tech Stack)  ──► ADR-013 (DB Selection)      
ADR-014 (Database-First)
ADR-015 (API Response field names)
ADR-019 (Naming)

OPERATIONAL
───────────────────────────────────────────────────────────────
ADR-018 (Monorepo) ──► ADR-021 (Env Config) ──► ADR-022 (CI/CD)
ADR-020 (Secret) ─────────────────────────────► ADR-022 (CI/CD)
ADR-025 (Test Coverage - Jacoco)
ADR-026 (Dependabot)
ADR-005 (Microservices) ──► ADR-027 (Change Detection per Service)
ADR-028 (Monitoring)
ADR-029 (Kubernetes Migration)
```

---

## Cross-Reference Matrix

| ADR     | Depends On       | Influences                         | Relationship                                     |
| ------- | ---------------- | ---------------------------------- | ------------------------------------------------ |
| ADR-004 | —                | ADR-005                            | Enables microservices decision                   |
| ADR-005 | ADR-004          | ADR-006, ADR-007, ADR-008, ADR-009 | Requires communication + data strategy           |
| ADR-006 | ADR-005          | ADR-016                            | Layered design guides error handling             |
| ADR-007 | ADR-005          | ADR-022                            | REST/RabbitMQ influences CI/CD setup             |
| ADR-008 | ADR-005          | ADR-011                            | API Gateway requires security strategy           |
| ADR-009 | ADR-005          | ADR-010, ADR-013                   | DB-per-service spawns transaction + DB selection |
| ADR-010 | ADR-009          | —                                  | Saga + Outbox resolves distributed tx            |
| ADR-011 | ADR-008          | ADR-020                            | Security strategy requires secret storage        |
| ADR-012 | —                | ADR-013                            | Tech stack spawns DB technology choice           |
| ADR-013 | ADR-009, ADR-012 | —                                  | Final DB selection                               |
| ADR-014 | —                | —                                  | Database-First coding style                      |
| ADR-015 | ADR-016          | —                                  | Response wraps error code                        |
| ADR-016 | ADR-006          | ADR-015                            | Error code details response structure            |
| ADR-017 | ADR-005          | —                                  | File storage for Profile Service                 |
| ADR-018 | —                | ADR-021                            | Monorepo influences env config                   |
| ADR-019 | —                | ADR-015                            | Naming guides response field names               |
| ADR-020 | ADR-011          | ADR-022                            | Secret storage required by CI/CD                 |
| ADR-021 | ADR-018          | ADR-022                            | Env config required by CI/CD                     |
| ADR-022 | ADR-020, ADR-021 | —                                  | CI/CD depends on secrets + env config            |
| ADR-027 | ADR-018          | —                                  | Change detection per service depends on Monorepo |
| ADR-028 | ADR-005          | ADR-029                            | Monitoring strategy applies across microservices |
| ADR-029 | ADR-028          | —                                  | Kubernetes migration requires observability      |
| ADR-030 | ADR-007          | —                                  | API versioning extends RESTful style             |
---

## ADR Template

```markdown
# ADR-XXX: [Title]

Date: YYYY-MM-DD

## Status

[Proposed | Accepted | Implemented | Deprecated | Superseded]

## Context

[Description of the problem and context]

## Decision

[Description of the architectural decision]

## Consequences

**Tích cực:**

- [Positive consequence 1]

**Tiêu cực:**

- [Negative consequence 1]
```

---

## Contributing

Khi thêm ADR mới:

1. Sử dụng số ADR tiếp theo (`ADR-023`, `ADR-024`, ...)
2. Đặt tên file: `ADR-XXX-kebab-case-descriptive-name.md`
3. Dùng template NYGARD ở trên
4. Cập nhật bảng **ADR Index** và **Status Summary** trong file này
5. Cập nhật **Cross-Reference Matrix** nếu ADR mới phụ thuộc hoặc ảnh hưởng đến ADR hiện có

## References

- [Architectural Decision Records](https://adr.github.io/)
- [NYGARD ADR Template](https://github.com/joelparkerhenderson/architecture-decision-record/blob/main/templates/decision-record-template-by-michael-nygard/index.md)
- [joelparkerhenderson ADR examples](https://github.com/joelparkerhenderson/architecture-decision-record)
