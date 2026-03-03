# ADR-021: Cấu hình biến môi trường

Date: 2026-03-04

## Status

Accepted

## Context

Hệ thống Task Management bao gồm nhiều Microservices chạy bằng Docker. Trong quá trình phát triển và vận hành, các
service cần kết nối đến các tài nguyên bên ngoài. Nếu chúng ta hardcode các thông tin nhạy cảm này trực tiếp vào source
code, sẽ dẫn đến rủi ro:

- Bảo mật: Lộ thông tin database, secret keys khi push code lên GitHub.
- Kém linh hoạt: Không thể mang cùng một Docker Image chạy ở các môi trường khác nhau vì mỗi môi trường dùng một
  database khác nhau.

## Decision

Chúng tôi quyết định quản lý toàn bộ cấu hình động và thông tin nhạy cảm thông qua Biến môi trường:

1. Tách biệt cấu hình khỏi mã nguồn: Các file cấu hình như `application.yml` chỉ chứa các biến giữ chỗ dưới dạng
   `${ENV_VAR_NAME}`.
2. Môi trường Local: Sử dụng file `.env` đặt tại thư mục gốc của dự án. File này được thêm vào`.gitignore` để tuyệt đối
   không push lên Git. Cung cấp một file `.env.example` chứa các key rỗng để developer mới biết cần cấu hình những gì.
3. Môi trường CI/CD: Sử dụng GitHub Repository Secrets để lưu trữ các biến này một cách an toàn và tự động bơm vào quá
   trình build/test.

## Consequences

**Tích cực:**

- Bảo mật tuyệt đối: Không có bất kỳ mật khẩu hay khóa bảo mật nào bị rò rỉ trên kho lưu trữ mã nguồn.
- Tính di động cao: Cùng một Docker Image có thể triển khai ở máy Dev, test ở Staging và chạy ở Production chỉ bằng cách
  thay đổi giá trị của các biến môi trường mà không cần phải build lại code.

**Tiêu cực:**

- Phức tạp khi setup ban đầu: Lập trình viên mới tham gia dự án sẽ gặp khó khăn và lỗi không chạy được app nếu quên copy
  file `.env.example` thành `.env` và điền đúng giá trị.
- Rủi ro cấu hình sai: Nếu DevOps hoặc người triển khai cấu hình thiếu hoặc sai tên biến môi trường trên Production
  server, service sẽ bị crash ngay khi khởi động.