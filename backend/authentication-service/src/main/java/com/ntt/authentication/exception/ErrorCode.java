package com.ntt.authentication.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    UNAUTHENTICATED(1001, "Chưa xác thực", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED(1002, "Không có quyền truy cập", HttpStatus.FORBIDDEN),
    INVALID_CREDENTIALS(1003, "Email hoặc mật khẩu không đúng", HttpStatus.UNAUTHORIZED),
    ACCOUNT_DISABLED(1004, "Tài khoản đã bị vô hiệu hóa", HttpStatus.FORBIDDEN),
    ACCOUNT_LOCKED(1005, "Tài khoản đã bị khóa", HttpStatus.FORBIDDEN),
    USER_EXISTED(2001, "Người dùng đã tồn tại", HttpStatus.BAD_REQUEST),
    ROLE_NOT_FOUND(2002, "Không tìm thấy role", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND(2003, "Không tìm thấy người dùng", HttpStatus.NOT_FOUND),
    OLD_PASSWORD_INCORRECT(2004, "Mật khẩu cũ không chính xác", HttpStatus.BAD_REQUEST),
    FIELD_REQUIRED(3001, "{field} không được để trống", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID(3002, "Email không đúng định dạng", HttpStatus.BAD_REQUEST),
    PASSWORD_WEAK(3003, "Mật khẩu không đủ mạnh", HttpStatus.BAD_REQUEST),
    FIELD_SIZE_INVALID(3004, "{field} chỉ được tối đa {max} ký tự", HttpStatus.BAD_REQUEST),
    INVALID_JSON(3005, "Dữ liệu gửi lên sai định dạng JSON", HttpStatus.BAD_REQUEST),
    TYPE_MISMATCH(3006, "Tham số {field} sai kiểu dữ liệu", HttpStatus.BAD_REQUEST),
    ENDPOINT_NOT_FOUND(4001, "Đường dẫn không tồn tại", HttpStatus.NOT_FOUND),
    METHOD_NOT_ALLOWED(4002, "Phương thức không được hỗ trợ", HttpStatus.METHOD_NOT_ALLOWED),
    INTERNAL_SERVER_ERROR(5001, "Đã có lỗi xảy ra, vui lòng thử lại sau", HttpStatus.INTERNAL_SERVER_ERROR),
    SERVICE_UNAVAILABLE(5002, "Dịch vụ tạm thời không khả dụng", HttpStatus.SERVICE_UNAVAILABLE),
    ;
    private final Integer code;
    private final String message;
    private final HttpStatusCode statusCode;
}
