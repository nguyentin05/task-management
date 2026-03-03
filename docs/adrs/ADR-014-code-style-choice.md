# ADR-014: Lựa chọn phong cách phát triển Database-First

Date: 2026-03-03

## Status

Accepted

## Context

Trong quá trình phát triển các Microservices, chúng ta cần xác định quy trình chuẩn để quản lý cấu trúc cơ sở dữ liệu.
Nếu sử dụng phương pháp Code-First, đội ngũ phát triển có thể code rất nhanh trong giai đoạn đầu. Tuy nhiên, khi hệ
thống lớn lên, việc giao phó toàn quyền sinh schema cho ORM thường dẫn đến các cấu trúc bảng thiếu tối ưu, thiếu các
index phức tạp, và rất khó để quản lý lịch sử thay đổi database trên các môi trường khác nhau.

## Decision

Chúng tôi quyết định áp dụng phong cách tiếp cận Database-First cho toàn bộ các dịch vụ có sử dụng cơ sở dữ liệu quan
hệ:

- Cấu trúc cơ sở dữ liệu phải được thiết kế và viết bằng các đoạn mã script SQL thủ công trước.
- Các class Entity trong mã nguồn ứng dụng sẽ được ánh xạ bám sát theo cấu trúc bảng đã được tạo.

## Consequences

**Tích cực:**

- Kiểm soát tuyệt đối: Làm chủ 100% cấu trúc dữ liệu, kiểu dữ liệu đặc thù của từng loại DB, dễ dàng tối ưu hóa index và
  hiệu năng query.
- Tách bạch trách nhiệm: Database schema là Nguồn sự thật duy nhất. Rõ ràng trong việc quản lý thay đổi.
- An toàn trên Production: Ngăn chặn hoàn toàn rủi ro framework ORM tự động xóa cột hoặc làm hỏng dữ liệu khi có thay
  đổi trong code Entity.

**Tiêu cực:**

- Tốn thời gian ban đầu: Developer phải viết script SQL thủ công và cấu hình mapping khớp với DB.
- Bảo trì phức tạp: Mỗi lần thay đổi cấu trúc bảng, developer phải vừa cập nhật script SQL vừa sửa lại class Entity
  tương ứng.