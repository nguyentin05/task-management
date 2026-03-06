# 8. Cross-Cutting Concepts

Cross-cutting concepts là các quyết định kỹ thuật áp dụng xuyên suốt toàn bộ hệ thống, không thuộc về một service cụ thể
nào.

---

## 8.1. Security — Authentication vs Authorization

> Xem chi tiết: [ADR-011](../adrs/ADR-011-security-strategy.md),
> [ADR-008](../adrs/ADR-008-api-gateway-architecture-pattern.md)

Hệ thống tách biệt hoàn toàn hai khái niệm Authentication và Authorization thành hai tầng riêng biệt:

```
┌─────────────────────────────────────────────────────────┐
│                     API Gateway                         │
│                                                         │
│  AUTHENTICATION                                         │
│  ├── Gọi POST /internal/auth/introspect                 │
│  ├── Validate JWT signature (HS512)                     │
│  ├── Check token blacklist                              │
│  └── Check token expiry                                 │
│                                                         │
│  Nếu hợp lệ → forward request + JWT header đến Service  │
└─────────────────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────┐
│                  Microservices                          │
│                                                         │
│  AUTHORIZATION                                          │
│  ├── Parse JWT claims (userId, roles, permissions)      │
│  ├── Khởi tạo SecurityContext                           │
│  └── @PreAuthorize("hasRole / hasPermission")           │
└─────────────────────────────────────────────────────────┘
```

**JWT Token Structure:**

```json
{
  "sub": "user-uuid",
  "scope": "ROLE_ADMIN PERMISSION_READ_USER",
  "iss": "task-management",
  "iat": 1709000000,
  "exp": 1709003600
}
```

**Token Lifecycle:**

| Token         | TTL    | Lưu trữ        | Mục đích                 |
|---------------|--------|----------------|--------------------------|
| Access Token  | 1 giờ  | Client memory  | Gọi API                  |
| Refresh Token | 10 giờ | Client storage | Lấy Access Token mới     |
| Blacklist     | = TTL  | PostgreSQL     | Thu hồi token khi logout |

**RBAC — 2 cấp phân quyền:**

```
System Role (toàn hệ thống):     Project Role (trong project):
────────────────────────────     ──────────────────────────────
ADMIN   — quản trị hệ thống      MANAGER — quản lý project
USER    — người dùng thường      MEMBER  — thành viên thường
```

---

## 8.2. Error Handling & Error Code

> Xem chi tiết: [ADR-016](../adrs/ADR-016-error-handling-strategy.md)

Toàn bộ hệ thống sử dụng hệ thống mã lỗi 6 chữ số thống nhất, giúp Frontend xử lý lỗi chính xác mà không cần parse
message.

**Cấu trúc mã lỗi:**

```
[ ServiceID ][ LayerID ][ HTTPClass ][ Sequence ]
     2 chữ      1 chữ      1 chữ        2 chữ

ServiceID:   00=Global  01=Auth  02=Profile  03=Task
             04=Comment 05=Notification
LayerID:     0=Global   1=Controller  2=Service  3=Repository
HTTPClass:   4=4xx      5=5xx
Sequence:    01-99
```

**Ví dụ:**

| Code   | Giải thích                                      |
|--------|-------------------------------------------------|
| 000000 | Success — không phải lỗi                        |
| 014201 | Auth Service, Controller, 4xx → FIELD_REQUIRED  |
| 012401 | Auth Service, Service, 4xx → USER_NOT_FOUND     |
| 035201 | Task Service, Service, 5xx → DB_CONNECTION_FAIL |

**GlobalExceptionHandler** được implement ở mỗi service:

```java

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ApiResponse handleAppException(AppException e) {
        return ApiResponse.builder()
                .code(e.getErrorCode().getCode())
                .message(e.getErrorCode().getMessage())
                .build();
    }
}
```

---

## 8.3. API Response Structure

> Xem chi tiết: [ADR-015](../adrs/ADR-015-api-response-structure.md)

Toàn bộ API response sử dụng một wrapper JSON thống nhất, giúp Frontend xử lý response bằng một axios interceptor duy
nhất.

**Standard Response:**

```json
{
  "code": "000000",
  "message": "Success",
  "result": {}
}
```

**Paginated Response:**

```json
{
  "code": "000000",
  "message": "Success",
  "result": {
    "currentPage": 1,
    "totalPages": 10,
    "pageSize": 20,
    "totalElements": 195,
    "data": []
  }
}
```

**Error Response:**

```json
{
  "code": "012401",
  "message": "User not found"
}
```

**Quy tắc:**

- `code = "000000"` → Success, mọi giá trị khác → Error
- `result = null` khi có lỗi
- HTTP Status Code vẫn được sử dụng đúng (200, 201, 400, 404, 500)
- Field names theo camelCase (ADR-019)

---

## 8.4. Event-Driven Communication

> Xem chi tiết: [ADR-006](../adrs/ADR-006-hybrid-architecture.md),
> [ADR-007](../adrs/ADR-007-communication-architecture-style.md)

Hệ thống sử dụng RabbitMQ làm message broker cho giao tiếp bất đồng bộ giữa các service, áp dụng pattern
**Choreography-based Saga** — không có central coordinator.

**Hai loại giao tiếp:**

```
Đồng bộ (REST):                  Bất đồng bộ (RabbitMQ):
──────────────────────           ──────────────────────────
Gateway → Service                Auth Service
Service → Service                  │ publish UserCreatedEvent
  (internal endpoints)             │
Dùng khi: cần kết quả ngay      RabbitMQ Exchange
                                   │
                                   ├──► Profile Service
                                   │    (tạo profile)
                                   ├──► Task Service
                                   │    (tạo workspace)
                                   └──► Notification Service
                                        (gửi email)
```

**Event catalog:**

| Event               | Publisher              | Subscribers                                         |
|---------------------|------------------------|-----------------------------------------------------|
| `UserCreatedEvent`  | Authentication Service | Profile Service, Task Service, Notification Service |
| `TaskAssignedEvent` | Task Service           | Notification Service                                |

**Message format:**

```json
{
  "eventId": "uuid-v4",
  "eventType": "UserCreatedEvent",
  "occurredAt": "2026-03-04T10:00:00Z",
  "payload": {
    "userId": "uuid",
    "email": "user@example.com",
    "fullName": "Nguyen Van A"
  }
}
```

---

## 8.5. Polyglot Persistence

> Xem chi tiết: [ADR-009](../adrs/ADR-009-database-per-service-architecture-pattern.md),
> [ADR-013](../adrs/ADR-013-database-technology-choice.md)

Mỗi service chọn database phù hợp nhất với đặc thù dữ liệu của mình — không bắt buộc dùng chung một loại database.

```
┌─────────────────────────────────────────────────────────┐
│                  Database Strategy                      │
│                                                         │
│  Auth Service ──────────► PostgreSQL                    │
│  Lý do: ACID transactions, quan hệ User-Role-Permission │
│                                                         │
│  Profile Service ───────► Neo4j                         │
│  Lý do: Graph relationships, future social features     │
│                                                         │
│  Task Service ──────────► PostgreSQL                    │
│  Lý do: Complex relations Workspace→Project→Column→Task │
│                                                         │
│  Comment Service ───────► MongoDB                       │
│  Lý do: Flexible schema, nested document structure      │
│                                                         │
│  Notification Service ──► Không có DB (stateless)       │
└─────────────────────────────────────────────────────────┘
```

**So sánh:**

| Service | Database   | Lý do chọn                        | Trade-off                       |
|---------|------------|-----------------------------------|---------------------------------|
| Auth    | PostgreSQL | ACID, foreign key constraints     | Schema cứng, migration phức tạp |
| Profile | Neo4j      | Graph traversal cho relationships | Learning curve cao              |
| Task    | PostgreSQL | Complex joins, transactions       | Khó scale horizontally          |
| Comment | MongoDB    | Schema linh hoạt, nested docs     | Không có ACID cross-collection  |

---

## 8.6. Naming Convention

> Xem chi tiết: [ADR-019](../adrs/ADR-019-naming-convention.md)

Quy ước đặt tên thống nhất áp dụng xuyên suốt toàn bộ hệ thống:

**REST API:**

```
URL path:         kebab-case     /auth/users, /project-members
URL parameter:    camelCase      ?pageSize=10&sortBy=createdAt
Request body:     camelCase      { "firstName": "Tin" }
Response body:    camelCase      { "userId": "uuid" }
```

**Database:**

```
Table name:       snake_case     users, project_members
Column name:      snake_case     first_name, created_at
Primary key:      id             id UUID DEFAULT gen_random_uuid()
Foreign key:      {table}_id     user_id, project_id
```

**Java Code:**

```
Class:            PascalCase     UserService, TaskController
Method:           camelCase      getUserById(), createTask()
Variable:         camelCase      userId, projectName
Constant:         UPPER_SNAKE    MAX_FILE_SIZE, JWT_SECRET
Package:          lowercase      com.ntt.authentication
```

**RabbitMQ:**

```
Exchange:         {domain}.events    user.events, task.events
Queue:            {service}.{event}  profile.user-created
Routing key:      {entity}.{action}  user.created, task.assigned
```

---

## 8.7. Configuration & Secret Management

> Xem chi tiết: [ADR-020](../adrs/ADR-020-secret-storage-strategy.md),
> [ADR-021](../adrs/ADR-021-environment-variable-configuration.md)

Cấu hình được phân loại thành hai nhóm và quản lý khác nhau theo từng môi trường:

```
┌─────────────────────────────────────────────────────────┐
│              Phân loại Configuration                    │
│                                                         │
│  Non-sensitive (commit được):                           │
│  ├── Server port, timeout, page size                    │
│  ├── Feature flags                                      │
│  └── application.yml, application-{profile}.yml         │
│                                                         │
│  Sensitive (KHÔNG commit):                              │
│  ├── JWT_SECRET_KEY                                     │
│  ├── POSTGRES_PASSWORD, NEO4J_PASSWORD                  │
│  ├── MONGODB_PASSWORD, RABBITMQ_PASSWORD                │
│  ├── BREVO_API_KEY                                      │
│  └── CLOUDINARY_API_KEY, CLOUDINARY_API_SECRET          │
└─────────────────────────────────────────────────────────┘
```

**Theo môi trường:**

| Môi trường | Non-sensitive          | Sensitive                |
|------------|------------------------|--------------------------|
| Local      | application-local.yml  | `.env` file (gitignored) |
| Docker     | application-docker.yml | `.env` file (gitignored) |
| Kubernetes | ConfigMap              | K8s Secret               |

**Spring Profile activation:**

```bash
# Local
SPRING_PROFILES_ACTIVE=local

# Docker Compose
SPRING_PROFILES_ACTIVE=docker

# Kubernetes
SPRING_PROFILES_ACTIVE=k8s
```

**Quy tắc bất biến:**

- `.env` luôn có trong `.gitignore`
- `.env.example` commit vào git với key đầy đủ, value để trống
- Kubernetes Secret không dùng ConfigMap cho sensitive data
- CI/CD inject secret qua GitHub Secrets (ADR-022)

---

## 8.8. Distributed Transaction — Saga + Transactional Outbox

> Xem chi tiết: [ADR-010](../adrs/ADR-010-transactional-distributed-strategy.md)

Với Database-per-Service, không thể dùng ACID transaction truyền thống. Hệ thống áp dụng **Choreography-based Saga**kết
hợp **Transactional Outbox Pattern** để đảm bảo eventual consistency.

**Vấn đề cần giải quyết:**

```
❌ Không thể làm:
BEGIN TRANSACTION
  INSERT INTO auth_db.users ...
  INSERT INTO profile_db.profiles ...   ← khác database!
  INSERT INTO task_db.workspaces ...    ← khác database!
COMMIT

✅ Giải pháp: Saga + Outbox
```

**Choreography-based Saga:**

```
Không có central coordinator — mỗi service tự quyết định
hành động tiếp theo dựa trên event nhận được.

Auth Service          Profile Service       Task Service
      │                      │                    │
      │ 1. Save user         │                    │
      │    to PostgreSQL     │                    │
      │ 2. Save              │                    │
      │    UserCreatedEvent  │                    │
      │    to outbox table   │                    │
      │ 3. Commit TX         │                    │
      │                      │                    │
      │──UserCreatedEvent───►│                    │
      │                      │ 4. Create profile  │
      │                      │    in Neo4j        │
      │──UserCreatedEvent────────────────────────►│
      │                      │                    │ 5. Create workspace
      │                      │                    │    
```

**Transactional Outbox Pattern:**

```
Thay vì publish trực tiếp lên RabbitMQ (có thể fail),
Auth Service lưu event vào outbox table trong cùng
một DB transaction với business data:

┌───────────────────────────────────────────────┐
│           Auth Service DB Transaction         │
│                                               │
│  INSERT INTO users (...)         ──┐          │
│  INSERT INTO outbox_events (       │          │
│    event_type = 'UserCreated',     ├── ATOMIC │
│    payload    = {...},             │          │
│    status     = 'PENDING'          │          │
│  )                               ──┘          │
│  COMMIT                                       │
└───────────────────────────────────────────────┘
         │
         │ Outbox Poller (background job)
         │ đọc PENDING events và publish lên RabbitMQ
         ▼
      RabbitMQ
```

**Đảm bảo At-Least-Once delivery:**

| Scenario                                 | Kết quả                               |
|------------------------------------------|---------------------------------------|
| DB commit thành công, publish thành công | Normal flow                           |
| DB commit thành công, publish thất bại   | Outbox poller retry                   |
| DB commit thất bại                       | Không có event, không có side effects |
| Consumer xử lý thất bại                  | RabbitMQ requeue, consumer retry      |