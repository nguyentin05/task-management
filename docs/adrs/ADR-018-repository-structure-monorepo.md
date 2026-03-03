# ADR-018: Lưu trữ mã nguồn trong 1 repository

Date: 2026-03-04

## Status

Accepted

## Context

Sau khi quyết định kiến trúc Microservices với 5 service độc lập, cần quyết định cách tổ chức mã nguồn trên hệ thống
version control. Có hai hướng chính được cân nhắc:

**Phương án 1 — Multirepo**
Mỗi service có một repository riêng biệt trên Git.

```
github.com/team/auth-service        (repo 1)
github.com/team/profile-service     (repo 2)
github.com/team/task-service        (repo 3)
github.com/team/comment-service     (repo 4)
github.com/team/notification-service(repo 5)
github.com/team/api-gateway         (repo 6)
github.com/team/frontend            (repo 7)
```

**Phương án 2 — Monorepo**
Toàn bộ codebase nằm trong một repository duy nhất.

```
github.com/team/task-management/
├── backend/
│   ├── api-gateway/
│   ├── authentication-service/
│   ├── profile-service/
│   ├── task-service/
│   ├── comment-service/
│   └── notification-service/
├── frontend/
├── database/
├── docs/
└── docker-compose.yml
```

Các yêu cầu cần thỏa mãn:

- Dễ dàng thay đổi API contract giữa Frontend và Backend trong cùng một PR.
- Chia sẻ tài liệu ADR, script, và config chung giữa các service.
- Đơn giản hóa CI/CD và quy trình review code trong nhóm nhỏ.

## Decision

Chúng tôi quyết định sử dụng Monorepo — toàn bộ mã nguồn của hệ thống Task Management được lưu trữ trong một repository
duy nhất.

Cấu trúc thư mục:

```
task-management/
│
├── backend/                            ← Toàn bộ backend services
│   ├── api-gateway/                    ← Spring Cloud Gateway
│   ├── authentication-service/         ← Spring Boot (Auth, User, Role, Permission)
│   ├── profile-service/                ← Spring Boot (Profile, Avatar)
│   └── task-service/                   ← Spring Boot (Workspace, Project, Column, Task)
├── frontend/                           ← React + Vite
├── database/                           ← Database migration scripts
│   └── migrations/
├── docs/                               ← Tài liệu kỹ thuật
│   ├── adrs/                           ← 22 Architecture Decision Records
│   ├── api/                            ← API Documentation
│   └── architecture/
│       ├── arc42/                      ← Arc42 documentation (12 sections)
│       └── c4/                         ← C4 diagrams (Context, Container, Component)
├── weekly-reports/                     ← Báo cáo tiến độ hàng tuần
├── docker-compose.yml                  ← Chạy toàn bộ hệ thống local
└── README.md                           ← Hướng dẫn cài đặt và chạy
```

## Consequences

**Tích cực:**

- Thay đổi liên quan đến nhiều service được commit trong một PR duy nhất, dễ review và rollback.
- Chia sẻ tài liệu tập trung: ADRs, kiến trúc C4, Arc42, migration scripts đều nằm ở một nơi, không bị phân tán.
- CI/CD đơn giản hơn với một pipeline duy nhất có thể detect service nào thay đổi và chỉ build/deploy service đó.
- Onboarding nhanh hơn: Developer mới clone một repo là có toàn bộ hệ thống, chạy `docker-compose up` là xong.
- Nhất quán về cấu hình: `docker-compose.yml` và biến môi trường được quản lý tập trung.

**Tiêu cực:**

- Repository ngày càng lớn — Khi hệ thống mở rộng, số file và history tăng nhanh, có thể ảnh hưởng tốc độ clone và log.
- Quyền truy cập không thể giới hạn developer chỉ đọc/ghi một service cụ thể mà không dùng thêm công cụ.
- CI/CD cần cấu hình thêm cần cấu hình path filtering để tránh build lại toàn bộ khi chỉ thay đổi tài liệu.