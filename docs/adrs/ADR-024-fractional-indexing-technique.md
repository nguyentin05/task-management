# ADR-024: Kỹ thuật đánh chỉ mục phân số

Date: 2026-03-13 — Accepted
Date: 2026-03-15 — Implemented

## Status

Implemented

## Context

Task management system cần hỗ trợ tính năng kéo thả task giữa các column trong Kanban board. Mỗi task có một vị trí
trong column, người dùng có thể thay đổi thứ tự bằng cách kéo thả.

Với cách tiếp cận truyền thống dùng integer order (1, 2, 3, ...), khi người dùng chèn task vào giữa hai task khác, hệ
thống phải cập nhật lại toàn bộ order của các task phía sau — gây ra N UPDATE query cho mỗi thao tác move.

## Decision

Sử dụng **Fractional Indexing** — lưu vị trí của task dưới dạng số thực `double` thay vì integer.

Nguyên tắc hoạt động:

- Task đầu tiên trong column có order = `1.0`
- Task thứ hai có order = `2.0`
- Khi chèn task vào giữa hai task có order `a` và `b`, order mới = `(a + b) / 2`
- Ví dụ: chèn giữa `1.0` và `2.0` → order mới = `1.5`
- Chèn tiếp giữa `1.0` và `1.5` → order mới = `1.25`

Kết quả: mỗi thao tác move chỉ cần 1 query duy nhất cho task được di chuyển, không cần cập nhật các task khác.

## Consequences

**Tích cực:**

- Không cần lock table hay distributed transaction cho thao tác di chuyển
- Logic đơn giản: chỉ cần phép tính `(a + b) / 2`

**Tiêu cực:**

- Precision của `double` có giới hạn — sau rất nhiều lần chèn liên tiếp vào cùng một vị trí, hai giá trị order có thể
  bằng nhau do floating point precision limit
- Cần implement rebalancing định kỳ khi các giá trị order quá gần nhau