# ADR-001: Lựa chọn kiến trúc Client-Server Modular Monolith Architecture
## Trạng thái
Accepted
## Bối cảnh
Chúng tôi đang phát triển hệ thống "Task Management"

Quy mô dự án: Bài tập lớn.

Team: 3 thành viên (1 Backend/Leader, 1 Frontend, 1 BA/Tester).

Thời gian phát triển: 8 tuần.
## Quyết định
Sử dụng kiến trúc Nguyên khối Mô-đun phân tách Client-Server:
### 1. Kiến trúc tổng thể
Hệ thống được tách biệt vật lý thành hai thành phần giao tiếp qua RESTful API:
- Client (Frontend): Sử dụng ReactJS (SPA)
- Server (Backend): Sử dụng Spring Boot (Spring Modulith)
### 2. Kiến trúc Backend
- Phần Backend được triển khai dưới dạng một khối duy nhất nhưng mã nguồn được tổ chức theo tư duy Spring Modulith
- Cấu trúc: Code được chia thành các module nghiệp vụ độc lập
- Nguyên tắc: Các module phân tách rõ ràng, chỉ giao tiếp qua Public Interface hoặc Domain Events, không truy cập trực tiếp vào Database của nhau.
### 3. Kiến trúc Thành phần:
- Presentation Layer: REST Controllers.
- Business Layer: Service Model.
- Data Access Layer: Spring Data JPA Repositories.
## Lý do
1. Phù hợp với team 3 người: Kiến trúc Microservices đòi hỏi chi phí vận hành quá lớn. Modular Monolith giúp team dễ triển khai logic nghiệp vụ thay vì cấu hình hạ tầng.
2. Tính tổ chức cao: Spring Modulith giúp ngăn chặn "Spaghetti code". Các module được kiểm soát chặt chẽ, giúp code dễ đọc và dễ bảo trì.
3. Tính tiến hóa: Hệ thống được thiết kế sẵn theo tư duy module hóa. Nếu sau này cần scale up, có thể tách một module cụ thể ra thành Microservices riêng biệt mà không cần đập đi xây lại toàn bộ.
4. Hiệu năng: Giao tiếp giữa các module là gọi hàm hoặc event nội bộ nhanh hơn nhiều so với giao tiếp qua mạng trong kiến trúc Microservices.
## Hệ quả
1. Tích cực:
- Dễ triển khai: Đóng gói đơn giản với Docker.
- Trải nghiệm tốt: SPA mang lại cảm giác ứng dụng mượt mà.
- An toàn dữ liệu: PostgreSQL đảm bảo tính nhất quán cho các giao dịch quan trọng.
2. Tiêu cực:
- Phức tạp ban đầu: Việc setup Spring Modulith và RabbitMQ tốn nhiều công sức hơn so với làm một project Spring Boot bình thường.
- SEO: SPA (React) mặc định hỗ trợ SEO kém hơn Server Side Rendering.
## Ngày quyết định
2026-01-28
