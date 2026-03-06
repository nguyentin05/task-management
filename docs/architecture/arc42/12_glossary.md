# 12. Glossary

---

## 12.1. Thuật ngữ kiến trúc

| Thuật ngữ                    | Viết tắt | Định nghĩa                                                                                      |
|------------------------------|----------|-------------------------------------------------------------------------------------------------|
| Architecture Decision Record | ADR      | Tài liệu ghi lại một quyết định kiến trúc quan trọng, bao gồm bối cảnh, quyết định và hệ quả    |
| API Gateway                  | —        | Điểm vào duy nhất của hệ thống, chịu trách nhiệm routing, authentication và rate limiting       |
| Microservices                | —        | Kiến trúc phân rã ứng dụng thành các service nhỏ, độc lập, mỗi service chạy trong process riêng |
| Layered Architecture         | —        | Kiến trúc phân tầng gồm Controller → Service → Repository, mỗi tầng có trách nhiệm riêng biệt   |
| Event-Driven Architecture    | EDA      | Kiến trúc trong đó các service giao tiếp thông qua việc publish và subscribe event              |
| Database-per-Service         | —        | Pattern mỗi Microservice sở hữu database riêng, không chia sẻ với service khác                  |
| Polyglot Persistence         | —        | Chiến lược sử dụng nhiều loại database khác nhau tùy theo đặc thù dữ liệu của từng service      |
| Single Page Application      | SPA      | Ứng dụng web chạy hoàn toàn trên browser, chỉ load một lần và cập nhật UI động                  |
| Bounded Context              | —        | Ranh giới nghiệp vụ rõ ràng của một service, trong đó các khái niệm domain có ý nghĩa nhất quán |
| Strangler Fig Pattern        | —        | Pattern migration dần dần từ Monolith sang Microservices mà không rewrite toàn bộ               |

---

## 12.2. Thuật ngữ bảo mật

| Thuật ngữ                 | Viết tắt | Định nghĩa                                                                             |
|---------------------------|----------|----------------------------------------------------------------------------------------|
| JSON Web Token            | JWT      | Chuẩn mã hóa thông tin xác thực dạng Base64, gồm header, payload và signature          |
| Role-Based Access Control | RBAC     | Mô hình phân quyền dựa trên vai trò — user được gán role, role được gán permission     |
| Authentication            | AuthN    | Xác minh danh tính: "Bạn là ai?" — kiểm tra token có hợp lệ không                      |
| Authorization             | AuthZ    | Kiểm tra quyền hạn: "Bạn được làm gì?" — kiểm tra role/permission trong token          |
| HS512                     | —        | HMAC-SHA512 — thuật toán ký JWT dùng symmetric key (cùng key để ký và verify)          |
| RS256                     | —        | RSA-SHA256 — thuật toán ký JWT dùng asymmetric key (private key ký, public key verify) |
| Token Blacklist           | —        | Danh sách token đã bị thu hồi (logout), được check mỗi request tại Gateway             |
| Refresh Token Rotation    | —        | Mỗi lần dùng Refresh Token để lấy Access Token mới, Refresh Token cũ bị thu hồi        |
| Secret                    | —        | Thông tin nhạy cảm như password, API key, không được commit vào git                    |

---

## 12.3. Thuật ngữ messaging

| Thuật ngữ               | Viết tắt | Định nghĩa                                                                                              |
|-------------------------|----------|---------------------------------------------------------------------------------------------------------|
| Message Broker          | —        | Hệ thống trung gian nhận và phân phối message giữa các service. Hệ thống dùng RabbitMQ                  |
| Publisher / Producer    | —        | Service publish event lên Message Broker                                                                |
| Subscriber / Consumer   | —        | Service subscribe và xử lý event từ Message Broker                                                      |
| Exchange                | —        | Thành phần trong RabbitMQ nhận message từ Producer và route đến Queue phù hợp                           |
| Queue                   | —        | Hàng đợi lưu trữ message trong RabbitMQ cho đến khi Consumer xử lý                                      |
| Routing Key             | —        | Key dùng để Exchange quyết định route message đến Queue nào                                             |
| At-Least-Once Delivery  | —        | Đảm bảo mỗi message được deliver ít nhất một lần, Consumer cần xử lý idempotent                         |
| Choreography-based Saga | —        | Pattern phân tán transaction: mỗi service tự phản ứng với event, không có coordinator                   |
| Transactional Outbox    | —        | Pattern lưu event vào outbox table trong cùng DB transaction với business data, đảm bảo không mất event |
| Eventual Consistency    | —        | Trạng thái nhất quán sẽ đạt được sau một khoảng thời gian, không đảm bảo ngay lập tức                   |

---

## 12.4. Thuật ngữ domain

| Thuật ngữ        | Định nghĩa                                                                     |
|------------------|--------------------------------------------------------------------------------|
| Workspace        | Không gian làm việc cấp cao nhất, thuộc về một User. Chứa nhiều Project        |
| Project          | Dự án trong Workspace. Chứa Kanban Board với nhiều Column và Task              |
| Board            | Kanban board của một Project, tổ chức Task theo Column                         |
| Column           | Cột trạng thái trong Kanban Board (ví dụ: To Do, In Progress, Done)            |
| Task             | Đơn vị công việc nhỏ nhất. Thuộc một Column, có thể gán thành viên và deadline |
| Label            | Nhãn màu gắn vào Task để phân loại ưu tiên                                     |
| Workspace Member | Thành viên được mời vào Workspace                                              |
| Project Member   | Thành viên được thêm vào Project với role cụ thể (MANAGER hoặc MEMBER)         |
| Comment          | Bình luận của thành viên trong một Task                                        |

---

## 12.5. Thuật ngữ công nghệ

| Thuật ngữ               | Viết tắt | Định nghĩa                                                                                                     |
|-------------------------|----------|----------------------------------------------------------------------------------------------------------------|
| Spring Boot             | —        | Framework Java để build microservice nhanh với auto-configuration                                              |
| Spring Cloud Gateway    | SCG      | API Gateway được xây dựng trên Spring Framework, hỗ trợ reactive programming                                   |
| Spring Security         | —        | Framework bảo mật cho Spring, xử lý authentication và authorization                                            |
| Spring Data JPA         | —        | Abstraction layer trên JPA/Hibernate, tự động generate query từ method name                                    |
| Spring Data Neo4j       | SDN      | Abstraction layer cho Neo4j, map node/relationship thành Java entity                                           |
| Spring Data MongoDB     | —        | Abstraction layer cho MongoDB, map document thành Java entity                                                  |
| MapStruct               | —        | Annotation processor tự động generate code convert giữa DTO và Entity                                          |
| Docker Compose          | —        | Tool định nghĩa và chạy multi-container Docker application từ một file YAML                                    |
| Kubernetes              | K8s      | Hệ thống orchestration container, tự động deploy, scale và quản lý containerized application                   |
| Minikube                | —        | Tool chạy Kubernetes cluster đơn node trên máy local để phát triển và test                                     |
| Persistent Volume Claim | PVC      | Kubernetes object yêu cầu persistent storage cho Pod, dữ liệu không mất khi Pod restart                        |
| ConfigMap               | —        | Kubernetes object lưu trữ configuration non-sensitive dạng key-value                                           |
| Fractional Indexing     | —        | Thuật toán tính vị trí task bằng `(prevPosition + nextPosition) / 2`, cho phép insert mà không reorder toàn bộ |
| Lexorank                | —        | Thuật toán ordering dùng VARCHAR thay DOUBLE, tránh precision drift của Fractional Indexing                    |
| Cloudinary              | —        | Dịch vụ SaaS lưu trữ và xử lý media, cung cấp CDN toàn cầu                                                     |
| Brevo                   | —        | Dịch vụ email transactional (SMTP), dùng để gửi thông báo đến người dùng                                       |

---

## 12.6. Viết tắt

| Viết tắt | Đầy đủ                                         |
|----------|------------------------------------------------|
| API      | Application Programming Interface              |
| CRUD     | Create, Read, Update, Delete                   |
| DTO      | Data Transfer Object                           |
| JPA      | Jakarta Persistence API                        |
| ORM      | Object-Relational Mapping                      |
| REST     | Representational State Transfer                |
| SPA      | Single Page Application                        |
| UUID     | Universally Unique Identifier                  |
| ACID     | Atomicity, Consistency, Isolation, Durability  |
| TTL      | Time To Live                                   |
| CDN      | Content Delivery Network                       |
| CI/CD    | Continuous Integration / Continuous Deployment |
| AMQP     | Advanced Message Queuing Protocol              |
| RBAC     | Role-Based Access Control                      |
| ADR      | Architecture Decision Record                   |
| K8s      | Kubernetes                                     |
| PVC      | Persistent Volume Claim                        |
| SCG      | Spring Cloud Gateway                           |
| EDA      | Event-Driven Architecture                      |

```