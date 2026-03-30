# ADR-010: Chiến lược giao dịch phân tán với Saga Pattern và Transactional Outbox Pattern

Date: 2026-03-03 - Accepted
Date: 2026-03-11 - Implemented

## Status

Implemented

## Context

Với quyết định sử dụng Database-per-Service, các giao dịch ACID truyền thống không thể vượt qua ranh giới của
một Microservice. Điều này dẫn đến 2 vấn đề cần giải quyết:

- Phạm vi Vĩ mô: Làm sao để đảm bảo tính nhất quán dữ liệu khi một luồng nghiệp vụ cần cập nhật dữ liệu
  trên nhiều service.
- Phạm vi Vi mô: Khi một service vừa lưu database cục bộ xong, vừa phải bắn sự kiện ra
  Message Broker, nếu mạng sập ngay khoảnh khắc bắn sự kiện, dữ liệu đã lưu nhưng sự kiện bị mất, gây sai lệch
  toàn hệ thống.

## Decision

Chúng tôi quyết định áp dụng kết hợp Saga Pattern và Transactional Outbox Pattern để giải quyết vấn đề
giao dịch phân tán:

1. Saga Pattern: Điều phối luồng nghiệp vụ chéo bằng chuỗi các sự kiện độc lập. Các service giao tiếp với nhau qua
   Broker. Nếu một bước thất bại, service bị lỗi sẽ phát ra sự kiện báo lỗi để kích hoạt các Giao dịch bù trừ nhằm
   rollback dữ liệu ở các service trước đó.
2. Transactional Outbox Pattern: Để chống mất sự kiện, mọi service khi phát sự kiện sẽ không bắn trực tiếp ra Broker.
   Thay vào đó sẽ lưu nội dung sự kiện vào một bảng `outbox_events` nằm cùng database với nghiệp vụ chính, sử dụng chung
   một database transaction. Một tiến trình chạy ngầm sẽ đọc bảng này và đẩy an toàn các sự kiện ra Message Broker.

## Consequences

**Tích cực:**

- Đảm bảo tính toàn vẹn: Dữ liệu không bao giờ bị lệch dẫu cho Message Broker hay mạng có bị sập
  tạm thời nhờ Transactional Outbox Pattern.
- Tính lỏng lẻo: Các service không gọi API đồng bộ nhau để cập nhật dữ liệu, giúp hệ thống không bị nghẽn cổ chai.
- Tính nhất quán: Hệ thống chấp nhận độ trễ nhỏ gọn trong đồng bộ, nhưng cam kết dữ liệu cuối cùng sẽ luôn chính xác.

**Tiêu cực:**

- Độ phức tạp khổng lồ: Phải thiết kế bảng Outbox, viết cronjob/worker để quét gửi event.
- Bắt buộc áp dụng Idempotent Consumer: Vì Outbox có thể vô tình gửi 1 event 2 lần, mọi service nhận sự kiện bắt buộc
  phải
  code theo chuẩn Idempotent để tránh trùng lặp dữ liệu.