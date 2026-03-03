# ADR-002: Sử dụng định dạng Markdown cho ADR

Date: 2026-03-01

## Status

Implemented

## Context

Sau khi quyết định sử dụng Architecture Decision Records để lưu trữ các quyết định về kiến trúc, chúng tôi cần
thống nhất một định dạng tệp tin để viết các tài liệu này và thỏa mãn các yêu cầu sau:

- Thân thiện với Version Control là Git.
- Dễ sử dụng và đọc hiểu đối với mọi người.

## Decision

Chúng tôi quyết định ghi chép mọi Architecture Decision Records bằng định
dạng [Markdown](https://daringfireball.net/projects/markdown/).

## Consequences

**Tích cực:**

- Markdown được hiển thị rất đẹp và rõ ràng trên GitHub.
- Có thể đọc và chỉnh sửa ngay trong terminal.
- Dễ chuyển sang file docx hay pdf bằng các tool như Pandoc.

**Tiêu cực:**

- Không hỗ trợ vẽ các sơ đồ kiến trúc phức tạp trực tiếp bằng thao tác kéo thả.