package com.ntt.profile_service.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    UNAUTHENTICATED(1001, "Chưa xác thực", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED(1002, "Không có quyền truy cập", HttpStatus.FORBIDDEN),
    PROFILE_EXISTED(2005, "Hồ sơ đã tồn tại", HttpStatus.BAD_REQUEST),
    PROFILE_NOT_FOUND(2006, "Không tìm thấy hồ sơ", HttpStatus.NOT_FOUND),
    FIELD_REQUIRED(3001, "{field} không được để trống", HttpStatus.BAD_REQUEST),
    FIELD_SIZE_INVALID(3004, "{field} chỉ được tối đa {max} ký tự", HttpStatus.BAD_REQUEST),
    DOB_INVALID(3007, "Tuổi của bạn phải lớn hơn {min}", HttpStatus.BAD_REQUEST),
    PHONE_INVALID(3008, "Số điện thoại không hợp lệ", HttpStatus.BAD_REQUEST),
    INVALID_JSON(3005, "Dữ liệu gửi lên sai định dạng JSON", HttpStatus.BAD_REQUEST),
    TYPE_MISMATCH(3006, "Tham số {field} sai kiểu dữ liệu", HttpStatus.BAD_REQUEST),
    ENDPOINT_NOT_FOUND(4001, "Đường dẫn không tồn tại", HttpStatus.NOT_FOUND),
    METHOD_NOT_ALLOWED(4002, "Phương thức không được hỗ trợ", HttpStatus.METHOD_NOT_ALLOWED),
    INTERNAL_SERVER_ERROR(5001, "Đã có lỗi xảy ra, vui lòng thử lại sau", HttpStatus.INTERNAL_SERVER_ERROR),
    SERVICE_UNAVAILABLE(5002, "Dịch vụ tạm thời không khả dụng", HttpStatus.SERVICE_UNAVAILABLE),
    ;
    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}