# Introduction and Goals

Task Management là ứng dụng quản lý công việc theo dạng Kanban board

## Requirements Overview

Mục đích chính của Task Management System là số hóa quy trình giao việc và báo cáo tiến độ cho các nhóm làm việc. Hệ
thống thay thế các phương thức quản lý rời rạc bằng một nền tảng tập trung, giúp minh bạch hóa trạng thái công việc và
giảm thiểu rủi ro trễ hạn.

**Các chức năng chính:**

| ID   | Feature               | Description                                                                         |
|------|-----------------------|-------------------------------------------------------------------------------------|
| *F1* | *Quản lý Dự án*       | *Quản lý Workspace, Project (CRUD).*                                                |
| *F2* | *Quản lý Task*        | *Tạo, sửa, xóa, di chuyển task giữa các cột trạng thái (To Do, In Progress, Done).* |                                                                               |
| *F3* | *Theo dõi & Gán việc* | *Gán thành viên, thiết lập Deadline, gắn nhãn màu ưu tiên.*                         |
| *F4* | *Cộng tác*            | *Bình luận trao đổi trực tiếp trong từng Task.*                                     |
| *F5* | *Báo cáo*             | *Thống kê tiến độ dự án.*                                                           |

## Quality Goals

| Priority | Quality Goal                     | Description                                                                                         |
|----------|----------------------------------|-----------------------------------------------------------------------------------------------------|
| *1*      | *Tính Mô-đun (Modularity)*       | *Hệ thống được phân tách thành các module nghiệp vụ độc lập giúp giảm thiểu sự phụ thuộc lẫn nhau.* |
| *2*      | *Khả năng mở rộng (Scalability)* | *Sẵn sàng cho việc mở rộng dễ dàng tách module thành Microservices khi tải tăng cao.*               |                                                                               |
| *3*      | *Tính Bảo mật (Confidentiality)* | *Đảm bảo tính riêng tư dữ liệu và phân quyền rõ ràng dựa trên vai trò.*                             |

## Stakeholders

| Role/Name             | Contact        | Expectations                                                                                                                      |
|-----------------------|----------------|-----------------------------------------------------------------------------------------------------------------------------------|
| *Giảng viên*          | *Võ Việt Khoa* | *Kiểm tra tính đúng đắn của kiến trúc, sự phù hợp của công nghệ và chất lượng mã nguồn.*                                          |
| *Team Dev*            | *TTT*          | *Cần hiểu rõ cấu trúc Project, quy ước đặt tên và ranh giới giữa các Module để code song song hiệu quả, tránh xung đột mã nguồn.* |
| *Software Architects* | *N/A*          | *Tìm kiếm một ví dụ thực tế về cách tài liệu hóa kiến trúc phần mềm theo chuẩn Arc42.*                                            |
