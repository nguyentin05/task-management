package com.ntt.task_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    UNAUTHENTICATED(1001, "Chưa xác thực", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED(1002, "Không có quyền truy cập", HttpStatus.FORBIDDEN),
    FIELD_REQUIRED(3001, "Trường dữ liệu không được để trống", HttpStatus.BAD_REQUEST),
    FIELD_SIZE_INVALID(3002, "Dữ liệu vượt quá độ dài cho phép", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID(3003, "Email không đúng định dạng", HttpStatus.BAD_REQUEST),
    LABEL_INVALID(3004, "Nhãn không hợp lệ", HttpStatus.BAD_REQUEST),
    PROJECT_ROLE_INVALID(3005, "Role trong dự án không hợp lệ", HttpStatus.BAD_REQUEST),
    TIME_INVALID(3006, "Thời gian kết thúc phải sau thời gian bắt đầu", HttpStatus.BAD_REQUEST),
    INVALID_JSON(3007, "Dữ liệu gửi lên sai định dạng JSON", HttpStatus.BAD_REQUEST),
    TYPE_MISMATCH(3008, "Tham số sai kiểu dữ liệu", HttpStatus.BAD_REQUEST),
    ENDPOINT_NOT_FOUND(4001, "Đường dẫn không tồn tại", HttpStatus.NOT_FOUND),
    METHOD_NOT_ALLOWED(4002, "Phương thức không được hỗ trợ", HttpStatus.METHOD_NOT_ALLOWED),
    WORKSPACE_NOT_FOUND(6001, "Không tìm thấy Workspace", HttpStatus.NOT_FOUND),
    PROJECT_NOT_FOUND(6002, "Không tìm thấy dự án", HttpStatus.NOT_FOUND),
    PROJECT_NOT_IN_WORKSPACE(6003, "Dự án không thuộc Workspace này", HttpStatus.BAD_REQUEST),
    USER_ALREADY_IN_PROJECT(6004, "Người dùng đã là thành viên của dự án", HttpStatus.BAD_REQUEST),
    USER_NOT_IN_PROJECT(6005, "Người dùng không thuộc dự án này", HttpStatus.NOT_FOUND),
    CANNOT_REMOVE_YOURSELF(6006, "Không thể xóa chính mình khỏi dự án", HttpStatus.BAD_REQUEST),
    CANNOT_REMOVE_PROJECT_OWNER(6007, "Không thể xóa người tạo dự án", HttpStatus.BAD_REQUEST),
    COLUMN_NOT_FOUND(7001, "Không tìm thấy cột", HttpStatus.NOT_FOUND),
    COLUMN_NOT_IN_PROJECT(7002, "Cột không thuộc dự án này", HttpStatus.BAD_REQUEST),
    TASK_NOT_FOUND(7003, "Không tìm thấy task", HttpStatus.NOT_FOUND),
    TASK_NOT_IN_COLUMN(7004, "Task không thuộc cột này", HttpStatus.BAD_REQUEST),
    USER_NOT_ASSIGNED(7005, "Người dùng không được gán vào task này", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR(5001, "Đã có lỗi xảy ra, vui lòng thử lại sau", HttpStatus.INTERNAL_SERVER_ERROR),
    SERVICE_UNAVAILABLE(5002, "Dịch vụ tạm thời không khả dụng", HttpStatus.SERVICE_UNAVAILABLE);
    ;
    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
