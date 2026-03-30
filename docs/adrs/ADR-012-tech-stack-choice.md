# ADR-012: Lựa chọn Tech Stack Ứng dụng

Date: 2026-03-03 - Accepted
Date: 2026-03-08 - Implemented

## Status

Implemented

## Context

Sau khi định hình kiến trúc cho hệ thống, chúng ta cần thống nhất bộ công cụ để xây dựng phần mềm.
Tiêu chí lựa chọn dựa trên:

1. Phù hợp với kiến trúc phân tán, hỗ trợ tốt các pattern đã đề ra.
2. Hiệu năng phát triển cao, cộng đồng hỗ trợ mạnh mẽ.
3. Phân tách rõ ràng giữa Frontend và Backend.

## Decision

Chúng tôi quyết định sử dụng bộ Tech Stack sau cho toàn bộ việc phát triển ứng dụng:

**1. Backend (Server-side):**

- Ngôn ngữ & Framework: Sử dụng Java kết hợp Spring Boot. Spring Boot cung cấp một hệ sinh thái hoàn thiện giúp xây dựng
  các Microservices độc lập một cách nhanh chóng và chuẩn mực.
- API Gateway: Sử dụng Spring Cloud Gateway. Hoạt động hoàn hảo trong hệ sinh thái Spring, hỗ trợ kiến trúc Non-blocking
  WebFlux giúp xử lý lượng lớn requests để làm cổng định tuyến tập trung.
- Message Broker: Sử dụng RabbitMQ. Đóng vai trò là xương sống cho giao tiếp bất đồng bộ, hỗ trợ đắc lực cho Saga
  Pattern nhờ cơ chế định tuyến linh hoạt và độ tin cậy cực cao.

**2. Frontend (Client-side):**

- Ngôn ngữ & Thư viện: Sử dụng JavaScript kết hợp với thư viện React. Thiết kế giao diện theo mô hình SPA giúp tương tác
  người dùng mượt mà.
- Build Tool: Sử dụng Vite thay vì Create React App truyền thống. Vite mang lại tốc độ khởi động server dev cực nhanh và
  thời gian build tối ưu nhờ tận dụng native ES modules.

## Consequences

**Tích cực:**

- Hệ sinh thái khổng lồ: Cả Spring Boot và React đều là những thế lực thống trị trong ngành. Tài liệu phong phú, các thư
  viện bên thứ 3 sẵn có cực kỳ nhiều.
- Trải nghiệm phát triển xuất sắc: Việc dùng Vite cho Frontend giúp frontend developer tiết kiệm hàng chục giờ chờ đợi
  build code. Hệ sinh thái Spring Cloud giúp backend developer dễ dàng cấu hình Microservices.
- Tính chuyên biệt hóa: Tách bạch hoàn toàn Backend và Frontend giúp hai team có thể làm việc song song, deploy độc lập
  mà không chờ đợi nhau.

**Tiêu cực:**

- Tiêu tốn tài nguyên Backend: Các ứng dụng Spring Boot chạy trên JVM thường ngốn nhiều RAM và có thời gian khởi động
  chậm hơn đáng kể so với các ngôn ngữ biên dịch khác.
- Phân mảnh ngữ cảnh: Đội ngũ phát triển sẽ phải liên tục chuyển đổi tư duy giữa lập trình hướng đối tượng tĩnh ở
  Backend và lập trình hàm linh động ở Frontend.
- Độ phức tạp học tập: Phải làm quen với lập trình phản ứng khi cấu hình Spring Cloud Gateway, cũng như học cách thiết
  kế Exchange/Queue chuẩn xác trong RabbitMQ.