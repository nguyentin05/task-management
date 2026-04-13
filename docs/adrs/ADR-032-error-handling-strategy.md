# ADR-032: Chiến lược Xử lý Lỗi Tập trung

Date: 2026-04-05 - Accepted
Date: 2026-04-05 - Implemented

## Status

Implemented

## Context

Trong hệ thống kiến trúc Microservices, mỗi service có thể ném ra các loại ngoại lệ khác nhau. Nếu không quy chuẩn, mỗi service sẽ trả về API theo các cấu trúc JSON báo lỗi khác nhau, hoặc rủi ro nghiêm trọng hơn là làm rò rỉ thông tin hệ thống nhạy cảm. Điều này không chỉ gây khó khăn cho Frontend khi tích hợp mã lỗi mà còn vi phạm Security. Do đó, cần một chuẩn chung cho toàn bộ dự án về cách thức phản hồi và xử lý lỗi.

## Decision

Chúng ta sẽ áp dụng chiến lược Centralized Error Handling cho mọi service:

1. Sử dụng GlobalExceptionHandler trong mỗi service để gom bắt tất cả mọi ngoại lệ ở cấp độ toàn cục.
2. Chuẩn hóa Error Response: Phản hồi về client luôn tuân theo một format cố định.
3. Sử dụng ErrorCode Enum: Khai báo danh sách các mã lỗi định nghĩa sẵn thay vì ném ra Exception chung chung. Mọi business exception đều phải được gắn kèm theo một ErrorCode.
4. Không rò rỉ thông tin: Ở môi trường Production, tuyệt đối không trả về stack trace hoặc các trace log nội bộ của Framework.

## Consequences

**Tích cực:**
- Đồng nhất API: Frontend chỉ cần xử lý một cấu trúc JSON duy nhất khi gặp lỗi cho mọi endpoints.
- Tăng độ bảo mật: Dữ liệu lỗi được chuẩn hóa, ẩn đi các rủi ro làm lộ thông tin nội bộ của Spring Boot hoặc DB, tuân thủ nguyên tắc fail-secure và fail-safe.
- Kiểm soát mã lỗi hiệu quả: Bằng cách tập trung vào ErrorCode, logic phía client có thể mapping mã lỗi chính xác với thông báo thân thiện với người dùng.

**Tiêu cực:**
- Đòi hỏi tuân thủ nghiêm ngặt: Tất cả developer phải sử dụng AppException kết hợp với ErrorCode.
- Maintain ErrorCode: Khi ứng dụng phình to, Enum ErrorCode có thể phát triển rất lớn, cần quản lý tốt để tránh trùng lặp mã lỗi giữa các domains.
