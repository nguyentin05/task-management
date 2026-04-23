# 4. Solution Strategy

## 4.1. Technology Decisions

| Category                 | Technology Choice           | Description                                                                                                                               |
|--------------------------|-----------------------------|-------------------------------------------------------------------------------------------------------------------------------------------|
| **Frontend**             | ReactJS + Vite              | Xây dựng giao diện Single Page Application tương tác mượt mà, tốc độ build nhanh.                                                         |
| **Backend**              | Java + Spring Boot          | Nền tảng mạnh mẽ và chuẩn mực nhất để xây dựng hệ sinh thái Microservices.                                                                |
| **Kiến trúc luồng**      | Microservices + API Gateway | Phân rã hệ thống thành 5 dịch vụ độc lập để dễ scale up. Dùng Gateway làm chốt chặn định tuyến và bảo mật tập trung.                      |
| **Database**             | Polyglot Persistence        | Áp dụng Database-per-Service: PostgreSQL shared instance (Auth schema + Task schema), Neo4j (Profile), MongoDB (Comment).                  |
| **Giao tiếp & Tích hợp** | RESTful API + RabbitMQ      | Giao tiếp đồng bộ cho các tác vụ cần kết quả ngay. Giao tiếp bất đồng bộ thông qua broker cho các tác vụ nền và luồng giao dịch phân tán. |
| **Hạ tầng & DevOps**     | Docker + GitHub Actions     | Đóng gói toàn bộ bằng Docker (TC1). Tự động hóa kiểm thử và tích hợp bằng CI/CD Pipeline thiết lập trên GitHub Actions.                   |

## 4.2. Strategy Mapping

| Quality goal       | Scenario                                                                         | Solution approach                                                                                                      |
|--------------------|----------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------|
| *Bảo mật*          | *Hệ thống cần ngăn chặn truy cập trái phép vào API và bảo vệ dữ liệu người dùng* | *Stateless Authentication kết hợp với API Gateway làm chốt chặn kiểm tra và phân quyền tập trung.*                     |
| *Hiệu năng*        | *Người dùng cần trải nghiệm mượt mà khi thao tác kéo thả, tải trang*             | *Single Page Application kết hợp với việc ủy thác lưu trữ tài nguyên tĩnh qua mạng lưới Cloud CDN.*                    |
| *Khả năng bảo trì* | *Code cần dễ đọc, dễ sửa lỗi và mở rộng tính năng mới*                           | *Kiến trúc Layered kết hợp với Microservices giúp phân tách rõ ràng ranh giới nghiệp vụ.*                              |
| *Tính tương thích* | *Hệ thống cần độc lập nền tảng*                                                  | *RESTful API Standard, sử dụng định dạng dữ liệu JSON nguyên thủy làm chuẩn giao tiếp duy nhất giữa Client và Server.* |
| *Triển khai*       | *Cần đảm bảo sự ổn định khi triển khai lên server thật*                          | *Containerization đóng gói toàn bộ ứng dụng và database để đảm bảo "Build once, run anywhere".*                        |
| Độ tin cậy         | *Khi gửi thông báo không được làm chậm luồng chính của user*                     | *RabbitMQ decouples notification khỏi business logic chính.*                                                           |