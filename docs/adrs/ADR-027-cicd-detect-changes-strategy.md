# ADR-027: Chiến lược phát hiện sự thay đổi theo service

Date: 2026-03-21 — Accepted
Date: 2026-03-21 — Implemented

## Status

Implemented

## Context

Monorepo gồm 5 service backend độc lập. Nếu mỗi lần push đều build và test toàn bộ service, CI/CD tốn thời gian và tài
nguyên không cần thiết. Nhóm cần chiến lược chỉ chạy pipeline cho service thực sự có thay đổi.
Các phương án được cân nhắc:

**Phương án 1 — Gộp cả CI/CD và detect vào 1 file**
Đơn giản nhất, ít file nhất. Bị loại vì file quá dài, khó đọc và khó maintain khi số lượng service tăng. CI và CD chạy
chung một job, không tách biệt được trách nhiệm.

**Phương án 2 — Gộp detect và CI vào 1 file, CD riêng**
Giảm được một file so với phương án 3. Bị loại vì detect logic bị ràng buộc với CI — nếu muốn tái sử dụng detect output
cho mục đích khác thì phải lặp logic.

**Phương án 3 — 3 file riêng: detect → CI → CD**
Mỗi file có một trách nhiệm rõ ràng. Detect output có thể được tái sử dụng bởi nhiều workflow khác. Dễ mở rộng khi thêm
service mới hoặc thêm bước vào pipeline.

## Decision

Tách thành 3 file workflow riêng biệt: detect-changes, ci, cd (phương án 3)

- detect-changes: dùng dorny/paths-filter phát hiện service nào thay đổi, output boolean flag cho từng service
- ci: nhận output từ detect-changes, dùng matrix strategy chỉ build/test service có sự thay đổi
- cd: nhận output từ ci, chỉ build image và push lên GHCR cho service pass CI

## Consequences

**Tích cực:**

- CI time giảm đáng kể — chỉ build/test service có thay đổi
- Mỗi workflow có trách nhiệm rõ ràng, dễ debug khi có lỗi
- Detect output tái sử dụng được cho các workflow khác

**Tiêu cực:**

- 3 file workflow cần hiểu dependency giữa chúng
- Thay đổi shared config không tự động trigger build tất cả service — cần handle thủ công