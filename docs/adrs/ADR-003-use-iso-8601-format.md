# ADR-003: Sử dụng chuẩn thời gian ISO 8601

Date: 2026-03-01

## Status

Implemented

## Context

Hệ thống cần lưu trữ và giao tiếp các dữ liệu thời gian giữa Client và Server. Việc sử dụng các định dạng mang tính địa
phương sẽ gây ra sự nhầm lẫn khi đọc dữ liệu, đồng thời gây khó khăn cho hệ thống khi phân tích và sắp xếp.

## Decision

Chúng tôi quyết định sử dụng chuẩn quốc tế ISO 8601 `yyyy-mm-dd` cho toàn bộ dữ liệu
thời gian trong hệ thống.

## Consequences

**Tích cực:**

- Dữ liệu thời gian rõ ràng, đồng nhất trên toàn bộ hệ thống.
- Ngăn chặn các lỗi logic liên quan đến sai lệch định dạng.

**Tiêu cực:**

- Chuỗi ISO 8601 thô khá dài và máy móc buộc đội frontend sẽ phải tốn thêm bước format lại dữ liệu thành định
  dạng thân thiện `dd-mm-yyyy` trước khi hiển thị lên giao diện cho người dùng cuối.