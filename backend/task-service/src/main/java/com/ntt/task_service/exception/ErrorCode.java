package com.ntt.task_service.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    // authentication & security
    EXPIRED_JWT(1001, "Token đã hết hạn", HttpStatus.UNAUTHORIZED),
    INVALID_JWT(1002, "Token không hợp lệ", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED(1003, "Bạn không có quyền truy cập tài nguyên này", HttpStatus.FORBIDDEN),
    UNAUTHENTICATED(1004, "Chưa chứng thực", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1005, "Không có quyền", HttpStatus.FORBIDDEN),
    //  validation
    FIELD_REQUIRED(2001, "{field} không được để trống", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID(2002, "Email không đúng định dạng", HttpStatus.BAD_REQUEST),
    PASSWORD_WEAK(2003, "Mật khẩu quá yếu", HttpStatus.BAD_REQUEST),
    // input data
    INVALID_JSON(3001, "Dữ liệu gửi lên sai định dạng JSON", HttpStatus.BAD_REQUEST),
    TYPE_MISMATCH(3002, "Tham số \"{field}\" sai kiểu dữ liệu", HttpStatus.BAD_REQUEST),
    // url
    NOT_FOUND_URL(4001, "Đường dẫn không tồn tại", HttpStatus.NOT_FOUND),
    INVALID_METHOD_URL(4002, "Phương thức không được hỗ trợ", HttpStatus.METHOD_NOT_ALLOWED),
    // server & db
    UNCATEGORIZED(5001, "Lỗi chưa được phân loại", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(5002, "Lỗi không đúng key", HttpStatus.INTERNAL_SERVER_ERROR),
    DB_CONSTRAINT_VIOLATION(5003, "Lỗi ràng buộc dữ liệu", HttpStatus.BAD_REQUEST),
    // auth - service
    USER_EXISTED(6001, "Người dùng đã tồn tại", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(6002, "Không tìm thấy người dùng", HttpStatus.NOT_FOUND),
    INVALID_OTP(6003, "Mã OTP không hợp lệ hoặc đã hết hạn", HttpStatus.BAD_REQUEST),
    // profile - service
    // task - service
    WORKSPACE_NOT_FOUND(8001, "Không tìm thấy Workspace", HttpStatus.BAD_REQUEST),
    ;
    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}