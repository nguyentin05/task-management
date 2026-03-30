# ADR-029: Chiến lược nâng cấp lên Kubernetes

Date: 2026-03-24

## Status

Proposed

## Context

Hệ thống hiện tại chạy bằng Docker Compose trên single host. Khi cần scale lên production, Docker Compose không hỗ trợ
auto-restart, horizontal scaling, rolling update và có single point of failure.

## Decision

Chọn Kubernetes làm nền tảng orchestration cho giai đoạn production. Lộ trình migration 3 bước:

1. **Containerization** — Mỗi service có Dockerfile riêng, image push lên GHCR qua CI/CD
2. **Kubernetes manifest** — Chuyển đổi docker-compose sang K8s resources (Deployment, Service, ConfigMap, Secret, PVC,
   Ingress)
3. **Helm chart** — Package manifest theo environment, hỗ trợ rollback

## Consequences

**Tích cực:**

- Self-healing, horizontal scaling, rolling update không downtime
- KEDA auto-scaling dựa trên RabbitMQ queue length — tích hợp tự nhiên với Outbox pattern
- Tích hợp observability stack theo định hướng ADR-028

**Tiêu cực:**

- Độ phức tạp vận hành tăng đáng kể so với Docker Compose
- Over-engineering cho quy mô đồ án hiện tại