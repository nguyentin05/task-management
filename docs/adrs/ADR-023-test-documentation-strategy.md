# ADR-023: Test Documentation Strategy

## Status
Accepted

## Context
Trong quá trình phát triển hệ thống, việc kiểm thử (testing) cần được thực hiện một cách rõ ràng, minh bạch và có thể truy vết. Nếu chỉ dựa vào code test hoặc thực hiện kiểm thử thủ công mà không có tài liệu mô tả, nhóm phát triển sẽ gặp các vấn đề:

- Khó hiểu phạm vi và mục tiêu của từng test.
- Khó onboarding thành viên mới.
- Không có tài liệu tham chiếu cho QA hoặc stakeholder.
- Khó truy vết khi xảy ra lỗi hoặc regression.

Ngoài ra, khi hệ thống phát triển theo hướng microservices và CI/CD, việc chuẩn hóa cách ghi nhận test giúp đảm bảo chất lượng phần mềm và cải thiện khả năng bảo trì.

Vì vậy cần một cách tiếp cận thống nhất để ghi nhận và quản lý các hoạt động kiểm thử.

## Decision
Nhóm quyết định ghi nhận các hoạt động kiểm thử bằng các tài liệu test chuyên biệt trong repository.

Các loại tài liệu test sẽ bao gồm:

- **Test Plan**
  - Mô tả phạm vi test
  - Chiến lược test
  - Môi trường test
  - Các loại test áp dụng (unit, integration, e2e)

- **Test Cases**
  - Mô tả từng trường hợp kiểm thử
  - Input / Output mong đợi
  - Điều kiện tiền đề
  - Các bước thực hiện

- **Test Scenarios**
  - Các luồng nghiệp vụ chính của hệ thống
  - Các trường hợp biên (edge cases)

Các tài liệu này sẽ:

- Được lưu trong repository của dự án
- Viết dưới dạng **Markdown**
- Đặt trong thư mục `docs/testing`
- Được version cùng với source code

Việc cập nhật tài liệu test là trách nhiệm của developer khi thêm feature mới hoặc thay đổi logic hệ thống.

## Consequences

### Positive
- Tăng khả năng truy vết và minh bạch trong quá trình kiểm thử.
- Giúp onboarding developer và QA nhanh hơn.
- Hỗ trợ review và audit chất lượng phần mềm.
- Cải thiện hiểu biết về nghiệp vụ và hệ thống.
- Dễ dàng tích hợp với CI/CD và automation.

### Negative
- Tăng thêm công việc viết và bảo trì tài liệu.
- Có nguy cơ tài liệu bị outdated nếu không được cập nhật cùng code.

Để giảm thiểu vấn đề này, việc cập nhật tài liệu test phải được thực hiện cùng với pull request khi thay đổi logic hoặc thêm tính năng mới.