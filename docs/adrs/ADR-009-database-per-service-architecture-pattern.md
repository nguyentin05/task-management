# ADR-009: Lựa chọn kiến trúc Database-per-service

Date: 2026-03-03 - Accepted
Date: 2026-03-22 - Implemented

## Status

Implemented

## Context

Với việc áp dụng kiến trúc Microservices, các dịch vụ đã được tách biệt rõ ràng. Tuy nhiên, nếu tất
cả các service vẫn kết nối chung vào một cơ sở dữ liệu vật lý duy nhất, tính độc lập này
sẽ bị phá vỡ:

- Việc một service thay đổi cấu trúc bảng có thể làm sập các service khác đang dùng chung bảng đó.
- Dữ liệu của các miền nghiệp vụ rất khác nhau. Một loại database duy nhất sẽ khó tối ưu cho tất cả.

## Decision

Chúng tôi quyết định áp dụng mẫu kiến trúc Database-per-Service Pattern:

- Độc quyền dữ liệu: Dữ liệu thuộc về service nào thì service đó quản lý. Các service khác tuyệt đối không
  được truy cập vào database của nhau mà bắt buộc phải giao tiếp thông qua service đó.
- Tối ưu công nghệ: Mỗi service được quyết định lựa chọn loại Database phù hợp nhất với đặc thù nghiệp vụ của mình.

## Consequences

**Tích cực:**

- Loose Coupling: Đảm bảo sự độc lập tuyệt đối. Đội ngũ phát triển của một service có thể thoải mái
  thay đổi schema database mà không sợ ảnh hưởng đến phần còn lại của hệ thống.
- Tối ưu hiệu năng & Chi phí: Áp dụng đúng công cụ cho đúng việc. Có thể mở rộng database của các service chịu tải cao
  một cách độc lập.
- Cô lập lỗi: Nếu database của một service bị sập, database của service khác vẫn hoạt
  động bình thường, không ảnh hưởng toàn hệ thống.

**Tiêu cực:**

- Độ phức tạp truy vấn: Không thể sử dụng câu lệnh SQL thông thường để lấy dữ
  liệu từ nhiều service.
- Giao dịch phân tán: Đảm bảo tính nhất quán dữ liệu khi một luồng
  nghiệp vụ ghi dữ liệu trên nhiều service trở thành bài toán cực khó. Hệ thống bắt buộc phải triển khai thêm các mẫu
  thiết kế phức tạp như Saga Pattern hoặc Outbox Pattern.
- vận hành phức tạp: độ phức tạp DevOps, quản lý hạ tầng, backup và giám sát tăng lên do số lượng database nhiều hơn.