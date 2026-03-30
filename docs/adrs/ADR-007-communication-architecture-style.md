# ADR-007: Phong cách kiến trúc RESTful và giao tiếp Stateless

Date: 2026-03-03 - Accepted
Date: 2026-03-08 - Implemented

## Status

Implemented

## Context

Sau khi lựa chọn kiến trúc tổng thể là Client-Server, chúng ta cần một tiêu chuẩn giao tiếp đồng bộ để Client có thể gọi
các chức năng của hệ thống, cũng như để các service trong Server giao tiếp với nhau.

Chuẩn giao tiếp này phải thỏa mãn các tiêu chí:

- Phổ biến, dễ dàng tích hợp với các framework.
- Hỗ trợ tốt cho việc mở rộng hệ thống khi tải tăng cao, Server không được phép bị phụ
  thuộc vào bộ nhớ cục bộ để lưu trữ phiên làm việc của người dùng.

## Decision

Chúng tôi quyết định áp dụng phong cách kiến trúc RESTful (Representational State Transfer) kết hợp với nguyên tắc thiết
kế Stateless cho toàn bộ các API của hệ thống:

1. Chuẩn giao tiếp: Sử dụng giao thức HTTP/HTTPS với định dạng dữ liệu là JSON.
2. Stateless: Server sẽ không lưu trữ bất kỳ trạng thái phiên làm việc nào của Client.
3. Ngữ cảnh độc lập: Mỗi HTTP request gửi từ Client bắt buộc phải chứa đầy đủ thông tin để server có thể hiểu và xử
   lý mà không cần tham chiếu đến các request trước đó.

## Consequences

**Tích cực:**

- Khả năng mở rộng: Vì hệ thống là Stateless, Load Balancer có thể điều hướng request của
  một user đến bất kỳ instance nào của service mà không lo lỗi mất đồng bộ session.
- Tính lỏng lẻo: Giao diện thống nhất qua các HTTP method tạo ra các bộ api rõ ràng, giúp team Frontend và Backend làm
  việc hoàn toàn độc lập.

**Tiêu cực:**

- Overhead băng thông: Việc phải đính kèm toàn bộ ngữ cảnh trong Header của mọi request sẽ
  làm tăng nhẹ kích thước gói tin mạng.
- Vấn đề Over-fetching: Đặc thù của REST là trả về các resource cố định, có thể khiến Client nhận
  thừa dữ liệu không cần thiết hoặc phải gọi nhiều API liên tiếp để gom đủ dữ liệu.
- Hạn chế giao tiếp thời gian thực: REST là mô hình Request-Response một chiều. Đối với các
  tính năng cần Server đẩy dữ liệu xuống, REST sẽ kém hiệu quả và sẽ cần bổ
  sung thêm WebSocket trong tương lai.