# 11. Risks and Technical Debt

---

## 11.1. Risks

### 11.1.1. Security Risks

| ID   | Risk                                                                                      | Probability | Impact | Mitigation                                                                    |
|------|-------------------------------------------------------------------------------------------|-------------|--------|-------------------------------------------------------------------------------|
| R-01 | JWT dùng HS512 (symmetric key) — nếu `JWT_SECRET_KEY` bị lộ, attacker có thể ký token giả | Medium      | High   | Migrate sang RS256 (asymmetric). Key lưu trong K8s Secret (ADR-020)           |
| R-02 | Token blacklist lưu PostgreSQL — mỗi request cần query DB để check                        | Low         | Medium | Migrate blacklist sang Redis để giảm latency. Hiện tại chấp nhận vì scale nhỏ |
| R-03 | `.env` file bị commit nhầm vào git                                                        | Low         | High   | `.gitignore` + Gitleaks block merge trong CI/CD pipeline (ADR-031)            |

### 11.1.2. Architecture Risks

| ID   | Risk                                                                    | Probability | Impact | Mitigation                                                                       |
|------|-------------------------------------------------------------------------|-------------|--------|----------------------------------------------------------------------------------|
| R-04 | Notification Service down — email không được gửi                        | Medium      | Low    | RabbitMQ requeue + retry. Email là non-critical, không ảnh hưởng core flow       |
| R-05 | RabbitMQ down — toàn bộ async event bị block                            | Low         | High   | RabbitMQ Persistence mode + PVC trên K8s. Outbox pattern đảm bảo không mất event |
| R-06 | Neo4j thiếu kinh nghiệm trong team — query sai hoặc schema không tối ưu | High        | Medium | Giới hạn Neo4j chỉ cho Profile Service. Team tự nghiên cứu Cypher query cơ bản   |
| R-07 | Distributed tracing khó debug khi lỗi xảy ra cross-service              | High        | Medium | Log correlation ID xuyên suốt các service. Plan: tích hợp Zipkin sau v1.0.0      |

### 11.1.3. Operational Risks

| ID   | Risk                                                           | Probability | Impact | Mitigation                                                            |
|------|----------------------------------------------------------------|-------------|--------|-----------------------------------------------------------------------|
| R-08 | Cloudinary free tier hết quota (25GB storage / 25GB bandwidth) | Low         | Medium | Monitor usage. Fallback: migrate sang AWS S3 + CloudFront             |
| R-09 | Brevo free tier giới hạn 300 email/ngày                        | Medium      | Low    | Đủ dùng cho môi trường dev/demo. Production cần upgrade plan          |
| R-10 | Docker Compose không đủ để demo toàn bộ K8s features           | Low         | Low    | Demo local dùng Docker Compose. K8s manifest có sẵn để deploy khi cần |

---

## 11.2. Technical Debt

### 11.2.1. Security Debt

| ID    | Debt                                                                         | Mức độ    | Kế hoạch giải quyết                                 |
|-------|------------------------------------------------------------------------------|-----------|-----------------------------------------------------|
| TD-01 | JWT dùng HS512 thay RS256 — symmetric key kém an toàn hơn                    | 🔴 High   | Migrate sang RS256 sau khi hoàn thiện core features |
| TD-02 | Token blacklist lưu PostgreSQL thay Redis — query chậm hơn khi scale         | 🟡 Medium | Thêm Redis layer sau v1.0.0                         |
| TD-03 | Refresh Token Rotation chưa implement — token bị steal có thể dùng nhiều lần | 🟡 Medium | Implement rotation cùng lúc migrate Redis           |

### 11.2.2. Architecture Debt

| ID    | Debt                                                                                        | Mức độ    | Kế hoạch giải quyết                              |
|-------|---------------------------------------------------------------------------------------------|-----------|--------------------------------------------------|
| TD-04 | `task.position` dùng `DOUBLE` (Fractional Indexing) — precision drift sau nhiều lần kéo thả | 🟡 Medium | Migrate sang Lexorank (VARCHAR-based) sau v1.0.0 |
| TD-05 | Transactional Outbox chưa implement đầy đủ — thiếu outbox poller background job             | 🔴 High   | Implement poller trước khi deploy production     |
| TD-06 | Thiếu Circuit Breaker ở tầng Service-to-Service — khi một service chậm có thể cascade       | 🟡 Medium | Tích hợp Resilience4j (ADR-033)                  |
| TD-07 | ~~Comment Service và Notification Service chưa hoàn thiện implementation~~                   | ✅ Resolved | Đã hoàn thiện                                    |

### 11.2.3. Operational Debt

| ID    | Debt                                                                            | Mức độ    | Kế hoạch giải quyết                              |
|-------|---------------------------------------------------------------------------------|-----------|--------------------------------------------------|
| TD-08 | Chưa có centralized logging — mỗi service log riêng lẻ, khó trace cross-service | 🟡 Medium | Tích hợp ELK Stack hoặc Loki sau v1.0.0          |
| TD-09 | Chưa có distributed tracing — khó debug khi lỗi xảy ra cross-service            | 🟡 Medium | Tích hợp Zipkin + Micrometer Tracing sau v1.0.0  |
| TD-10 | ~~Unit test và Integration test chưa đầy đủ — coverage thấp~~                    | ✅ Resolved | Đã viết test, coverage đạt tiêu chuẩn            |
| TD-11 | API Gateway chưa có Rate Limiting thực tế — chỉ có trong ADR, chưa config       | 🟢 Low    | Config rate limit trước khi deploy production    |

### 11.2.4. Development Debt

| ID    | Debt                                                                      | Mức độ    | Kế hoạch giải quyết                                      |
|-------|---------------------------------------------------------------------------|-----------|----------------------------------------------------------|
| TD-12 | `ApiResponse.java` bị duplicate ở mỗi service thay vì dùng shared library | 🟢 Low    | Extract shared module nếu project scale lên              |
| TD-13 | Thiếu `@Valid` validation đầy đủ ở một số Controller                      | 🟡 Medium | Review và bổ sung trong quá trình viết test              |
| TD-14 | Docker image chưa được tối ưu size — dùng full JDK thay JRE               | 🟢 Low    | Migrate sang `eclipse-temurin:17-jre` để giảm image size |

---

## 11.3. Technical Debt Summary

```
🔴 High Priority (giải quyết trước khi nộp):
───────────────────────────────────────────────────────
TD-05  Outbox poller chưa implement
✅ TD-07  Comment Service + Notification Service — Đã hoàn thiện
✅ TD-10  Unit test + Integration test — Đã viết

🟡 Medium Priority (giải quyết sau v1.0.0):
───────────────────────────────────────────────────────
TD-02  Redis cho blacklist
TD-03  Refresh Token Rotation
TD-04  Lexorank thay Fractional Indexing
TD-06  Circuit Breaker (ADR-033)
TD-08  Centralized Logging
TD-09  Distributed Tracing
TD-13  @Valid validation

🟢 Low Priority (nice to have):
───────────────────────────────────────────────────────
TD-11  Rate Limiting config
TD-12  Shared library cho ApiResponse
TD-14  Docker image optimization
```

---

## 11.4. Điều team tự nhận xét

Nếu làm lại từ đầu, team sẽ:

1. **Bắt đầu với Monolith trước** — sau đó tách dần sang Microservices khi cần. Microservices ở giai đoạn đầu tạo ra
   overhead không cần thiết cho team nhỏ 3 người.

2. **Implement Redis ngay từ đầu** cho token blacklist thay vì dùng PostgreSQL rồi migrate sau.

3. **Viết test song song với code** thay vì để cuối cùng — Layered Architecture đã tạo điều kiện tốt cho unit test nhưng
   team chưa tận dụng được.

4. **Đơn giản hóa database strategy** — Neo4j có learning curve cao trong khi Profile Service chưa khai thác được graph
   features. PostgreSQL đủ dùng ở giai đoạn này.