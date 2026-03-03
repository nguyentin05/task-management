# ADR-013: Lựa chọn công nghệ cho cơ sở dữ liệu

Date: 2026-03-03

## Status

Accepted

## Context

Chúng ta đã quyết định áp dụng mẫu kiến trúc Database-per-Service Pattern. Hệ quả của quyết định này là hệ thống
không còn bị trói buộc vào một loại cơ sở dữ liệu duy nhất.
Trong hệ thống Task Management, các miền nghiệp vụ có đặc thù dữ liệu và hành vi truy vấn hoàn toàn khác biệt:

- Dữ liệu về định danh và công việc cốt lõi (Auth, Task) đòi hỏi tính toàn vẹn cao, cấu trúc quan hệ chặt chẽ và giao
  dịch ACID.
- Dữ liệu về Hồ sơ người dùng (Profile) thường đi kèm với các mối quan hệ chằng chịt. Nếu dùng RDBMS sẽ dẫn đến các câu
  lệnh JOIN nhiều cấp làm giảm hiệu năng.
- Dữ liệu bình luận (Comment) có cấu trúc linh hoạt, độ dài tùy biến, tần suất ghi/đọc lớn.
- Dữ liệu tạm thời (JWT Blacklist) cần tốc độ truy xuất tính bằng mili-giây để không làm chậm luồng xác thực.

## Decision

Chúng tôi quyết định áp dụng chiến lược Polyglot Persistence, lựa chọn 4 công nghệ Database chuyên biệt tối ưu nhất cho
từng mục đích:

1. PostgreSQL (RDBMS):
    - Sử dụng cho: Authentication Service, Task Service.
    - Đảm bảo tính ACID tuyệt đối cho các luồng dữ liệu cốt lõi. Khả năng quản lý quan hệ tốt và độ tin cậy cực cao.

2. Neo4j (Graph Database):
    - Sử dụng cho: Profile Service.
    - Tối ưu hóa việc lưu trữ và truy vấn các thực thể có tính liên kết cao. Truy vấn các mối quan hệ sâu bằng ngôn ngữ
      Cypher nhanh gấp nhiều lần so với các câu lệnh JOIN truyền thống trên SQL.

3. MongoDB (NoSQL Document):
    - Sử dụng cho: Comment Service.
    - Kiến trúc Schema-less cho phép lưu trữ nội dung bình luận một cách linh hoạt. Tốc độ ghi cực nhanh và dễ dàng phân
      mảnh khi lượng comment tăng đột biến.

4. Redis (Caching):
    - Sử dụng cho: Lưu trữ JWT Blacklist và Caching.
    - Lưu trữ toàn bộ trên RAM giúp tốc độ đọc/đối chiếu Token đạt mức siêu tốc. Hỗ trợ sẵn cơ chế TTL tự động xóa token
      hết hạn khỏi danh sách đen.

## Consequences

**Tích cực:**

- Hiệu năng và Tối ưu hóa tuyệt đối: Áp dụng đúng công cụ cho đúng bài toán. Đột phá về
  tốc độ truy vấn mối quan hệ nhờ Neo4j và tốc độ đọc / ghi bình luận nhờ MongoDB.
- Cách ly rủi ro: Nếu MongoDB của Comment Service bị quá tải, nó hoàn toàn không ảnh hưởng đến khả
  năng đăng nhập (Postgres) hay xem hồ sơ (Neo4j) của người dùng.

**Tiêu cực:**

- Gánh nặng Vận hành lớn: Đội ngũ DevOps phải cài đặt, cấu hình mạng, backup và giám sát tới 4 hệ quản trị cơ sở dữ liệu
  hoàn toàn khác nhau.
- Phức tạp về kiến thức: Đội ngũ phát triển bắt buộc phải thông thạo 4 tư duy truy vấn: SQL (Postgres), Cypher (Neo4j),
  Document Query (Mongo) và Key-Value (Redis). Việc chuyển đổi ngữ cảnh khi code các service khác nhau rất dễ gây lỗi.