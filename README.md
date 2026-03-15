# Task Management
## Hệ thống quản lý công việc
## Thành viên nhóm
| MSSV       | Họ tên | Vai trò                                 |
|------------|--------|-----------------------------------------|
| 2351010209 | Nguyễn Trọng Tín | Software Architect, Backend Dev, DevOps |
| 2351010237 | Trần Anh Tú | Frontend Dev                            |
| 2351010232 | Trần Thanh Tung | BA, Tester                              |
## Công nghệ sử dụng
- Backend: Spring Boot
- Frontend: React + Vite
- Databases: PostgreSQL, Neo4j, Mongodb
- Message Broker: RabbitMQ
- Container: Docker + Docker Compose
## Kiến trúc
- Client-Server là tổng thể, Microservices cho phía server và event-driven với layred là bổ trợ
## Cài đặt và chạy
- docker compose up --build -d
### Yêu cầu
- Docker
- Git
### Chạy với Docker Compose
git clone https://github.com/nguyentin05/task-management.git && cd task-management && docker compose up -d
### Truy cập
- Frontend: http://localhost:3000
- Backend API gateway: http://localhost:8888
- RabbitMQ Management: http://localhost:15672
## Demo
comming soon...
## Tài liệu
- [ADRs](docs/adrs/)
- [Arc42](docs/architecture/arc42/)
- [C4 model](docs/architecture/c4/)
