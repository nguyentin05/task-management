# ADR-025: Lựa chọn JaCoCo đánh giá độ bao phủ test

Date: 2026-03-24 — Accepted
Date: 2026-03-24 — Implemented

## Status

Implemented

## Context

Nhóm cần một công cụ đo độ bao phủ test tự động, tích hợp được với Maven và CI/CD pipeline, hiển thị kết quả trực tiếp
trong GitHub Actions.

## Decision

Sử dụng **JaCoCo** tích hợp vào Maven build của từng service. JaCoCo agent thu thập coverage data khi chạy test,
generate report tại `target/site/jacoco/`. CI pipeline chạy `mvn verify` và upload report lên GitHub Actions artifacts,
coverage summary hiển thị trực tiếp trong workflow run.

## Consequences

**Tích cực:**

- Tích hợp sẵn với Maven, không cần cấu hình phức tạp
- Report HTML chi tiết, dễ xác định code chưa được test
- Summary hiển thị ngay trong GitHub Actions không cần download artifact

**Tiêu cực:**

- Một số class như DTO, mapper cần exclude thủ công để không ảnh hưởng metric