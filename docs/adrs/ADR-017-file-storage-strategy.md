# ADR-017: Chiến lược lưu trữ file bằng Cloudinary

Date: 2026-03-03

## Status

Accepted

## Context

Hệ thống cần lưu trữ file media, cụ thể là ảnh đại diện của người dùng trong Profile Service. Cần quyết định nơi lưu trữ
file phù hợp với kiến trúc Microservices và yêu cầu của hệ thống.

Các phương án được cân nhắc:

**Phương án 1 — Local Filesystem**
Lưu file trực tiếp trên server. Đơn giản nhưng không phù hợp với Microservices
vì mỗi instance có filesystem riêng, gây mất đồng bộ khi scale ngang.

**Phương án 2 — Cloudinary**
Dịch vụ lưu trữ và xử lý media chuyên dụng. Có CDN sẵn, xử lý ảnh tự động
qua URL parameter, free tier đủ dùng cho giai đoạn phát triển.

## Decision

Chúng tôi quyết định sử dụng Cloudinary làm dịch vụ lưu trữ file media.

Quy tắc upload:

- Chỉ chấp nhận định dạng: `jpg`, `png`, `webp`
- Giới hạn kích thước: tối đa 5MB
- Validate MIME type bằng magic bytes, không tin Content-Type header
- Re-encode ảnh qua Cloudinary transformation để loại bỏ metadata độc hại

## Consequences

**Tích cực:**

- Server không lưu file, giữ được tính stateless
- CDN giao ảnh nhanh, giảm tải Server
- Resize ảnh tự động qua URL parameter
- Free tier phù hợp giai đoạn phát triển

**Tiêu cực:**

- Phụ thuộc dịch vụ bên thứ ba, Cloudinary down thì không upload được
- Chi phí tăng theo lưu lượng khi user base lớn
- Cần xử lý cleanup ảnh cũ khi user đổi avatar