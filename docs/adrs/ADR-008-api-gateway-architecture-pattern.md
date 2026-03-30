# ADR-008: Lựa chọn mẫu kiến trúc API Gateway

Date: 2026-03-03 - Accepted
Date: 2026-03-10 - Implemented

## Status

Implemented

## Context

Trong kiến trúc Microservices, hệ thống Task Management bao gồm nhiều dịch vụ độc lập như: Authentication, Profile,
Task,
Notification và Comment, được chạy trên các địa chỉ khác nhau.
Nếu Client giao tiếp trực tiếp với các service này, sẽ nảy sinh các vấn đề nghiêm trọng:

- Client phải tự lưu trữ và quản lý danh sách endpoint của toàn bộ hệ thống.
- Cấu trúc nội bộ bị phơi bày trực tiếp ra Internet.
- Khó tái cấu trúc khi Server muốn gộp, chia tách service hoặc thay đổi port, các ứng dụng Client bắt
  buộc phải cập nhật phiên bản mới.

## Decision

Chúng tôi quyết định áp dụng API Gateway Pattern làm cổng giao tiếp duy nhất cho mọi luồng
dữ liệu từ Client đi vào Server.

Tất cả các request từ bên ngoài sẽ đi qua API Gateway trước khi được định tuyến đến các service tương ứng.

## Consequences

**Tích cực:**

- Đóng gói kiến trúc nội bộ: Client chỉ cần biết và giao tiếp với một Base URL duy nhất. Server có thể tự do thay đổi
  cấu trúc mạng, thêm bớt service mà Client không cần cập nhật lại.
- Xử lý tập trung: Đưa các logic chung như Xác thực token, Giới hạn tốc
  độ và CORS xử lý một lần, giúp các microservices bên trong gọn nhẹ và
  tập trung 100% vào logic nghiệp vụ.
- Tăng cường bảo mật: Chỉ có API Gateway được public, các microservices
  còn lại được giấu kín hoàn toàn trong mạng nội bộ.

**Tiêu cực:**

- Nếu API Gateway sập, toàn bộ hệ thống sẽ ngừng hoạt động. Bắt buộc
  phải triển khai gateway có tính sẵn sàng cao.
- Vì tất cả các request đều đi qua đây nên gateway cần được phân bổ tài nguyên phần
  cứng tốt và có khả năng tự động mở rộng để không làm nghẽn mạng.