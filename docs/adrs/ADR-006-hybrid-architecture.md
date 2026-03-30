# ADR-006: Áp dụng kiến trúc lai Layered và Event-Driven để bổ trợ Microservices

Date: 2026-03-03 - Accepted
Date: 2026-03-08 - Implemented

## Status

Implemented

## Context

Chúng tôi đã quyết định chọn Microservices làm kiến trúc chính cho phía Server. Tuy nhiên,
trong thực tế triển khai, để tối ưu hóa và tránh tạo ra một hệ thống chắp vá "quái vật Frankenstein", hệ thống cần các
kiến trúc khác bổ trợ để giải quyết các vấn đề như:

- Tổ chức mã nguồn bên trong mỗi service như thế nào để dễ bảo trì, dễ test và tuân thủ nguyên lý Separation of
  Concerns.
- Xử lý các nghiệp vụ bất đồng bộ như thế nào để các service không bị phụ thuộc cứng vào nhau và không làm chậm hệ
  thống.

## Decision

Chúng tôi quyết định áp dụng kiến trúc lai kết hợp 2 phong cách kiến trúc phụ trợ sau vào nền tảng Microservices:

1. **Layered Architecture:** Mỗi Microservice sẽ được tổ chức code theo 3 tầng chuẩn: `Controller` Tiếp nhận
   request -> `Service` Xử lý Business Logic -> `Repository` Giao tiếp Database.
2. **Event-Driven Architecture:** Áp dụng nguyên tắc thiết kế `Fire and Forget Pattern` qua Message Broker cho các
   nghiệp vụ bất đồng bộ. Service gọi chỉ cần ném sự kiện qua Message Broker, các Service nghe sẽ tự động bắt lấy để xử
   lý mà không cần giao tiếp trực tiếp.

## Consequences

**Tích cực:**

- Kiểm soát độ phức tạp: Tầng kiến trúc nào làm đúng việc của tầng đó. Layered giúp code bên trong gọn gàng,
  Event-Driven giúp giao tiếp bên ngoài lỏng lẻo.
- Tăng độ chịu lỗi và hiệu năng: Nhờ Event-Driven, nếu service phụ trợ bị sập, luồng chính của
  user vẫn hoàn tất tức thì, sự kiện được lưu ở Message Broker và xử lý bù sau.

**Tiêu cực:**

- Tăng khối lượng code: Việc áp dụng Layered đòi hỏi phải tạo nhiều file/class ngay cả với những API CRUD đơn giản nhất.
- Độ phức tạp của Eventual Consistency: Do dùng Event-Driven, dữ liệu giữa các service sẽ có
  độ trễ đồng bộ nhất định, đòi hỏi phía Frontend phải xử lý UX khéo léo để không gây bối rối cho người dùng.
- Khó khăn khi gỡ lỗi chéo: Việc trace log một luồng nghiệp vụ đi xuyên qua nhiều tầng khác nhau là một thách thức lớn.