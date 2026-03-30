# ADR-015: Cấu trúc API Response thống nhất

Date: 2026-03-03 - Accepted
Date: 2026-03-08 - Implemented

## Status

Implemented

## Context

Trong một hệ thống phân tán với nhiều Microservices, mỗi service do các developer khác nhau phát triển có thể trả về các
cấu trúc dạng JSON hoàn toàn khác nhau.

Sự thiếu đồng nhất này khiến đội ngũ Frontend phải viết các đoạn code bóc tách dữ liệu riêng biệt cho từng API. Nó làm
mất đi khả năng viết các bộ xử lý chặn ở phía Client, gây lãng phí thời gian và cực kỳ dễ sinh ra bug khi bắt lỗi.

## Decision

Chúng tôi quyết định áp dụng mẫu thiết kế cho tất cả các API trong hệ thống kể cả xử lý thành công hay thất bại.
Toàn bộ api bắt buộc phải trả về một định dạng duy nhất với cấu trúc JSON cố định gồm 3 trường chuẩn mực:

1. `code`: Mã trạng thái nghiệp vụ.
2. `message`: Thông báo mô tả.
3. `result`: Dữ liệu mang theo.

## Consequences

**Tích cực:**

- Trải nghiệm Frontend: Đội ngũ Client chỉ cần cấu hình một Axios Interceptor duy nhất.
- Dễ dàng bắt lỗi tập trung: Kết hợp hoàn hảo với Global Exception Handler.
- Tính quy chuẩn cao: Tạo ra một bản hợp đồng giao tiếp trên toàn bộ hệ thống.

**Tiêu cực:**

- Dư thừa dữ liệu:Làm tăng nhẹ kích thước gói tin mạng. Kể cả với những API chỉ cần trả về `true`/`false`, payload vẫn
  phải có thêm `code` và `message`.