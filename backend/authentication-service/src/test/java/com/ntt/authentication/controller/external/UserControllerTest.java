package com.ntt.authentication.controller.external;

import static org.hamcrest.Matchers.either;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ntt.authentication.configuration.CustomJwtDecoder;
import com.ntt.authentication.configuration.SecurityConfig;
import com.ntt.authentication.dto.request.*;
import com.ntt.authentication.dto.response.PageResponse;
import com.ntt.authentication.dto.response.RoleResponse;
import com.ntt.authentication.dto.response.UserResponse;
import com.ntt.authentication.service.UserService;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    UserService userService;

    @MockitoBean
    CustomJwtDecoder customJwtDecoder;

    @MockitoBean
    UserDetailsService userDetailsService;

    @Test
    @DisplayName("Register - Success: đăng ký thành công, trả về UserResponse")
    void register_ValidRequest_ShouldReturnUserResponse() throws Exception {
        UserRegisterRequest request = UserRegisterRequest.builder()
                .email("test@example.com")
                .password("Password123@")
                .firstName("test")
                .lastName("test")
                .build();

        UserResponse mockResponse = UserResponse.builder()
                .id("user-uuid-1234")
                .email("test@example.com")
                .roles(Set.of(new RoleResponse("USER", "Người dùng")))
                .build();

        when(userService.register(any(UserRegisterRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/auth/users/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value("user-uuid-1234"))
                .andExpect(jsonPath("$.result.email").value("test@example.com"))
                .andExpect(jsonPath("$.result.roles[0].name").value("USER"))
                .andExpect(jsonPath("$.result.roles[0].description").value("Người dùng"));
    }

    static Stream<Arguments> provideInvalidRegisterRequests() {
        String validEmail = "test@example.com";
        String validPassword = "Password123@";
        String validFirstName = "test";
        String validLastName = "test";
        String tooLongString = "test".repeat(64);

        return Stream.of(
                Arguments.of(
                        UserRegisterRequest.builder()
                                .email(null)
                                .password(validPassword)
                                .firstName(validFirstName)
                                .lastName(validLastName)
                                .build(),
                        3001),
                Arguments.of(
                        UserRegisterRequest.builder()
                                .email("invalid-email")
                                .password(validPassword)
                                .firstName(validFirstName)
                                .lastName(validLastName)
                                .build(),
                        3002),
                Arguments.of(
                        UserRegisterRequest.builder()
                                .email(validEmail)
                                .password(null)
                                .firstName(validFirstName)
                                .lastName(validLastName)
                                .build(),
                        3001),
                Arguments.of(
                        UserRegisterRequest.builder()
                                .email(validEmail)
                                .password("weakpass")
                                .firstName(validFirstName)
                                .lastName(validLastName)
                                .build(),
                        3003),
                Arguments.of(
                        UserRegisterRequest.builder()
                                .email(validEmail)
                                .password(validPassword + tooLongString)
                                .firstName(validFirstName)
                                .lastName(validLastName)
                                .build(),
                        3004),
                Arguments.of(
                        UserRegisterRequest.builder()
                                .email(validEmail)
                                .password(validPassword)
                                .firstName(null)
                                .lastName(validLastName)
                                .build(),
                        3001),
                Arguments.of(
                        UserRegisterRequest.builder()
                                .email(validEmail)
                                .password(validPassword)
                                .firstName(tooLongString)
                                .lastName(validLastName)
                                .build(),
                        3004),
                Arguments.of(
                        UserRegisterRequest.builder()
                                .email(validEmail)
                                .password(validPassword)
                                .firstName(validFirstName)
                                .lastName(null)
                                .build(),
                        3001),
                Arguments.of(
                        UserRegisterRequest.builder()
                                .email(validEmail)
                                .password(validPassword)
                                .firstName(validFirstName)
                                .lastName(tooLongString)
                                .build(),
                        3004));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidRegisterRequests")
    @DisplayName("Register - Fail: bắt lỗi validation, trả về bad request")
    void register_InvalidRequest_ShouldReturnBadRequest(UserRegisterRequest invalidRequest, int errorCode)
            throws Exception {

        mockMvc.perform(post("/auth/users/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(errorCode));
    }

    @Test
    @DisplayName("Register - Fail: email quá dài, trả về bad request")
    void register_EmailTooLong_ShouldReturnBadRequest() throws Exception {
        String tooLongString = "test".repeat(64);

        UserRegisterRequest request = UserRegisterRequest.builder()
                .email(tooLongString + "@gmail.com")
                .password("Password123@")
                .firstName("test")
                .lastName("test")
                .build();

        mockMvc.perform(post("/auth/users/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(either(is(3002)).or(is(3004))));
    }

    @Test
    @DisplayName("Create - Success: admin tạo user mới thành công, trả về UserResponse")
    @WithMockUser(roles = "ADMIN")
    void create_AdminRole_ValidRequest_ShouldReturnUserResponse() throws Exception {
        UserCreationRequest request = UserCreationRequest.builder()
                .email("test@example.com")
                .password("123456")
                .firstName("test")
                .lastName("test")
                .roles(Set.of("USER"))
                .build();

        UserResponse mockResponse = UserResponse.builder()
                .id("new-uuid-999")
                .email("test@example.com")
                .roles(Set.of(new RoleResponse("USER", "Người dùng")))
                .build();

        when(userService.create(any(UserCreationRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/auth/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value("new-uuid-999"))
                .andExpect(jsonPath("$.result.email").value("test@example.com"))
                .andExpect(jsonPath("$.result.roles[0].name").value("USER"))
                .andExpect(jsonPath("$.result.roles[0].description").value("Người dùng"));
    }

    @Test
    @DisplayName("Create - Fail: bị chặn khi không có quyền admin, trả về forbidden")
    @WithMockUser(roles = "USER")
    void create_UserRole_ShouldReturnForbidden() throws Exception {
        UserCreationRequest request = UserCreationRequest.builder()
                .email("test@example.com")
                .password("123456")
                .firstName("test")
                .lastName("test")
                .roles(Set.of("USER"))
                .build();

        mockMvc.perform(post("/auth/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    static Stream<Arguments> provideInvalidCreationRequests() {
        String validEmail = "test@example.com";
        String validPassword = "123456";
        String validFirstName = "test";
        String validLastName = "test";
        Set<String> validRoles = Set.of("USER");
        String tooLongString = "test".repeat(64);

        return Stream.of(
                Arguments.of(
                        UserCreationRequest.builder()
                                .email(null)
                                .password(validPassword)
                                .firstName(validFirstName)
                                .lastName(validLastName)
                                .roles(validRoles)
                                .build(),
                        3001),
                Arguments.of(
                        UserCreationRequest.builder()
                                .email("invalid-email")
                                .password(validPassword)
                                .firstName(validFirstName)
                                .lastName(validLastName)
                                .roles(validRoles)
                                .build(),
                        3002),
                Arguments.of(
                        UserCreationRequest.builder()
                                .email(validEmail)
                                .password(tooLongString)
                                .firstName(validFirstName)
                                .lastName(validLastName)
                                .roles(validRoles)
                                .build(),
                        3004),
                Arguments.of(
                        UserCreationRequest.builder()
                                .email(validEmail)
                                .password(null)
                                .firstName(validFirstName)
                                .lastName(validLastName)
                                .roles(validRoles)
                                .build(),
                        3001),
                Arguments.of(
                        UserCreationRequest.builder()
                                .email(validEmail)
                                .password(validPassword)
                                .firstName(null)
                                .lastName(validLastName)
                                .roles(validRoles)
                                .build(),
                        3001),
                Arguments.of(
                        UserCreationRequest.builder()
                                .email(validEmail)
                                .password(validPassword)
                                .firstName(tooLongString)
                                .lastName(validLastName)
                                .roles(validRoles)
                                .build(),
                        3004),
                Arguments.of(
                        UserCreationRequest.builder()
                                .email(validEmail)
                                .password(validPassword)
                                .firstName(validFirstName)
                                .lastName(null)
                                .roles(validRoles)
                                .build(),
                        3001),
                Arguments.of(
                        UserCreationRequest.builder()
                                .email(validEmail)
                                .password(validPassword)
                                .firstName(validFirstName)
                                .lastName(tooLongString)
                                .roles(validRoles)
                                .build(),
                        3004));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidCreationRequests")
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Create - Fail: bắt lỗi validation, trả về bad request")
    void create_InvalidRequest_ShouldReturnBadRequest(UserCreationRequest invalidRequest, int errorCode)
            throws Exception {
        mockMvc.perform(post("/auth/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(errorCode));

        verify(userService, never()).create(any(UserCreationRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Create - Fail: email quá dài, trả về bad request")
    void create_EmailTooLong_ShouldReturnBadRequest() throws Exception {
        String tooLongString = "test".repeat(64);

        UserRegisterRequest request = UserRegisterRequest.builder()
                .email(tooLongString + "@gmail.com")
                .password("Password123@")
                .firstName("test")
                .lastName("test")
                .build();

        mockMvc.perform(post("/auth/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(either(is(3002)).or(is(3004))));
    }

    @Test
    @DisplayName("Get All - Success: admin lấy danh sách user thành công, trả về PageResponse")
    @WithMockUser(roles = "ADMIN")
    void getAll_AdminRole_ShouldReturnPageResponse() throws Exception {
        PageResponse<UserResponse> mockResponse = PageResponse.<UserResponse>builder()
                .currentPage(1)
                .pageSize(20)
                .totalPages(1)
                .totalElements(2)
                .data(List.of(
                        UserResponse.builder()
                                .id("uuid-1")
                                .email("test1@example.com")
                                .build(),
                        UserResponse.builder()
                                .id("uuid-2")
                                .email("test2@example.com")
                                .build()))
                .build();

        when(userService.getAllUser(1, 20)).thenReturn(mockResponse);

        mockMvc.perform(get("/auth/users").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.currentPage").value(1))
                .andExpect(jsonPath("$.result.totalElements").value(2))
                .andExpect(jsonPath("$.result.data").isArray())
                .andExpect(jsonPath("$.result.data.size()").value(2))
                .andExpect(jsonPath("$.result.data[0].id").value("uuid-1"))
                .andExpect(jsonPath("$.result.data[1].id").value("uuid-2"));

        verify(userService, times(1)).getAllUser(1, 20);
    }

    @Test
    @DisplayName("Get All - Fail: bị chặn khi không có quyền admin, trả về forbidden")
    @WithMockUser(roles = "USER")
    void getAll_UserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/auth/users").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(userService, never()).getAllUser(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Get Detail - Success: admin lấy chi tiết user thành công, trả về UserResponse")
    @WithMockUser(roles = "ADMIN")
    void getDetail_AdminRole_ValidUserId_ShouldReturnUserResponse() throws Exception {
        String userId = "uuid-1234";
        UserResponse mockResponse =
                UserResponse.builder().id(userId).email("test@example.com").build();

        when(userService.getDetail(userId)).thenReturn(mockResponse);

        mockMvc.perform(get("/auth/users/{userId}", userId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value(userId))
                .andExpect(jsonPath("$.result.email").value("test@example.com"));

        verify(userService, times(1)).getDetail(userId);
    }

    @Test
    @DisplayName("Get Detail - Fail: bị chặn khi không có quyền admin, trả về forbidden")
    @WithMockUser(roles = "USER")
    void getDetail_UserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/auth/users/{userId}", "uuid-1234").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(userService, never()).getDetail(any());
    }

    @Test
    @DisplayName("Reset Password - Success: admin đặt lại mật khẩu thành công, trả về message")
    @WithMockUser(roles = "ADMIN")
    void resetPassword_AdminRole_ValidRequest_ShouldReturnSuccessMessage() throws Exception {
        String userId = "uuid-1234";
        PasswordResetRequest request =
                PasswordResetRequest.builder().newPassword("Password123@").build();

        doNothing().when(userService).resetPassword(eq(userId), any(PasswordResetRequest.class));

        mockMvc.perform(put("/auth/users/{userId}/reset-password", userId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Đặt lại mật khẩu cho người dùng thành công"));

        verify(userService, times(1)).resetPassword(eq(userId), any(PasswordResetRequest.class));
    }

    @Test
    @DisplayName("Reset Password - Fail: bị chặn khi không có quyền admin, trả về forbidden")
    @WithMockUser(roles = "USER")
    void resetPassword_UserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(put("/auth/users/{userId}/reset-password", "uuid-1234")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(PasswordResetRequest.builder()
                                .newPassword("Password123@")
                                .build())))
                .andExpect(status().isForbidden());

        verify(userService, never()).resetPassword(any(), any());
    }

    static Stream<Arguments> provideInvalidResetPasswordRequests() {
        String tooLongString = "test".repeat(64);

        return Stream.of(
                Arguments.of(PasswordResetRequest.builder().newPassword(null).build(), 3001),
                Arguments.of(
                        PasswordResetRequest.builder().newPassword("weakpass").build(), 3003),
                Arguments.of(
                        PasswordResetRequest.builder()
                                .newPassword("Password123@" + tooLongString)
                                .build(),
                        3004));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidResetPasswordRequests")
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Reset Password - Fail: bắt lỗi validation, trả về bad request")
    void resetPassword_InvalidRequest_ShouldReturnBadRequest(PasswordResetRequest invalidRequest, int errorCode)
            throws Exception {
        mockMvc.perform(put("/auth/users/{userId}/reset-password", "uuid-1234")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(errorCode));

        verify(userService, never()).resetPassword(any(), any());
    }

    @Test
    @DisplayName("Update Roles - Success: admin cập nhật role thành công, trả về UserResponse")
    @WithMockUser(roles = "ADMIN")
    void updateRoles_AdminRole_ValidRequest_ShouldReturnUserResponse() throws Exception {
        String userId = "uuid-1234";
        RoleUpdateRequest request =
                RoleUpdateRequest.builder().roles(Set.of("USER", "ADMIN")).build();

        UserResponse mockResponse = UserResponse.builder()
                .id(userId)
                .email("test@example.com")
                .roles(Set.of(new RoleResponse("ADMIN", "Quản trị viên"), new RoleResponse("USER", "Người dùng")))
                .build();

        when(userService.updateRoles(eq(userId), any(RoleUpdateRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(put("/auth/users/{userId}/roles", userId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value(userId))
                .andExpect(jsonPath("$.result.roles").isArray());

        verify(userService, times(1)).updateRoles(eq(userId), any(RoleUpdateRequest.class));
    }

    @Test
    @DisplayName("Update Roles - Fail: bị chặn khi không có quyền admin, trả về forbidden")
    @WithMockUser(roles = "USER")
    void updateRoles_UserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(put("/auth/users/{userId}/roles", "uuid-1234")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(RoleUpdateRequest.builder()
                                .roles(Set.of("USER"))
                                .build())))
                .andExpect(status().isForbidden());

        verify(userService, never()).updateRoles(any(), any());
    }

    @Test
    @DisplayName("Update Roles - Fail: roles rỗng bắt lỗi validation, trả về bad request")
    @WithMockUser(roles = "ADMIN")
    void updateRoles_EmptyRoles_ShouldReturnBadRequest() throws Exception {
        RoleUpdateRequest request = RoleUpdateRequest.builder().roles(Set.of()).build();

        mockMvc.perform(put("/auth/users/{userId}/roles", "uuid-1234")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(3001));

        verify(userService, never()).updateRoles(any(), any());
    }

    @Test
    @DisplayName("Get My Info - Success: lấy thông tin bản thân thành công, trả về UserResponse")
    @WithMockUser(username = "test@example.com", roles = "USER")
    void getMyInfo_Authenticated_ShouldReturnUserResponse() throws Exception {
        UserResponse mockResponse =
                UserResponse.builder().id("uuid-1234").email("test@example.com").build();

        when(userService.getMyInfo()).thenReturn(mockResponse);

        mockMvc.perform(get("/auth/users/me").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value("uuid-1234"))
                .andExpect(jsonPath("$.result.email").value("test@example.com"));

        verify(userService, times(1)).getMyInfo();
    }

    @Test
    @DisplayName("Change Password - Success: đổi mật khẩu thành công, trả về message")
    @WithMockUser(roles = "USER")
    void changePassword_ValidRequest_ShouldReturnSuccessMessage() throws Exception {
        PasswordChangeRequest request = PasswordChangeRequest.builder()
                .oldPassword("OldPassword123@")
                .newPassword("Password123@")
                .build();

        doNothing().when(userService).changePassword(any(PasswordChangeRequest.class));

        mockMvc.perform(put("/auth/users/me/change-password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Cập nhật mật khẩu thành công"));

        verify(userService, times(1)).changePassword(any(PasswordChangeRequest.class));
    }

    static Stream<Arguments> provideInvalidChangePasswordRequests() {
        String tooLongString = "test".repeat(64);

        return Stream.of(
                Arguments.of(
                        PasswordChangeRequest.builder()
                                .oldPassword(null)
                                .newPassword("Password123@")
                                .build(),
                        3001),
                Arguments.of(
                        PasswordChangeRequest.builder()
                                .oldPassword("OldPassword123@")
                                .newPassword(null)
                                .build(),
                        3001),
                Arguments.of(
                        PasswordChangeRequest.builder()
                                .oldPassword("OldPassword123@")
                                .newPassword("weakpass")
                                .build(),
                        3003),
                Arguments.of(
                        PasswordChangeRequest.builder()
                                .oldPassword("OldPassword123@")
                                .newPassword("Password123@" + tooLongString)
                                .build(),
                        3004));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidChangePasswordRequests")
    @WithMockUser(roles = "USER")
    @DisplayName("Change Password - Fail: bắt lỗi validation, trả về bad request")
    void changePassword_InvalidRequest_ShouldReturnBadRequest(PasswordChangeRequest invalidRequest, int errorCode)
            throws Exception {
        mockMvc.perform(put("/auth/users/me/change-password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(errorCode));

        verify(userService, never()).changePassword(any());
    }

    @Test
    @DisplayName("Delete - Success: admin xóa user thành công, trả về message")
    @WithMockUser(roles = "ADMIN")
    void delete_AdminRole_ValidUserId_ShouldReturnSuccessMessage() throws Exception {
        String userId = "uuid-1234";

        doNothing().when(userService).delete(userId);

        mockMvc.perform(delete("/auth/users/{userId}", userId).with(csrf()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Xóa user thành công"));

        verify(userService, times(1)).delete(userId);
    }

    @Test
    @DisplayName("Delete - Fail: bị chặn khi không có quyền admin, trả về forbidden")
    @WithMockUser(roles = "USER")
    void delete_UserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(delete("/auth/users/{userId}", "uuid-1234")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(userService, never()).delete(any());
    }
}
