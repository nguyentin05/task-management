# Architecture Constraints

Hệ thống phải tuân thủ các ràng buộc nghiêm ngặt về công nghệ, giới hạn nguồn lực và các quy ước phát triển chung để đảm
bảo tính thống nhất trong nhóm và đáp ứng yêu cầu của môn học.

## Technical Constraints

| ID    | Constraint          | 	Motivation                                                                                   |
|-------|---------------------|-----------------------------------------------------------------------------------------------|
| *TC1* | *Docker*            | *Toàn bộ hệ thống phải được đóng gói và chạy bằng docker compose.*                            |
| *TC2* | *Message Queue*     | *Hệ thống phải tích hợp RabbitMQ để xử lý ít nhất 1 use case bất đồng bộ.*                    |
| *TC3* | *CI/CD Pipeline*    | *Phải thiết lập Pipeline tự động trên GitHub Actions để kiểm tra code mỗi khi có commit mới.* |
| *TC4* | *Testing*           | *Phải có Unit Tests hoặc Integration Tests để đảm bảo chất lượng mã nguồn.*                   |
| *TC5* | *API Documentation* | *Phải sử dụng Swagger/OpenAPI để tài liệu hóa API.*                                           |

## Organizational Constraints

| ID    | Constraint          | 	Motivation                                                                                       |
|-------|---------------------|---------------------------------------------------------------------------------------------------|
| *OC1* | *Version Control*   | *Sử dụng Git + GitHub. Repository phải có lịch sử commit rõ ràng và chiến lược phân nhánh hợp lý.* |
| *OC2* | *Kiến trúc*         | *Áp dụng ít nhất 1 mẫu kiến trúc*                                                                 |
| *OC3* | *Tài liệu*          | *Vẽ sơ đồ theo chuẩn C4 Model và ghi lại các quyết định quan trọng bằng ADR.*                     |
| *OC4* | *Phạm vi chức năng* | *Phải hoàn thành đầy đủ các chức năng theo đề tài đã đăng ký.*                                    |
| *OC5* | *Team*              | *Nhóm TTT gồm 3 thành viên.*                                                                      |
| *OC6* | *Time Schedule*     | *8 tuần (26/1 - 9/2 & 2/3 - 12/4)*                                                      |

## Conventions

| ID   | Convention   | Explanation                                                |
|------|--------------|------------------------------------------------------------|
| *C1* | *Tech Stack* | *Backend: Java / Frontend: ReactJS / DB: PostgreSQL.*      |
| *C2* | *Ngôn ngữ*   | *Tài liệu viết bằng Tiếng Việt. Code viết bằng Tiếng Anh.* |
