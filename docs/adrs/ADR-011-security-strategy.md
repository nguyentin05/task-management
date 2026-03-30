# ADR-011: Chiến lược bảo mật

Date: 2026-03-03 - Accepted
Date: 2026-03-03 - Implemented

## Status

Implemented

## Context

Với việc áp dụng mẫu kiến trúc API Gateway Pattern thì mọi request từ người dùng sẽ đi qua gateway trước khi vào các
service nội bộ. Nếu mỗi service đều phải tự gọi cơ sở dữ liệu hoặc gọi chéo sang Auth Service để kiểm tra xem user này
là ai và có quyền gì, hệ thống sẽ gặp tình trạng nghẽn cổ chai nghiêm trọng và độ trễ tăng cao. Chúng ta cần một cơ chế
bảo mật phi trạng thái, chia tách rõ ràng trách nhiệm giữa gateway và internal services, đồng thời tối ưu hóa trải
nghiệm người dùng bằng cách giảm thiểu thời gian xử lý request.

## Decision

Chúng tôi quyết định áp dụng chiến lược bảo mật phân tán sử dụng JSON Web Token với thuật toán mã hóa HS512, theo mô
hình "1 Token - 2 Vai trò":

1. Xác thực tập trung tại API Gateway: Gateway đóng vai trò là "người gác cổng". Nó sẽ đảm nhận trách nhiệm xác thực
   request, nếu token hợp lệ Gateway cho request đi tiếp; nếu sai hoặc hết hạn, chặn đứng ngay lập tức để bảo vệ mạng
   nội bộ.
2. "1 Token - 2 Vai trò": Thay vì cấp phát 2 token riêng biệt, hệ thống sinh ra một chuỗi JWT duy nhất đảm nhiệm cả 2
   vai trò thông qua logic kiểm tra động.
3. Phân quyền phân tán tại các Service:
    - Các Microservices nội bộ hoàn toàn tin tưởng JWT đã được Gateway duyệt.
    - Khi nhận request, Service tự bóc tách Payload của JWT để đọc Role/Permission và quyết định xem user có được phép
      thực thi hành động hay không.

## Consequences

**Tích cực:**

- Tách bạch trách nhiệm: Gateway không cần hiểu logic nghiệp vụ phức tạp chỉ lo phần xác thực. Ngược lại, Microservices
  không cần quan tâm đến xác thực chỉ cần chứng thực về quyền.
- Khả năng mở rộng: Hoàn toàn Stateless. Có thể nhân bản Gateway và các Service lên hàng chục node mà không gặp vấn đề
  về đồng bộ session.

**Tiêu cực:**

- Kích thước Payload lớn: Vì phải chứa thêm thông tin Role và Permission, chuỗi JWT sẽ dài hơn, gây tốn băng thông mạng
  trên mỗi request.
- Độ trễ cập nhật quyền hạn: Nếu Admin tước quyền của một User, User đó vẫn có thể sử dụng quyền cũ cho đến khi cái JWT
  hiện tại hết hạn. Cần thời gian sống của token đủ ngắn để giảm thiểu rủi ro này.
- Chia sẻ Secret Key: HS512 là thuật toán mã hóa đối xứng, nghĩa là Auth Service và API Gateway phải cấu hình chung một
  chuỗi Secret Key. Nếu Gateway bị lộ cấu hình, hacker có thể tự ký token giả.