export const ErrorCodes = {
  SUCCESS: { code: 1000, message: "Thành công" },

  UNAUTHENTICATED:      { code: 1001, message: "Chưa xác thực" },
  ACCESS_DENIED:        { code: 1002, message: "Không có quyền truy cập" },
  INVALID_CREDENTIALS:  { code: 1003, message: "Email hoặc mật khẩu không đúng" },
  ACCOUNT_DISABLED:     { code: 1004, message: "Tài khoản đã bị vô hiệu hóa" },
  ACCOUNT_LOCKED:       { code: 1005, message: "Tài khoản đã bị khóa" },

  USER_EXISTED:         { code: 2001, message: "Người dùng đã tồn tại" },
  ROLE_NOT_FOUND:       { code: 2002, message: "Không tìm thấy role" },
  USER_NOT_FOUND:       { code: 2003, message: "Không tìm thấy người dùng" },
  OLD_PASSWORD_INCORRECT: { code: 2004, message: "Mật khẩu cũ không chính xác" },
  PROFILE_EXISTED:      { code: 2005, message: "Hồ sơ đã tồn tại" },
  PROFILE_NOT_FOUND:    { code: 2006, message: "Không tìm thấy hồ sơ" },

  FIELD_REQUIRED:       { code: 3001, message: (field) => `${field} không được để trống` },
  EMAIL_INVALID:        { code: 3002, message: "Email không đúng định dạng" },
  PASSWORD_WEAK:        { code: 3003, message: "Mật khẩu không đủ mạnh" },
  FIELD_SIZE_INVALID:   { code: 3004, message: (field, max) => `${field} chỉ được tối đa ${max} ký tự` },
  INVALID_JSON:         { code: 3005, message: "Dữ liệu gửi lên sai định dạng JSON" },
  TYPE_MISMATCH:        { code: 3006, message: (field) => `Tham số ${field} sai kiểu dữ liệu` },
  DOB_INVALID:          { code: 3007, message: (min) => `Tuổi của bạn phải lớn hơn ${min}` },
  PHONE_INVALID:        { code: 3008, message: "Số điện thoại không hợp lệ" },
  TIME_INVALID:         { code: 3009, message: "Thời gian kết thúc phải sau thời gian bắt đầu" },
  TIME_IN_PAST:         { code: 3010, message: "Thời gian không được ở quá khứ" },
  LABEL_INVALID:        { code: 3011, message: "Nhãn không hợp lệ" },
  PROJECT_ROLE_INVALID: { code: 3012, message: "Quyền trong dự án không hợp lệ" },
  POSITION_INVALID:     { code: 3013, message: "Vị trí không hợp lệ" },
  FILE_INVALID:         { code: 3014, message: "File không hợp lệ" },

  ENDPOINT_NOT_FOUND:   { code: 4001, message: "Đường dẫn không tồn tại" },
  METHOD_NOT_ALLOWED:   { code: 4002, message: "Phương thức không được hỗ trợ" },

  INTERNAL_SERVER_ERROR: { code: 5001, message: "Đã có lỗi xảy ra, vui lòng thử lại sau" },
  SERVICE_UNAVAILABLE:   { code: 5002, message: "Dịch vụ tạm thời không khả dụng" },

  WORKSPACE_NOT_FOUND:        { code: 6001, message: "Không tìm thấy Workspace" },
  PROJECT_NOT_FOUND:          { code: 6002, message: "Không tìm thấy dự án" },
  PROJECT_NOT_IN_WORKSPACE:   { code: 6003, message: "Dự án không thuộc Workspace này" },
  USER_ALREADY_IN_PROJECT:    { code: 6004, message: "Người dùng đã là thành viên của dự án" },
  USER_NOT_IN_PROJECT:        { code: 6005, message: "Người dùng không thuộc dự án này" },
  CANNOT_REMOVE_YOURSELF:     { code: 6006, message: "Không thể xóa chính mình khỏi dự án" },
  CANNOT_REMOVE_PROJECT_OWNER:{ code: 6007, message: "Không thể xóa người tạo dự án" },

  COLUMN_NOT_FOUND:    { code: 7001, message: "Không tìm thấy cột" },
  COLUMN_NOT_IN_PROJECT:{ code: 7002, message: "Cột không thuộc dự án này" },
  TASK_NOT_FOUND:      { code: 7003, message: "Không tìm thấy task" },
  TASK_NOT_IN_COLUMN:  { code: 7004, message: "Task không thuộc cột này" },
  USER_NOT_ASSIGNED:   { code: 7005, message: "Người dùng không được gán vào task này" },

  CANNOT_SEND_EMAIL:   { code: 9001, message: "Không thể gửi mail" },
};

const codeMap = Object.fromEntries(
  Object.values(ErrorCodes).map((e) => [e.code, e])
);

export const getErrorMessage = (error, fallback = "Đã có lỗi xảy ra") => {
  const code = error?.response?.data?.code;
  if (!code) return fallback;

  const found = codeMap[code];
  if (!found) return fallback;

  if (typeof found.message === "function") return fallback;

  return found.message;
};