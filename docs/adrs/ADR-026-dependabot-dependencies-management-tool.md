# ADR-026: Quản lý dependency tự động bằng Dependabot

Date: 2026-03-20 — Accepted
Date: 2026-03-21 — Implemented

## Status

Implemented

## Context

Hệ thống gồm 5 Spring Boot service với nhiều dependency Maven, cộng với frontend React với npm packages. Quản lý
dependency thủ công trong monorepo nhiều service có các rủi ro:

- Dependency lỗi thời chứa security vulnerability không được phát hiện kịp thời
- Developer thường không chủ động update dependency vì sợ breaking change
- Không có quy trình chuẩn để track và review dependency update

## Decision

Sử dụng **GitHub Dependabot** để tự động hóa việc theo dõi và update dependency.

Schedule: weekly — Dependabot tạo PR vào thứ Hai hàng tuần, tránh spam PR hàng ngày

Tích hợp với CodeQL và GitHub Advanced Security: Dependabot alerts tự động khi phát hiện dependency có CVE đã biết,
không cần chờ scheduled scan.

## Consequences

**Tích cực:**

- Security vulnerability trong dependency được phát hiện và có PR fix tự động
- Nhóm luôn có thông tin về version mới của dependency quan trọng
- GitHub Advanced Security tích hợp sẵn — không cần setup tool bên ngoài
- PR từ Dependabot có thể được review và merge như PR thông thường, CI chạy test tự động trước khi merge

**Tiêu cực:**

- Dependabot có thể tạo nhiều PR cùng lúc khi có nhiều update — cần có người review và merge định kỳ
- Major version update thường có breaking change — Dependabot tạo PR nhưng không tự resolve conflict