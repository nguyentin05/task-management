# ADR-034: Chiến lược phát hành hệ thống

Date: 2026-04-23 - Accepted  
Date: 2026-04-23 - Implemented

## Status

Implemented

## Context

Với kiến trúc microservices, hệ thống cần tự động phát hành version, build Docker image, đảm bảo tất cả service đều có
tag version đồng bộ mỗi lần release, tiết kiệm thời gian build và cho phép rollback dễ dàng.

## Decision

- Sử dụng semantic-release với conventional commits để tự động xác định version (major/minor/patch) và sinh change log.
- Chỉ build và push các service thay đổi trong release.
- Với service không thay đổi: pull image cũ, retag thành version mới và push.
- Tag version tuân theo 'vX.Y.Z'. Tag 'latest' chỉ được cập nhật cho service có thay đổi; service không đổi giữ nguyên '
  latest' cũ.

## Consequences

**Tích cực:**

- Tiết kiệm thời gian build.
- Tất cả service đều có tag version mới – có thể deploy đồng bộ.
- Dễ dàng rollback bằng tag cũ.

**Tiêu cực:**

- CI/CD phức tạp hơn.
- Cần tuân thủ nghiêm ngặt Conventional Commits.
- Service không đổi vẫn phải pull/push.