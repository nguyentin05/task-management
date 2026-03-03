# ADR-022: CI/CD bằng GitHub Actions

Date: 2026-03-04

## Status

Accepted

## Context

Hệ thống Task Management được tổ chức theo dạng Monorepo, bao gồm nhiều service Spring Boot (Backend), ứng dụng React (
Frontend), và các Dockerfile tương ứng cho mỗi service.
Chúng ta cần một hệ thống CI/CD pipeline tự động để:

1. Kiểm tra chất lượng mã nguồn (build, test) mỗi khi có Pull Request (PR), đảm bảo không có code lỗi (broken build)
   được merge.
2. Tự động build và push Docker images lên registry khi code được merge vào nhánh `main`.

Các lựa chọn được cân nhắc:

- **Phương án 1 — GitHub Actions:** Tích hợp sẵn trong hệ sinh thái GitHub, miễn phí, không tốn công quản lý máy chủ.
- **Phương án 2 — Jenkins:** Self-hosted, mạnh mẽ nhưng yêu cầu phải tự thuê, cấu hình và bảo trì server riêng.

## Decision

Chúng tôi quyết định chọn **GitHub Actions**.

**Cấu trúc thư mục Workflow:**

```text
.github/workflows/
├── ci.yml   ← Trigger khi có PR: Chạy test và kiểm tra build.
└── cd.yml   ← Trigger khi merge vào `main`: Build và push Docker images.
```

**Path filtering** — chỉ build service có thay đổi:

| Thay đổi                          | Job trigger    |
|-----------------------------------|----------------|
| backend/authentication-service/** | build-auth     |
| backend/profile-service/**        | build-profile  |
| backend/task-service/**           | build-task     |
| backend/api-gateway/**            | build-gateway  |
| frontend/**                       | build-frontend |
| docs/**, *.md                     | Không trigger  |

## Consequences

**Tích cực:**

- Tích hợp liền mạch: Hoạt động tự nhiên với GitHub, giao diện trực quan.
- Hiệu năng cao: Path Filtering giúp tiết kiệm đáng kể thời gian CI/CD.
- Infrastructure as Code: Workflow được định nghĩa bằng YAML, version control cùng mã nguồn, dễ dàng review sự thay đổi.

**Tiêu cực:**

- Vendor Lock-in: Phụ thuộc hoàn toàn vào hạ tầng và độ ổn định của GitHub.
- Giới hạn tài nguyên: Bị ràng buộc bởi số phút chạy miễn phí của GitHub Actions.
- Bảo trì thủ công: Mỗi khi thêm một Microservice mới, bắt buộc phải cập nhật lại Path Filtering trong file YAML bằng
  tay.