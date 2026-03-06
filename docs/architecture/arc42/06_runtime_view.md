# 6. Runtime View

Runtime View mô tả hành vi động của hệ thống qua các kịch bản quan trọng nhất, thể hiện cách các building block tương
tác với nhau trong thời gian thực.

---

## 6.1. Đăng ký tài khoản (User Registration)

**Mô tả:** Luồng này thể hiện Event-Driven Architecture — một hành động đăng ký kích hoạt đồng thời nhiều service qua
RabbitMQ mà không tạo coupling trực tiếp.

```
Client          Gateway         Auth Service       RabbitMQ
  │                │                 │                 │
  │ POST /auth/    │                 │                 │
  │ users/register │                 │                 │
  │───────────────►│                 │                 │
  │                │ forward request │                 │
  │                │────────────────►│                 │
  │                │                 │ validate input  │
  │                │                 │ hash password   │
  │                │                 │ save to DB      │
  │                │                 │────────────────►│
  │                │                 │  publish        │
  │                │                 │  UserCreated    │
  │                │                 │  Event          │
  │                │◄────────────────│                 │
  │◄───────────────│  200 success    │                 │
  │                │                 │                 │
  
RabbitMQ      Profile Service   Task Service   
  │                │                 │                
  │ UserCreated    │                 │                
  │ Event          │                 │                
  │───────────────►│                 │                
  │────────────────────────────────► │                
  │                │ create profile  │                
  │                │ in Neo4j        │                
  │                │ create default  │                
  │─────────────── │ workspace       │                
  │                │ in PostgreSQL   │                
  │                │ send welcome    │                
  │                │ email via Brevo │                
```

**Điểm quan trọng:**

- Client nhận `201 Created` ngay sau khi Auth Service lưu DB thành công — không chờ Profile, Task, Notification
- 3 service consume event **song song**, độc lập nhau
- Nếu Notification Service down, đăng ký vẫn thành công

---

## 6.2. Upload Avatar

**Mô tả:** Luồng này thể hiện cách hệ thống tích hợp với External System (Cloudinary) và tách biệt trách nhiệm giữa
Service Layer và Repository Layer.

```
Client       Gateway      Profile Service    Cloudinary     Neo4j
  │              │               │                │            │
  │ PUT          │               │                │            │
  │ /profiles/   │               │                │            │
  │ me/avatar    │               │                │            │
  │ (multipart)  │               │                │            │
  │─────────────►│               │                │            │
  │              │ validate JWT  │                │            │
  │              │ (introspect)  │                │            │
  │              │──────────────►│                │            │
  │              │               │ validate file  │            │
  │              │               │ size ≤ 5MB     │            │
  │              │               │ type: jpg/png/ │            │
  │              │               │ webp           │            │
  │              │               │───────────────►│            │
  │              │               │  upload via    │            │
  │              │               │  Cloudinary SDK│            │
  │              │               │◄───────────────│            │
  │              │               │  return CDN URL│            │
  │              │               │────────────────────────────►│
  │              │               │  save URL to   │            │
  │              │               │  UserProfile   │            │
  │              │               │  node          │            │
  │              │◄──────────────│                │            │
  │◄─────────────│  200 OK       │                │            │
  │              │               │                │            │
```

**Điểm quan trọng:**

- Server không lưu file — chỉ stream thẳng lên Cloudinary
- Neo4j chỉ lưu **URL**, không lưu binary data
- Client hiển thị ảnh bằng cách tải trực tiếp từ Cloudinary CDN — không đi qua server

---

## 6.3. Thêm thành viên vào Project

**Mô tả:** Luồng này thể hiện Authorization theo RBAC — chỉ thành viên có role MANAGER hoặc ADMIN trong project mới được
thực hiện thao tác này.

```
Client       Gateway        Auth Service     Task Service    PostgreSQL
  │              │                │               │               │
  │ POST         │                │               │               │
  │ /projects/   │                │               │               │
  │ {id}/members │                │               │               │
  │─────────────►│                │               │               │
  │              │ POST /internal/│               │               │
  │              │ auth/introspect│               │               │
  │              │───────────────►│               │               │
  │              │◄───────────────│               │               │
  │              │ { valid: true, │               │               │
  │              │   userId,      │               │               │
  │              │   roles }      │               │               │
  │              │──────────────────────────────► │               │
  │              │                │  check caller │               │
  │              │                │  role in      │               │
  │              │                │  project      │               │
  │              │                │  ─────────────────────────── ►│
  │              │                │  caller is    │               │
  │              │                │  MANAGER?     │               │
  │              │                │  ◄────────────────────────────│
  │              │                │               │ save member   │
  │              │                │               │───────────────►
  │              │                │               │               │
  │              │◄──────────────────────────────-│               │
  │◄─────────────│  201 Created   │               │               │
```

**Điểm quan trọng:**

- Gateway chỉ check token hợp lệ — không biết role trong project
- Authorization chi tiết (MANAGER trong project cụ thể) nằm ở Task Service — đây là **business authorization**, khác với
  **system authorization** ở Gateway
- Nếu caller không có quyền → Task Service trả `403 Forbidden`

---

## 6.4. Kéo thả Task sang Column khác

**Mô tả:** Luồng này thể hiện cách hệ thống xử lý thao tác kéo thả theo thời gian thực, sử dụng Fractional Indexing để
duy trì thứ tự task mà không cần cập nhật toàn bộ danh sách.

```
Client       Gateway        Task Service              PostgreSQL
  │              │               │                        │
  │ PUT          │               │                        │
  │ /tasks/      │               │                        │
  │ {id}/move    │               │                        │
  │ {            │               │                        │
  │   columnId,  │               │                        │
  │   prevPos,   │               │                        │
  │   nextPos    │               │                        │
  │ }            │               │                        │
  │─────────────►│               │                        │
  │              │ validate JWT  │                        │
  │              │──────────────►│                        │
  │              │               │ compute new position   │
  │              │               │ pos = (prevPos +       │
  │              │               │        nextPos) / 2    │
  │              │               │                        │
  │              │               │ UPDATE task SET        │
  │              │               │   column_id = ?,       │
  │              │               │   position = ?         │
  │              │               │───────────────────────►│
  │              │               │◄───────────────────────│
  │              │◄──────────────│                        │
  │◄─────────────│  200 OK       │                        │
  │              │  { task }     │                        │
```

**Điểm quan trọng:**

- Client gửi `prevPos` và `nextPos` — vị trí của task trước và sau điểm thả
- Server tính `newPos = (prevPos + nextPos) / 2`(Fractional Indexing — ADR-021)
- Chỉ **1 record** được UPDATE, không cần reorder toàn bộ danh sách
- Trade-off: sau nhiều lần kéo thả, precision có thể drift → cần normalize định kỳ

---

## 6.5. Tạo Comment trong Task

**Mô tả:** Luồng này thể hiện cách Comment Service hoạt động
độc lập với Task Service — không có direct call giữa 2 service,
chỉ dùng taskId làm foreign reference.

```
Client       Gateway       Comment Service          MongoDB
  │              │               │                     │
  │ POST         │               │                     │
  │ /tasks/      │               │                     │
  │ {taskId}/    │               │                     │
  │ comments     │               │                     │
  │ { content }  │               │                     │
  │─────────────►│               │                     │
  │              │ validate JWT  │                     │
  │              │──────────────►│                     │
  │              │               │ extract userId      │
  │              │               │ from SecurityContext│
  │              │               │                     │
  │              │               │ insert document     │
  │              │               │ {                   │
  │              │               │   taskId,           │
  │              │               │   userId,           │
  │              │               │   content,          │
  │              │               │   createdAt         │
  │              │               │ }                   │
  │              │               │────────────────────►│
  │              │               │◄────────────────────│
  │              │◄──────────────│                     │
  │◄─────────────│  201 Created  │                     │
  │              │  { comment }  │                     │
```

**Điểm quan trọng:**

- Comment Service **không gọi Task Service** để verify taskId tồn tại — chấp nhận eventual consistency
- `userId` lấy từ JWT claims qua SecurityContext, không cần gọi Auth Service
- MongoDB document schema linh hoạt — dễ thêm field như `reactions`, `attachments` sau này mà không cần migration