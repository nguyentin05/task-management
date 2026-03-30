# ADR-016: Chiến lược xử lý lỗi

Date: 2026-03-03 - Accepted
Date: 2026-03-08 - Implemented

## Status

Implemented

## Context

Trong một hệ thống Microservices phân tán, một luồng nghiệp vụ có thể gọi chéo qua nhiều dịch vụ. Khi có lỗi xảy ra, nếu
chỉ sử dụng các mã trạng thái HTTP tiêu chuẩn, hệ thống sẽ gặp các vấn đề sau:

- Không đủ thông tin về lỗi
- Khó nhận biết và truy vết lỗi phát sinh từ Service nào.

## Decision

Chúng tôi quyết định chuẩn hóa chiến lược xử lý lỗi bằng cơ chế Bắt lỗi tập trung và quy chuẩn mã định danh 6 chữ số:

-Cấu trúc mã lỗi 6 chữ số: Hệ thống sử dụng mã lỗi 6 chữ số được thiết kế để encode 4 cấp thông tin cốt lõi theo định
dạng `[Service ID][Layer][HTTP][Sequence]`:

   ```text
   [Service ID] [Layer] [HTTP] [Sequence]
        |          |       |        |
        |          |       |        +-- 2 chữ số: Số thứ tự lỗi cụ thể
        |          |       |
        |          |       +----------- 1 chữ số: HTTP status class
        |          |
        |          +------------------- 1 chữ số: Layer ID
        |
        +------------------------------ 2 chữ số: Service ID
        
   ```

## Consequences

**Tích cực:**

- Tối ưu Frontend: Frontend không cần xử lý chuỗi văn bản, dễ dàng dùng mã lỗi để map với hệ thống đa ngôn ngữ.
- Truy vết nhanh: Nhìn vào mã 6 chữ số là khoanh vùng ngay lập tức vị trí và nguyên nhân.
- Code sạch: Tách biệt hoàn toàn tầng xử lý logic nghiệp vụ với tầng định dạng dữ liệu trả về mạng.

**Tiêu cực:**

- Chi phí quản trị: Bắt buộc duy trì từ điển mã lỗi
- thiết lập ban đầu: Phải tự viết các bộ ánh xạ để dịch toàn bộ các lỗi mặc định của framework / thư viện sang chuẩn 6
  chữ số của dự án.