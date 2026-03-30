# ADR-030: Chiến lược phiên bản hóa các API

Date: 2026-03-08 — Accepted
Date: 2026-03-10 — Implemented

## Status

Implemented

## Context

Hệ thống cần thống nhất cách versioning API để tránh breaking change khi API thay đổi trong tương lai.

## Decision

Hiện tại: implement cơ bản phần versioning vì frontend và backend cùng team, cùng monorepo, deploy đồng thời — không có
external consumer bị ảnh hưởng.

Định hướng khi cần: Dùng URL path versioning (`/api/v1/resource`) vì dễ test trực tiếp trên browser và Swagger UI, rõ
ràng trong log và monitoring, được Spring Cloud Gateway hỗ trợ tốt.

## Consequences

**Tích cực:**

- Không thêm complexity không cần thiết ở giai đoạn hiện tại
- URL path versioning dễ implement và dễ hiểu khi cần mở rộng

**Tiêu cực:**

- Nếu có breaking change, tất cả client phải update cùng lúc
- Thêm versioning sau này tốn effort refactor