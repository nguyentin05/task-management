# ADR-031: Kiểm tra chất lượng mã nguồn và bảo mật với SonarCloud và Gitleaks

Date: 2026-04-06 - Accepted
Date: 2026-04-06 - Implemented

## Status

Implemented

## Context

Trong một hệ thống Microservices được phát triển bởi nhiều thành viên, việc duy trì chất lượng mã nguồn và đảm bảo an toàn bảo mật là rất quan trọng. Việc review code thủ công tốn nhiều thời gian và dễ bỏ sót các lỗi tiềm ẩn như code smells, bugs, lổ hổng bảo mật, hay đặc biệt là việc vô tình đẩy các thông tin nhạy cảm lên kho lưu trữ mã nguồn. Chúng ta cần một quy trình tự động trong CI/CD để quét và phát hiện các vấn đề này sớm nhất có thể.

## Decision

Chúng ta sẽ sử dụng và tích hợp các công cụ quét mã tĩnh vào GitHub Actions:

1. **SonarCloud**: 
   - Sử dụng SonarCloud để phân tích mã tĩnh và đo lường chất lượng mã nguồn.
   - SonarCloud sẽ phân tích code smells, bugs, vulnerabilities, test coverage (được tạo ra bởi JaCoCo), và tỷ lệ trùng lặp mã.

2. **Gitleaks**:
   - Sử dụng Gitleaks làm công cụ phát hiện bí mật.
   - Gitleaks sẽ quét lịch sử commit và các file thay đổi để phát hiện các mẫu chứa mật khẩu, token, hoặc API keys.

## Consequences

**Tích cực:**
- Phát hiện sớm rủi ro: Lỗi và lỗ hổng bảo mật được phát hiện ngay từ giai đoạn PR, giảm chi phí sửa chữa.
- Ngăn chặn lộ lọt bí mật: Gitleaks giúp cảnh báo và ngăn chặn nỗ lực commit secrets lên Git, bảo vệ hệ thống khỏi các rủi ro bảo mật nghiêm trọng.
- Duy trì chất lượng cao: Các báo cáo về code coverage và technical debt từ SonarCloud giúp team duy trì codebase sạch sẽ và dễ bảo trì.
- Tự động hóa: Giảm tải cho người review bằng cách tự động hóa kiểm tra tĩnh.

**Tiêu cực:**
- Cấu hình phức tạp: Đòi hỏi phải cấu hình workflow, tokens và xử lý các false-positives.
- Tăng thời gian CI/CD: Các công cụ phân tích tĩnh cộng thêm thời gian chạy cho CI pipeline.
