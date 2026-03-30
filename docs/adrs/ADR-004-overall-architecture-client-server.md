# ADR-004: Lựa chọn kiến trúc tổng thể là Client-Server

Date: 2026-03-01 - Accepted
Date: 2026-03-08 - Implemented

## Status

Implemented

## Context

Hệ thống Task Management cần 1 kiến trúc có thể tối ưu hóa khả năng mở rộng, phân tán tải trọng tốt cũng như đảm bảo khả
năng bảo trì ổn định. Nếu gộp chung mã nguồn giao diện và logic nghiệp vụ vào cùng một khối, hệ thống sẽ bị dính chặt,
gây cản trở cho việc phát triển trong tương lai.

## Decision

Chúng tôi quyết định áp dụng kiến trúc Client-Server. Hệ thống được phân tách hoàn toàn thành hai lớp độc lập:

- Client: Chịu trách nhiệm hiển thị giao diện, quản lý trạng thái hiển thị và tương tác trực tiếp với người dùng.
- Server: Tập trung việc xử lý logic nghiệp vụ, phân quyền, xác thực và quản lý cơ sở dữ liệu.

## Consequences

**Tích cực:**

- Dữ liệu và logic nằm tại Server đảm bảo tính nhất quán.
- Dữ liệu lưu an toàn tại Server, áp dụng xác thực tập trung dễ dàng.
- Giao diện có thể linh hoạt thay đổi, nâng cấp công nghệ thỏa mãn khả năng mở rộng cho tương lai.
- Khả năng bảo trì ổn định do chỉ cần cập nhật ở Server và tối ưu phần cứng do nhiều client dùng chung tài nguyên
  Server.

**Tiêu cực:**

- Hệ thống cần phải thiết kế và bảo trì các bộ API giao tiếp giữa hai bên.
- Rủi ro quá tải Server khi lượng client tăng đột biến.
- Phụ thuộc nhiều vào đường truyền mạng.
- Chi phí đầu tư hạ tầng Server, bảo trì và vận hành cao.