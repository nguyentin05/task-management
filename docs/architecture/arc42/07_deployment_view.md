# 7. Deployment View

Deployment View mô tả môi trường triển khai của hệ thống qua hai giai đoạn: Local Development với Docker Compose và
Production với Kubernetes.

---

## 7.1. Local Development — Docker Compose

**Mô tả:** Toàn bộ hệ thống chạy trên một máy duy nhất thông qua Docker Compose. Các service giao tiếp qua Docker
network nội bộ, không expose port trực tiếp ra ngoài trừ Gateway.

```
┌─────────────────────────────────────────────────────────────────┐
│                     Developer Machine                           │
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │                   Docker Engine                          │   │
│  │                                                          │   │
│  │  ┌─────────────────────────────────────────────────────┐ │   │
│  │  │              task-management-network                │ │   │
│  │  │                                                     │ │   │
│  │  │  ┌────────────┐     ┌─────────────────────────┐     │ │   │
│  │  │  │  React SPA │     │   Spring Cloud Gateway  │     │ │   │
│  │  │  │  :3000     │────►│   :8888                 │     │ │   │
│  │  │  └────────────┘     └──────────┬──────────────┘     │ │   │
│  │  │                                │                    │ │   │
│  │  │           ┌────────────────────┼───────────┐        │ │   │
│  │  │           │                    │           │        │ │   │
│  │  │  ┌────────▼───┐  ┌─────────────▼──┐  ┌─────▼────┐   │ │   │
│  │  │  │    Auth    │  │    Profile     │  │   Task   │   │ │   │
│  │  │  │  Service   │  │    Service     │  │  Service │   │ │   │
│  │  │  │  :8080     │  │    :8081       │  │  :8082   │   │ │   │
│  │  │  └─────┬──────┘  └───────┬────────┘  └────┬─────┘   │ │   │
│  │  │        │                 │                │         │ │   │
│  │  │  ┌─────▼─────────────────┼────────────────▼─────┐   │ │   │
│  │  │  │     PostgreSQL (shared, schema separation)   │   │ │   │
│  │  │  │  auth_schema │ task_schema                    │   │ │   │
│  │  │  │  :5432                                       │   │ │   │
│  │  │  └─────────────────────────────────────────────┘   │ │   │
│  │  │               ┌───────▼────────┐                    │ │   │
│  │  │               │     Neo4j      │                    │ │   │
│  │  │               │   profile_db   │                    │ │   │
│  │  │               │   :7474/:7687  │                    │ │   │
│  │  │               └────────────────┘                    │ │   │
│  │  │                                                     │ │   │
│  │  │  ┌─────────────┐  ┌──────────────┐  ┌────────────┐  │ │   │
│  │  │  │   Comment   │  │ Notification │  │  RabbitMQ  │  │ │   │
│  │  │  │   Service   │  │   Service    │  │  :5672     │  │ │   │
│  │  │  │   :8083     │  │   :8084      │  │  UI :15672 │  │ │   │
│  │  │  └──────┬──────┘  └──────────────┘  └────────────┘  │ │   │
│  │  │         │                                           │ │   │
│  │  │  ┌──────▼──────┐                                    │ │   │
│  │  │  │   MongoDB   │                                    │ │   │
│  │  │  │ comment_db  │                                    │ │   │
│  │  │  │   :27017    │                                    │ │   │
│  │  │  └─────────────┘                                    │   │   │
│  │  └─────────────────────────────────────────────────────┘ │   │
│  └──────────────────────────────────────────────────────────┘   │
│                                                                 │
│  Browser ──► http://localhost:3000  (React SPA)                 │
│  Browser ──► http://localhost:8888  (API Gateway)               │
│  Browser ──► http://localhost:15672 (RabbitMQ Management UI)    │
└─────────────────────────────────────────────────────────────────┘
```

**Port mapping:**

| Container              | Internal Port | Host Port | Ghi chú                    |
|------------------------|---------------|-----------|----------------------------|
| react-spa              | 80            | 3000      | Nginx serve static files   |
| api-gateway            | 8888          | 8888      | Điểm vào duy nhất          |
| authentication-service | 8080          | —         | Chỉ trong Docker network   |
| profile-service        | 8081          | —         | Chỉ trong Docker network   |
| task-service           | 8082          | —         | Chỉ trong Docker network   |
| comment-service        | 8083          | —         | Chỉ trong Docker network   |
| notification-service   | 8084          | —         | Chỉ trong Docker network   |
| postgres               | 5432          | 5433      | Shared: auth + task schema |
| neo4j                  | 7687          | 7687      | Bolt protocol              |
| neo4j                  | 7474          | 7474      | Browser UI                 |
| mongodb                | 27017         | 27017     | Dev access trực tiếp       |
| rabbitmq               | 5672          | 5672      | AMQP protocol              |
| rabbitmq               | 15672         | 15672     | Management UI              |

**Startup order (`depends_on`):**

```
PostgreSQL, Neo4j, MongoDB, RabbitMQ    (infrastructure)
        │
        ▼
Authentication Service                  (phải up trước)
        │
        ▼
Profile Service, Task Service,          (depends on Auth)
Comment Service, Notification Service
        │
        ▼
API Gateway                             (depends on all services)
        │
        ▼
React SPA                               (depends on Gateway)
```

---

## 7.2. Production — Kubernetes (Minikube)

**Mô tả:** Hệ thống được triển khai trên Kubernetes cluster. Mỗi service chạy trong Pod riêng, giao tiếp qua Kubernetes
Service. Ingress Controller thay thế vai trò expose port của Docker Compose.

```
┌─────────────────────────────────────────────────────────────────┐
│                    Kubernetes Cluster                           │
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │                 Namespace: task-management               │   │
│  │                                                          │   │
│  │  ┌─────────────────────────────────────────────────┐     │   │
│  │  │              Ingress Controller                 │     │   │
│  │  │         /  → react-svc                          │     │   │
│  │  │         /api → gateway-svc                      │     │   │
│  │  └──────────────────┬──────────────────────────────┘     │   │
│  │                     │                                    │   │
│  │         ┌───────────┴──────────────┐                     │   │
│  │         │                          │                     │   │
│  │  ┌──────▼──────┐          ┌────────▼────────┐            │   │
│  │  │  react-pod  │          │  gateway-pod    │            │   │
│  │  │  (React SPA)│          │  (Spring Cloud  │            │   │
│  │  │  replicas:1 │          │   Gateway)      │            │   │
│  │  └─────────────┘          │  replicas:1     │            │   │
│  │                           └────────┬────────┘            │   │
│  │                                    │                     │   │
│  │        ┌───────────────────────────┼──────────────┐      │   │
│  │        │                           │              │      │   │
│  │  ┌─────▼──────┐  ┌─────────────────▼──┐  ┌───────▼──┐    │   │
│  │  │  auth-pod  │  │   profile-pod      │  │ task-pod │    │   │
│  │  │  replicas:1│  │   replicas:1       │  │replicas:1│    │   │
│  │  └──────┬─────┘  └────────┬───────────┘  └────┬─────┘    │   │
│  │         │                 │                   │          │   │
│  │  ┌──────▼─────────────────┼───────────────────▼──────┐   │   │
│  │  │     postgres-pod (shared, schema separation)      │   │   │
│  │  │     auth_schema │ task_schema                      │   │   │
│  │  └───────────────────────────────────────────────────┘   │   │
│  │                    ┌──────▼───────┐                       │   │
│  │                    │  neo4j-pod   │                       │   │
│  │                    │ (profile_db) │                       │   │
│  │                    └──────────────┘                       │   │
│  │                                                          │   │
│  │  ┌─────────────┐  ┌──────────────┐  ┌────────────────┐   │   │
│  │  │ comment-pod │  │notif-pod     │  │ rabbitmq-pod   │   │   │
│  │  │ replicas:1  │  │replicas:1    │  │ replicas:1     │   │   │
│  │  └──────┬──────┘  └──────────────┘  └────────────────┘   │   │
│  │         │                                                │   │
│  │  ┌──────▼──────┐                                         │   │
│  │  │mongodb-pod  │                                         │   │
│  │  │(comment_db) │                                         │   │
│  │  └─────────────┘                                         │   │
│  │                                                          │   │
│  │  ┌──────────────────────────────────────────────────┐    │   │
│  │  │                  ConfigMap + Secret              │    │   │
│  │  │  SPRING_PROFILES_ACTIVE=k8s                      │    │   │
│  │  │  POSTGRES_PASSWORD=***  JWT_SECRET_KEY=***       │    │   │
│  │  │  RABBITMQ_PASSWORD=***  BREVO_API_KEY=***        │    │   │
│  │  └──────────────────────────────────────────────────┘    │   │
│  └──────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
```

**Kubernetes objects:**

| Object     | Tên                     | Ghi chú                                  |
|------------|-------------------------|------------------------------------------|
| Namespace  | task-management         | Tất cả resources nằm trong namespace này |
| Deployment | auth-deployment         | replicas: 1                              |
| Deployment | profile-deployment      | replicas: 1                              |
| Deployment | task-deployment         | replicas: 1                              |
| Deployment | comment-deployment      | replicas: 1                              |
| Deployment | notification-deployment | replicas: 1                              |
| Deployment | gateway-deployment      | replicas: 1                              |
| Service    | auth-svc                | ClusterIP — chỉ trong cluster            |
| Service    | profile-svc             | ClusterIP — chỉ trong cluster            |
| Service    | task-svc                | ClusterIP — chỉ trong cluster            |
| Service    | comment-svc             | ClusterIP — chỉ trong cluster            |
| Service    | notification-svc        | ClusterIP — chỉ trong cluster            |
| Service    | gateway-svc             | NodePort — expose ra ngoài               |
| ConfigMap  | task-management-config  | Non-sensitive config (ADR-021)           |
| Secret     | task-management-secrets | Passwords, API keys (ADR-020)            |
| PVC        | postgres-pvc            | Persistent storage cho PostgreSQL (shared) |
| PVC        | neo4j-pvc               | Persistent storage cho Neo4j             |
| PVC        | mongodb-pvc             | Persistent storage cho MongoDB           |
| PVC        | rabbitmq-pvc            | Persistent storage cho RabbitMQ          |

---

## 7.3. So sánh hai môi trường

| Tiêu chí          | Local (Docker Compose)     | Production (Kubernetes)      |
|-------------------|----------------------------|------------------------------|
| Expose ra ngoài   | Port mapping trực tiếp     | Ingress Controller           |
| Service discovery | Docker DNS (tên container) | Kubernetes DNS (tên Service) |
| Config            | `.env` file                | ConfigMap + Secret           |
| Storage           | Docker volume              | PersistentVolumeClaim        |
| Scale             | Không hỗ trợ               | replicas trong Deployment    |
| Health check      | healthcheck + autoheal     | liveness + readiness probe   |
| Spring Profile    | `docker`                   | `k8s`                        |