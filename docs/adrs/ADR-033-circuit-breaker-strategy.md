# ADR-033: Chiến lược Circuit Breaker cho giao tiếp nội bộ

Date: 2026-04-11 - Accepted
Date: 2026-04-11 - Implemented

## Status

Implemented

## Context

Trong kiến trúc Microservices, hệ thống thường xuyên có các lời gọi đồng bộ qua HTTP/REST giữa các dịch vụ. Nếu một service trung gian bị chậm hoặc gặp sự cố, các request ở service gọi sẽ bị kẹt, dẫn đến cạn kiệt connection pool và làm sụp đổ toàn bộ hệ thống. Chúng ta cần một cơ chế để ngắt mạch chủ động nhằm bảo vệ tài nguyên hệ thống.

## Decision

Chúng ta sẽ áp dụng Circuit Breaker Pattern bằng thư viện Resilience4j cho các giao tiếp đồng bộ qua OpenFeign giữa Task Service với Authentication Service và Profile Service:

1. Tích hợp tự động qua OpenFeign: Bật spring.cloud.openfeign.circuitbreaker.enabled: true để OpenFeign tự động kích hoạt Circuit Breaker trên mọi Feign Client mà không cần chú thích @CircuitBreaker thủ công.
2. Cấu hình Resilience4j cho từng instance: Task Service cấu hình Circuit Breaker riêng cho authentication-service và profile-service với sliding-window-size: 10, failure-rate-threshold: 50%, wait-duration-in-open-state: 10s, permitted-calls-in-half-open: 3.
3. Time Limiter: Giới hạn timeout 3 giây cho mỗi lời gọi sang Authentication Service và Profile Service — nếu vượt quá, Circuit Breaker tính là failure.
4. Cơ chế Fail-Fast: Khi tỷ lệ lỗi vượt ngưỡng 50% trong cửa sổ 10 lời gọi gần nhất, Circuit Breaker chuyển sang trạng thái OPEN và từ chối các request tiếp theo ngay lập tức.
5. Auto-Recovery: Sau 10 giây ở trạng thái OPEN, Circuit Breaker chuyển sang HALF_OPEN để thử 3 lời gọi thăm dò trước khi khôi phục về CLOSED.

## Consequences

**Tích cực:**
- Chống Cascading Failures: Đảm bảo lỗi từ một dịch vụ không làm sập thêm dịch vụ gọi nó. Giải quyết vấn đề rủi ro Non-risk bảo mật và ổn định hạ tầng.
- Fail-Fast: Giải phóng các thread đang bị block nhanh hơn, giúp tiết kiệm và quản lý tài nguyên hệ thống tốt hơn.
- Tính khả dụng cao hơn: Các hệ thống có khả năng tự phục hồi mà không cần can thiệp thủ công từ quản trị viên.

**Tiêu cực:**
- Độ phức tạp cấu hình: Yêu cầu team phải tùy chỉnh các chỉ số timeout, sliding window size, và error threshold phù hợp với từng API.
- Khó khăn khi test: Việc mô phỏng lỗi Circuit Breaker trong môi trường local đòi hỏi các kịch bản test kỹ càng hoặc integration test tích hợp các công cụ tạo độ trễ nhân tạo.
