package com.ntt.comment_service.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

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
    PROFILE_EXISTED(2005, "Hồ sơ đã tồn tại", HttpStatus.BAD_REQUEST),
    PROFILE_NOT_FOUND(2006, "Không tìm thấy hồ sơ", HttpStatus.NOT_FOUND),

    FIELD_REQUIRED(3001, "{field} không được để trống", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID(3002, "Email không đúng định dạng", HttpStatus.BAD_REQUEST),
    PASSWORD_WEAK(3003, "Mật khẩu không đủ mạnh", HttpStatus.BAD_REQUEST),
    FIELD_SIZE_INVALID(3004, "{field} chỉ được tối đa {max} ký tự", HttpStatus.BAD_REQUEST),
    INVALID_JSON(3005, "Dữ liệu gửi lên sai định dạng JSON", HttpStatus.BAD_REQUEST),
    TYPE_MISMATCH(3006, "Tham số {field} sai kiểu dữ liệu", HttpStatus.BAD_REQUEST),
    DOB_INVALID(3007, "Tuổi của bạn phải lớn hơn {min}", HttpStatus.BAD_REQUEST),
    PHONE_INVALID(3008, "Số điện thoại không hợp lệ", HttpStatus.BAD_REQUEST),
    TIME_INVALID(3009, "Thời gian kết thúc phải sau thời gian bắt đầu", HttpStatus.BAD_REQUEST),
    TIME_IN_PAST(3010, "Thời gian không được ở quá khứ", HttpStatus.BAD_REQUEST),
    LABEL_INVALID(3011, "Nhãn không hợp lệ", HttpStatus.BAD_REQUEST),
    PROJECT_ROLE_INVALID(3012, "Quyền trong dự án không hợp lệ", HttpStatus.BAD_REQUEST),
    POSITION_INVALID(3013, "Vị trí không hợp lệ", HttpStatus.BAD_REQUEST),
    FILE_INVALID(3014, "File không hợp lệ", HttpStatus.BAD_REQUEST),

    ENDPOINT_NOT_FOUND(4001, "Đường dẫn không tồn tại", HttpStatus.NOT_FOUND),
    METHOD_NOT_ALLOWED(4002, "Phương thức không được hỗ trợ", HttpStatus.METHOD_NOT_ALLOWED),

    INTERNAL_SERVER_ERROR(5001, "Đã có lỗi xảy ra, vui lòng thử lại sau", HttpStatus.INTERNAL_SERVER_ERROR),
    SERVICE_UNAVAILABLE(5002, "Dịch vụ tạm thời không khả dụng", HttpStatus.SERVICE_UNAVAILABLE),

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

    COMMENT_NOT_FOUND(7005, "Không tìm thấy bình luận", HttpStatus.BAD_REQUEST),
    ;
    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
