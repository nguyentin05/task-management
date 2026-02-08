# System Scope and Context

## Business Context

<div align="center">
    <img src="../c4/c4-context.png" alt="context diagram" width="100%">
    <br>
    <i>Context Diagram</i>
</div>

| Neighbor             | Description                                                                                                                                   |
|----------------------|-----------------------------------------------------------------------------------------------------------------------------------------------|
| *Authenticated User* | *Người dùng chính của hệ thống. Sử dụng ứng dụng để khởi tạo dự án, quản lý công việc, theo dõi tiến độ và cộng tác với các thành viên khác.* |
| *Admin User*         | *Người quản trị hệ thống. Chịu trách nhiệm quản lý danh sách người dùng, workspace và xem các báo cáo thống kê mức độ hệ thống.*              |
| *Google Login*       | *Hệ thống định danh bên thứ 3. Cung cấp dịch vụ xác thực OAuth 2.0, cho phép người dùng đăng nhập nhanh bằng tài khoản Google.*               |
| *Cloudinary*         | *Dịch vụ lưu trữ đám mây (SaaS). Được sử dụng để lưu trữ, quản lý và tối ưu hóa hình ảnh đại diện và các tệp ảnh đính kèm trong task.*        |
| *Gmail*              | *Hệ thống máy chủ Email. Đóng vai trò gửi các thông báo đến người dùng cuối.*                                                                 |

## Technical Context
