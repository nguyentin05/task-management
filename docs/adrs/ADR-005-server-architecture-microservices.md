# ADR-005: Lựa chọn kiến trúc Microservices cho phía Server

Date: 2026-03-02

## Status

Accepted

## Context

Sau khi xác định Client-Server là kiến trúc tổng thể, chúng ta cần quyết
định cách tổ chức bên phía Server. Hệ thống Task Management bao gồm nhiều nhóm
chức năng có tính độc lập cao:

- Xác thực và phân quyền người dùng
- Quản lý hồ sơ cá nhân
- Quản lý công việc
- Bình luận
- Thông báo

Các phương án được cân nhắc:

**Phương án 1 — Monolithic Architecture**
Toàn bộ server được đóng gói trong một ứng dụng duy nhất, dùng chung
một database và deploy như một unit.

**Phương án 2 — Microservices Architecture**
Server được tách thành các service độc lập theo từng bounded context, mỗi service
có database riêng, vòng đời deploy riêng, giao tiếp với nhau.

**Phương án 3 — Modular Monolith**
Một ứng dụng duy nhất nhưng được tổ chức thành các module độc lập với ranh giới
rõ ràng, có thể tách thành microservices sau này nếu cần.

Các yêu cầu cần thỏa mãn:

- Mỗi nhóm chức năng có thể phát triển và deploy độc lập.
- Lỗi ở một chức năng không được kéo sập toàn bộ hệ thống.
- Linh hoạt lựa chọn công nghệ và database phù hợp cho từng chức năng.
- Dễ dàng mở rộng thêm chức năng mới mà không ảnh hưởng đến phần hiện có.

## Decision

Chúng tôi quyết định áp dụng kiến trúc Microservices cho phía Server. Hệ thống sẽ được phân rã thành các dịch vụ độc lập
theo miền nghiệp vụ DDD.

```
┌────────────────────────────────────────────────────────────────────────────────┐
│                                     SERVER                                     │
│                                                                                │
│  ┌──────────────────────────┐    ┌───────────────────┐    ┌────────────────┐   │                
│  │  Authentication Service  │    │  Profile Service  │    │  Task Service  │   │
│  │                          │    │                   │    │                │   │
│  │ - Quản lý Token          │    │ - Hồ sơ cá nhân   │    │ - Workspace    │   │
│  │ - Role/Permission        │    │ - Ảnh đại diện    │    │ - Project      │   │
│  │ - Đăng ký / Đăng nhập    │    │                   │    │ - Column       │   │
│  │                          │    └───────────────────┘    │ - Task         │   │
│  └──────────────────────────┘                             │                │   │      
│                                                           └────────────────┘   │
│                                                                                │  
│   ┌────────────────────────┐    ┌───────────────────┐                          │
│   │  Notification Service  │    │  Comment Service  │                          │
│   │                        │    │                   │                          │
│   │  - Gửi Email           │    │ - Bình luận       │                          │
│   │                        │    │ - Nested reply    │                          │
│   └────────────────────────┘    │                   │                          │
│                                 └───────────────────┘                          │
│                                                                                │
└────────────────────────────────────────────────────────────────────────────────┘

```

## Consequences

**Tích cực:**

- Mỗi service có thể được phát triển, test và deploy theo chu kỳ riêng mà không ảnh hưởng đến các service khác.
- Lỗi được cô lập trong từng service, tránh hiện tượng cascading failure trên toàn hệ thống.
- Dễ dàng thêm service mới mà không cần chỉnh sửa các service hiện có.

**Tiêu cực:**

- Tăng độ phức tạp vận hành: cần quản lý 5 service với các vòng đời độc lập.
- Giao tiếp qua mạng giữa các service có độ trễ cao hơn so với gọi hàm nội bộ trong Monolith
- Phải xử lý transactions phức tạp và tính nhất quán về dữ liệu giữa các service.
- Phải giải quyết bài toán truy vết lỗi giữa các service chi tiết để dễ bảo trì.