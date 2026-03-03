# ADR-020: Chiến lược lưu trữ Secret

Date: 2026-03-03

## Status

Accepted

## Context

Hệ thống Task Management có nhiều thông tin nhạy cảm cần được bảo vệ nghiêm ngặt, không được commit vào git hay hardcode
trong source code:

Cần quyết định cách lưu trữ và truyền secret an toàn qua các môi trường khác nhau.

Các phương án được cân nhắc:

**Phương án 1 — `.env` file không commit + Kubernetes Secret**
File `.env` chỉ tồn tại local, Kubernetes Secret cho staging/production.

**Phương án 2 — HashiCorp Vault / AWS Secrets Manager**
Giải pháp enterprise, centralized secret management. Mạnh nhưng phức tạp và tốn chi phí vận hành.

## Decision

Chúng tôi áp dụng chiến lược lưu trữ secret theo từng môi trường:

Quy tắc bắt buộc:

- `.env` luôn có trong `.gitignore`, không bao giờ commit
- `.env.example` commit vào git với tất cả key nhưng value để trống
- Không dùng `ConfigMap` cho dữ liệu nhạy cảm trong Kubernetes

## Consequences

**Tích cực:**

- `.env.example` là tài liệu sống cho onboarding
- Nhất quán qua các môi trường
- Dễ audit: biết chính xác service nào cần secret gì

**Tiêu cực:**

- Developer mới phải xin secret thủ công
- Chưa có secret rotation tự động
- Nếu `.env` bị leak local thì toàn bộ credential bị lộ