# 10. Quality Requirements

## 10.1. Quality Tree

```
Quality
├── Security (Bảo mật)
│   ├── Authentication — JWT token validation tại Gateway
│   └── Authorization — RBAC phân quyền tại Service
│
├── Maintainability (Khả năng bảo trì)
│   ├── Modularity — 5 service độc lập, loose coupling
│   ├── Testability — Layered Architecture dễ mock/test
│   └── Readability — Naming convention thống nhất
│
├── Reliability (Độ tin cậy)
│   ├── Fault Isolation — 1 service down không kéo service khác
│   └── Eventual Consistency — Saga + Outbox đảm bảo data
│
├── Scalability (Khả năng mở rộng)
│   ├── Horizontal — mỗi service scale độc lập trên K8s
│   └── Data — Polyglot Persistence tối ưu theo từng service
│
└── Operability (Khả năng vận hành)
    ├── Deployability — Docker + K8s + CI/CD
    └── Configurability — Spring Profile + env var
```

---

## 10.2. Quality Scenarios

### 10.2.1. Security

| ID    | Stimulus                                          | Source    | Artifact          | Environment | Response                         | Measure                                      |
|-------|---------------------------------------------------|-----------|-------------------|-------------|----------------------------------|----------------------------------------------|
| QS-01 | Gửi request với token đã logout                   | Attacker  | API Gateway       | Production  | Gateway từ chối, trả 401         | 100% request có token blacklisted bị từ chối |
| QS-02 | Gửi request với token hợp lệ nhưng không đủ quyền | User      | Task Service      | Production  | Service trả 403 Forbidden        | 100% request thiếu permission bị từ chối     |
| QS-03 | Commit secret lên git                             | Developer | GitHub Repository | Development | CI/CD pipeline phát hiện và fail | 0 secret bị merge vào main branch            |

### 10.2.2. Maintainability

| ID    | Stimulus                            | Source    | Artifact        | Environment | Response                                                  | Measure                              |
|-------|-------------------------------------|-----------|-----------------|-------------|-----------------------------------------------------------|--------------------------------------|
| QS-04 | Thay đổi schema bảng `tasks`        | Developer | Task Service DB | Development | Chỉ Task Service bị ảnh hưởng, các service khác không đổi | 0 service ngoài Task Service cần sửa |
| QS-05 | Thêm field mới vào Comment document | Developer | Comment Service | Development | Chỉ thêm field vào MongoDB schema, không cần migration    | Thời gian thay đổi < 30 phút         |
| QS-06 | Developer mới join team             | Developer | Codebase        | Development | Clone 1 repo, copy `.env.example`, chạy docker-compose up | Hệ thống chạy được trong < 15 phút   |

### 10.2.3. Reliability

| ID    | Stimulus                                    | Source         | Artifact             | Environment | Response                                                 | Measure                                |
|-------|---------------------------------------------|----------------|----------------------|-------------|----------------------------------------------------------|----------------------------------------|
| QS-07 | Notification Service bị crash               | Infrastructure | Notification Service | Production  | Auth, Profile, Task, Comment vẫn hoạt động bình thường   | 0 downtime cho các service còn lại     |
| QS-08 | RabbitMQ publish thất bại sau khi DB commit | Infrastructure | Auth Service         | Production  | Outbox poller retry, event được deliver                  | 0 event bị mất, At-Least-Once delivery |
| QS-09 | Brevo API timeout khi gửi email             | External       | Notification Service | Production  | Email thất bại được retry, không ảnh hưởng luồng đăng ký | User đăng ký thành công dù email chậm  |

### 10.2.4. Scalability

| ID    | Stimulus                          | Source | Artifact        | Environment | Response                                                  | Measure                       |
|-------|-----------------------------------|--------|-----------------|-------------|-----------------------------------------------------------|-------------------------------|
| QS-10 | Số lượng task tăng đột biến       | User   | Task Service    | Production  | Scale replicas Task Service trên K8s                      | Scale hoàn tất trong < 2 phút |
| QS-11 | Nhiều user upload avatar cùng lúc | User   | Profile Service | Production  | Upload stream thẳng lên Cloudinary, server không bị nghẽn | Server bandwidth không tăng   |

### 10.2.5. Operability

| ID    | Stimulus                   | Source    | Artifact       | Environment | Response                                       | Measure                               |
|-------|----------------------------|-----------|----------------|-------------|------------------------------------------------|---------------------------------------|
| QS-12 | Push code lên main branch  | Developer | GitHub Actions | CI/CD       | Pipeline tự động build và test                 | Kết quả CI trong < 5 phút             |
| QS-13 | Chạy hệ thống trên máy mới | Developer | Docker Compose | Development | `docker-compose up` khởi động toàn bộ hệ thống | Tất cả service healthy trong < 3 phút |

---

## 10.3. Quality vs. Trade-off Summary

| Quality Goal    | Giải pháp áp dụng                           | Trade-off chấp nhận                            |
|-----------------|---------------------------------------------|------------------------------------------------|
| Security        | JWT + RBAC + Gateway centralized auth       | Blacklist query DB mỗi request → latency nhỏ   |
| Maintainability | Microservices + Layered + Naming convention | Nhiều service hơn Monolith → overhead vận hành |
| Reliability     | Saga + Outbox + Fault Isolation             | Eventual consistency thay ACID → phức tạp hơn  |
| Scalability     | Database-per-Service + K8s Deployment       | Không có cross-service ACID transaction        |
| Operability     | Docker + GitHub Actions + Spring Profile    | Cần hiểu nhiều công cụ hơn                     |