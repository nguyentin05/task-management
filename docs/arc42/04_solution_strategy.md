# Solution Strategy

## Technology Decisions

| Category         | Technology Choice          | Description                                                                                               |
|------------------|----------------------------|-----------------------------------------------------------------------------------------------------------|
| *Frontend*       | *ReactJS*                  | *Xây dựng ứng dụng đơn trang, giúp trải nghiệm người dùng mượt mà, không cần tải lại trang.*              |
| *Backend*        | *Spring Modulith*          | *Tận dụng sự mạnh mẽ của Java + Spring Modulith giúp tổ chức code theo module rõ ràng dễ mở rộng về sau.* |
| *Database*       | *PostgreSQL*               | *Cơ sở dữ liệu quan hệ mạnh mẽ, mã nguồn mở, đảm bảo tính toàn vẹn dữ liệu cho các giao dịch phức tạp.*   |
| *Security*       | *Spring Security + OAuth2* | *Tiêu chuẩn cho bảo mật Java. Hỗ trợ tốt OAuth2 để đăng nhập bằng Google và quản lý phân quyền.*          |
| *Infrastructure* | *Docker*                   | *Đảm bảo môi trường phát triển và sản phẩm giống nhau.*                                                   |

## Strategy Mapping

| Quality goal       | Scenario                                                                         | Solution approach          | Link to Details |
|--------------------|----------------------------------------------------------------------------------|----------------------------|-----------------|
| *Bảo mật*          | *Hệ thống cần ngăn chặn truy cập trái phép vào API và bảo vệ dữ liệu người dùng* | *Stateless Authentication* | **              |
| *Hiệu năng*        | *Người dùng cần trải nghiệm mượt mà khi thao tác*                                | *Single Page Application*  | **              |
| *Khả năng bảo trì* | *Code cần dễ đọc, dễ sửa lỗi và mở rộng tính năng mới bởi nhiều thành viên*      | *Layered Architecture*     | **              |
| *Tính tương thích* | *Hệ thống cần độc lập nền tảng*                                                  | *RESTful API Standard*     | **              |
| *Triển khai*       | *Cần đảm bảo sự ổn định khi triển khai lên server thật*                          | *Containerization*         | **              |
