# 1. Introduction and Goals

Task Management là ứng dụng quản lý công việc theo dạng Kanban board

## 1.1. Requirements Overview

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

## 1.2. Quality Goals

| Priority | Quality Goal                         | Description                                                                              |
|----------|--------------------------------------|------------------------------------------------------------------------------------------|
| *1*      | *Khả năng mở rộng (Scalability)*     | *Mỗi service có thể scale độc lập khi tải tăng, không ảnh hưởng đến service khác.*       |
| *2*      | *Khả năng bảo trì (Maintainability)* | *Phân tách trách nhiệm rõ ràng, tuân thủ chặt chẽ các quy chuẩn để dễ test và thay thế.* |
| *3*      | *Tính Bảo mật (Confidentiality)*     | *Đảm bảo tính riêng tư dữ liệu và phân quyền rõ ràng dựa trên vai trò.*                  |

## 1.3. Stakeholders

| Role/Name             | Contact        | Expectations                                                                                                                      |
|-----------------------|----------------|-----------------------------------------------------------------------------------------------------------------------------------|
| *Giảng viên*          | *Võ Việt Khoa* | *Kiểm tra tính đúng đắn của kiến trúc, sự phù hợp của công nghệ và chất lượng mã nguồn.*                                          |
| *Team Dev*            | *TTT*          | *Cần hiểu rõ cấu trúc Project, quy ước đặt tên và ranh giới giữa các Module để code song song hiệu quả, tránh xung đột mã nguồn.* |
| *Software Architects* | *N/A*          | *Tìm kiếm một ví dụ thực tế về cách tài liệu hóa kiến trúc phần mềm theo chuẩn Arc42.*                                            |
