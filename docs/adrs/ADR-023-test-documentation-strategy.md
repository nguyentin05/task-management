# ADR-023: Tài liệu hóa kiểm thử

Date: 2026-03-15

## Status

Accepted

## Context

Hệ thống microservices có nhiều luồng nghiệp vụ phức tạp liên quan đến nhiều service. Khi thực hiện kiểm thử, nhóm cần
thống nhất cách tài liệu hóa quá trình kiểm thử để:

- Đảm bảo các thành viên hiểu rõ phạm vi và kết quả kiểm thử
- Có bằng chứng rõ ràng về việc hệ thống đã được kiểm thử

## Decision

Nhóm thống nhất tài liệu hóa kiểm thử theo 3 thành phần:

**Test Scenario**
Mô tả các kịch bản kiểm thử ở mức cao — luồng nghiệp vụ cần được kiểm tra. Mỗi scenario mô tả điều kiện đầu vào, hành
động thực hiện, và kết quả kỳ vọng.

**Test Case**
Chi tiết hóa từng scenario thành các test case cụ thể, bao gồm cả happy path và edge case. Test case được implement
thành automated test trong code.

**Test Report**
Kết quả thực tế sau khi chạy test — số lượng test passed/failed, coverage report, và screenshot hoặc log khi có test
fail.

## Consequences

**Tích cực:**

- Nhóm có tài liệu rõ ràng để đối chiếu khi có bug
- Test report giúp chuyên nghiệp hóa hệ thống

**Tiêu cực:**

- Cần đầu tư thời gian viết test scenario và test case trước khi implement
- Test report từ CI chỉ có giá trị khi CI pass — nếu môi trường CI có vấn đề, report không phản ánh đúng thực tế