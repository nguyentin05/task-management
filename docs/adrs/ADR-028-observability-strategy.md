# ADR-028: Chiến lược giám sát hệ thống

Date: 2026-03-28

## Status

Accepted

## Context

Hệ thống microservices gồm 5 service chạy độc lập. Khi xảy ra sự cố, việc xác định nguyên nhân trong môi trường phân tán
rất khó nếu không có observability. Nhóm cần chiến lược thống nhất cho 3 trụ cột: Metrics, Logs, Traces.

## Decision

**Logging:** Tất cả service dùng SLF4J + Logback, log theo level chuẩn (ERROR/WARN/INFO), mỗi entry có timestamp và
service name.

**Metrics + Tracing:** Sử dụng OpenTelemetry làm chuẩn thu thập, export sang Prometheus + Grafana (metrics) và Tempo (
traces), log aggregation về Loki qua Promtail. Stack này nằm ngoài phạm vi đồ án do giới hạn thời gian và rủi ro khi
demo.

## Consequences

**Tích cực:**

- Structured logging giúp debug ngay cả khi chưa có log aggregation
- OpenTelemetry vendor-neutral, tích hợp sau không cần thay đổi business code

**Tiêu cực:**

- Chưa có metrics và distributed tracing — khó diagnose performance issue
- Correlate log giữa các service phải làm thủ công qua timestamp