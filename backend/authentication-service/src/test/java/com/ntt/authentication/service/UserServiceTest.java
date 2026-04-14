package com.ntt.authentication.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ntt.authentication.constant.PredefinedRole;
import com.ntt.authentication.domain.OutboxEvent;
import com.ntt.authentication.domain.Role;
import com.ntt.authentication.domain.User;
import com.ntt.authentication.dto.request.*;
import com.ntt.authentication.dto.response.PageResponse;
import com.ntt.authentication.dto.response.UserResponse;
import com.ntt.authentication.dto.response.UserSearchResponse;
import com.ntt.authentication.exception.AppException;
import com.ntt.authentication.exception.ErrorCode;
import com.ntt.authentication.mapper.UserMapper;
import com.ntt.authentication.repository.OutboxEventRepository;
import com.ntt.authentication.repository.RoleRepository;
import com.ntt.authentication.repository.UserRepository;

import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserMapper userMapper;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    RoleRepository roleRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ObjectMapper objectMapper;

    @Mock
    OutboxEventRepository outboxEventRepository;

    @InjectMocks
    UserService userService;

    private User user;
    private Role userRole;

    @BeforeEach
    void setUpGlobal() {
        userRole = new Role();
        userRole.setName(PredefinedRole.USER_ROLE);

        user = User.builder()
                .id("uuid-1234")
                .email("test@example.com")
                .password("encodedPassword")
                .roles(Set.of(userRole))
                .build();
    }

    private void stubPublishEvent() {
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(outboxEventRepository.save(any())).thenReturn(mock(OutboxEvent.class));
    }

    private void mockSecurityContext(MockedStatic<SecurityContextHolder> mocked) {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        mocked.when(SecurityContextHolder::getContext).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
    }

    @Nested
    @DisplayName("Scenario - Fail: user không tồn tại")
    @MockitoSettings(strictness = Strictness.LENIENT)
    class UserNotFoundScenario {

        @BeforeEach
        void setupScenario() {
            when(userRepository.findById(anyString())).thenReturn(Optional.empty());
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        }

        @Test
        @DisplayName("Get Detail: trả về lỗi USER_NOT_FOUND")
        void getDetailTest() {
            assertThatThrownBy(() -> userService.getDetail("uuid-1234"))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);

            verify(userRepository, times(1)).findById("uuid-1234");
        }

        @Test
        @DisplayName("Reset Password: trả về lỗi USER_NOT_FOUND")
        void resetPasswordTest() {
            PasswordResetRequest request =
                    PasswordResetRequest.builder().newPassword("Password123@").build();

            assertThatThrownBy(() -> userService.resetPassword("uuid-1234", request))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Update Roles: trả về lỗi USER_NOT_FOUND")
        void updateRolesTest() {
            RoleUpdateRequest request =
                    RoleUpdateRequest.builder().roles(Set.of("USER")).build();

            assertThatThrownBy(() -> userService.updateRoles("uuid-1234", request))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Delete: trả về lỗi USER_NOT_FOUND")
        void deleteTest() {
            when(userRepository.existsById(anyString())).thenReturn(false);

            assertThatThrownBy(() -> userService.delete("uuid-1234"))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);

            verify(userRepository, never()).deleteById(any());
        }

        @Test
        @DisplayName("Get My Info: trả về lỗi USER_NOT_FOUND")
        void getMyInfoTest() {
            try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
                mockSecurityContext(mocked);

                assertThatThrownBy(() -> userService.getMyInfo())
                        .isInstanceOf(AppException.class)
                        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
            }
        }

        @Test
        @DisplayName("Change Password: trả về lỗi USER_NOT_FOUND")
        void changePasswordTest() {
            try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
                mockSecurityContext(mocked);

                PasswordChangeRequest request = PasswordChangeRequest.builder()
                        .oldPassword("OldPassword123@")
                        .newPassword("Password123@")
                        .build();

                assertThatThrownBy(() -> userService.changePassword(request))
                        .isInstanceOf(AppException.class)
                        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);

                verify(userRepository, never()).save(any());
            }
        }
    }

    @Nested
    @DisplayName("Register: test hàm register")
    class RegisterTest {

        private UserRegisterRequest request;
        private UserResponse mockResponse;

        @BeforeEach
        void setUpRegister() {
            request = UserRegisterRequest.builder()
                    .email("test@example.com")
                    .password("Password123@")
                    .firstName("test")
                    .lastName("test")
                    .build();

            mockResponse = UserResponse.builder()
                    .id("uuid-1234")
                    .email("test@example.com")
                    .build();
        }

        @Test
        @DisplayName("Success: đăng ký thành công, trả về UserResponse")
        void register_ValidRequest_ShouldReturnUserResponse() {
            when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
            when(userMapper.toUser(request)).thenReturn(user);
            when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
            when(roleRepository.findById(PredefinedRole.USER_ROLE)).thenReturn(Optional.of(userRole));
            when(userRepository.save(any(User.class))).thenReturn(user);
            when(userMapper.toUserResponse(user)).thenReturn(mockResponse);
            stubPublishEvent();

            UserResponse response = userService.register(request);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo("uuid-1234");
            assertThat(response.getEmail()).isEqualTo("test@example.com");

            verify(userRepository, times(1)).save(any(User.class));
            verify(outboxEventRepository, times(1)).save(any());
        }

        @Test
        @DisplayName("Fail: email đã tồn tại, trả về lỗi USER_EXISTED")
        void register_DuplicateEmail_ShouldThrowUserExisted() {
            when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

            assertThatThrownBy(() -> userService.register(request))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_EXISTED);

            verify(userRepository, never()).save(any());
            verify(outboxEventRepository, never()).save(any());
        }

        @Test
        @DisplayName("Fail: race condition khi lưu DB, trả về lỗi USER_EXISTED")
        void register_DataIntegrityViolation_ShouldThrowUserExisted() {
            when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
            when(userMapper.toUser(request)).thenReturn(user);
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(roleRepository.findById(PredefinedRole.USER_ROLE)).thenReturn(Optional.of(userRole));
            when(userRepository.save(any(User.class))).thenThrow(new DataIntegrityViolationException("duplicate"));

            assertThatThrownBy(() -> userService.register(request))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_EXISTED);

            verify(outboxEventRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Create: test hàm create")
    class CreateTest {

        private UserResponse mockResponse;

        @BeforeEach
        void setUpCreate() {
            mockResponse = UserResponse.builder()
                    .id("uuid-1234")
                    .email("test@example.com")
                    .build();
        }

        @Test
        @DisplayName("Success: tạo user với roles được chỉ định, trả về UserResponse")
        void create_WithRoles_ShouldReturnUserResponse() {
            UserCreationRequest request = UserCreationRequest.builder()
                    .email("test@example.com")
                    .password("Password123@")
                    .firstName("test")
                    .lastName("test")
                    .roles(Set.of("USER"))
                    .build();

            when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
            when(userMapper.toUser(request)).thenReturn(user);
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(roleRepository.findAllById(request.getRoles())).thenReturn(List.of(userRole));
            when(userRepository.save(any(User.class))).thenReturn(user);
            when(userMapper.toUserResponse(user)).thenReturn(mockResponse);
            stubPublishEvent();

            UserResponse response = userService.create(request);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo("uuid-1234");

            verify(roleRepository, times(1)).findAllById(request.getRoles());
            verify(roleRepository, never()).findById(any());
            verify(outboxEventRepository, times(1)).save(any());
        }

        @Test
        @DisplayName("Success: tạo user không có roles, tự gán role USER mặc định, trả về UserResponse")
        void create_WithoutRoles_ShouldAssignDefaultUserRole() {
            UserCreationRequest request = UserCreationRequest.builder()
                    .email("test@example.com")
                    .password("Password123@")
                    .firstName("test")
                    .lastName("test")
                    .roles(null)
                    .build();

            when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
            when(userMapper.toUser(request)).thenReturn(user);
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(roleRepository.findById(PredefinedRole.USER_ROLE)).thenReturn(Optional.of(userRole));
            when(userRepository.save(any(User.class))).thenReturn(user);
            when(userMapper.toUserResponse(user)).thenReturn(mockResponse);
            stubPublishEvent();

            UserResponse response = userService.create(request);

            assertThat(response).isNotNull();

            verify(roleRepository, times(1)).findById(PredefinedRole.USER_ROLE);
            verify(roleRepository, never()).findAllById(any());
        }

        @Test
        @DisplayName("Fail: email đã tồn tại, trả về lỗi USER_EXISTED")
        void create_DuplicateEmail_ShouldThrowUserExisted() {
            UserCreationRequest request = UserCreationRequest.builder()
                    .email("test@example.com")
                    .password("Password123@")
                    .firstName("test")
                    .lastName("test")
                    .roles(Set.of("USER"))
                    .build();

            when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

            assertThatThrownBy(() -> userService.create(request))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_EXISTED);

            verify(userRepository, never()).save(any());
            verify(outboxEventRepository, never()).save(any());
        }

        @Test
        @DisplayName("Fail: race condition khi lưu DB, trả về lỗi USER_EXISTED")
        void create_DataIntegrityViolation_ShouldThrowUserExisted() {
            UserCreationRequest request = UserCreationRequest.builder()
                    .email("test@example.com")
                    .password("Password123@")
                    .firstName("test")
                    .lastName("test")
                    .roles(Set.of("USER"))
                    .build();

            when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
            when(userMapper.toUser(request)).thenReturn(user);
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(roleRepository.findAllById(any())).thenReturn(List.of(userRole));
            when(userRepository.save(any(User.class))).thenThrow(new DataIntegrityViolationException("duplicate"));

            assertThatThrownBy(() -> userService.create(request))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_EXISTED);

            verify(outboxEventRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Get All User: test hàm getAllUser")
    class GetAllUserTest {

        @Test
        @DisplayName("Success: lấy danh sách user thành công, trả về PageResponse")
        void getAllUser_ShouldReturnPageResponse() {
            User user2 =
                    User.builder().id("uuid-5678").email("test2@example.com").build();
            UserResponse response1 = UserResponse.builder().id("uuid-1234").build();
            UserResponse response2 = UserResponse.builder().id("uuid-5678").build();

            Page<User> mockPage = new PageImpl<>(List.of(user, user2), PageRequest.of(0, 20), 2);

            when(userRepository.findAll(any(Pageable.class))).thenReturn(mockPage);
            when(userMapper.toUserResponse(user)).thenReturn(response1);
            when(userMapper.toUserResponse(user2)).thenReturn(response2);

            PageResponse<UserResponse> result = userService.getAllUser(1, 20);

            assertThat(result.getCurrentPage()).isEqualTo(1);
            assertThat(result.getPageSize()).isEqualTo(20);
            assertThat(result.getTotalElements()).isEqualTo(2);
            assertThat(result.getData()).hasSize(2);
            assertThat(result.getData().get(0).getId()).isEqualTo("uuid-1234");
            assertThat(result.getData().get(1).getId()).isEqualTo("uuid-5678");

            verify(userRepository, times(1)).findAll(any(Pageable.class));
        }

        @Test
        @DisplayName("Success: không có user nào, trả về PageResponse rỗng")
        void getAllUser_NoUsers_ShouldReturnEmptyPageResponse() {
            Page<User> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);

            when(userRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

            PageResponse<UserResponse> result = userService.getAllUser(1, 20);

            assertThat(result.getData()).isEmpty();
            assertThat(result.getTotalElements()).isZero();
            assertThat(result.getTotalPages()).isZero();
        }
    }

    @Nested
    @DisplayName("Get Detail: test hàm getDetail")
    class GetDetailTest {

        @Test
        @DisplayName("Success: lấy chi tiết user thành công, trả về UserResponse")
        void getDetail_ValidId_ShouldReturnUserResponse() {
            UserResponse mockResponse = UserResponse.builder()
                    .id("uuid-1234")
                    .email("test@example.com")
                    .build();

            when(userRepository.findById("uuid-1234")).thenReturn(Optional.of(user));
            when(userMapper.toUserResponse(user)).thenReturn(mockResponse);

            UserResponse response = userService.getDetail("uuid-1234");

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo("uuid-1234");
            assertThat(response.getEmail()).isEqualTo("test@example.com");

            verify(userRepository, times(1)).findById("uuid-1234");
        }
    }

    @Nested
    @DisplayName("Reset Password: test hàm resetPassword")
    class ResetPasswordTest {

        @Test
        @DisplayName("Success: đặt lại mật khẩu thành công, password được mã hóa và lưu")
        void resetPassword_ValidRequest_ShouldEncodeAndSave() {
            PasswordResetRequest request =
                    PasswordResetRequest.builder().newPassword("Password123@").build();

            when(userRepository.findById("uuid-1234")).thenReturn(Optional.of(user));
            when(passwordEncoder.encode(request.getNewPassword())).thenReturn("newEncodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(user);

            assertThatCode(() -> userService.resetPassword("uuid-1234", request))
                    .doesNotThrowAnyException();

            verify(passwordEncoder, times(1)).encode("Password123@");
            verify(userRepository, times(1)).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Update Roles: test hàm updateRoles")
    class UpdateRolesTest {

        @Test
        @DisplayName("Success: cập nhật role thành công, trả về UserResponse")
        void updateRoles_ValidRoles_ShouldReturnUserResponse() {
            Role adminRole = new Role();
            adminRole.setName("ADMIN");

            RoleUpdateRequest request =
                    RoleUpdateRequest.builder().roles(Set.of("ADMIN")).build();

            UserResponse mockResponse = UserResponse.builder()
                    .id("uuid-1234")
                    .email("test@example.com")
                    .build();

            when(userRepository.findById("uuid-1234")).thenReturn(Optional.of(user));
            when(roleRepository.findAllById(request.getRoles())).thenReturn(List.of(adminRole));
            when(userRepository.save(any(User.class))).thenReturn(user);
            when(userMapper.toUserResponse(user)).thenReturn(mockResponse);

            UserResponse response = userService.updateRoles("uuid-1234", request);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo("uuid-1234");

            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        @DisplayName("Fail: role không tồn tại trong DB, trả về lỗi ROLE_NOT_FOUND")
        void updateRoles_RoleNotFound_ShouldThrowRoleNotFound() {
            RoleUpdateRequest request = RoleUpdateRequest.builder()
                    .roles(Set.of("ADMIN", "SUPER_ADMIN"))
                    .build();

            when(userRepository.findById("uuid-1234")).thenReturn(Optional.of(user));
            when(roleRepository.findAllById(request.getRoles())).thenReturn(List.of(userRole));

            assertThatThrownBy(() -> userService.updateRoles("uuid-1234", request))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ROLE_NOT_FOUND);

            verify(userRepository, never()).save(any());
        }
    }

    @Test
    @DisplayName("Success: lấy thông tin bản thân thành công")
    void getMyInfo_Authenticated_ShouldReturnUserResponse() {
        String mockIdFromSecurityContext = "test@example.com";

        UserResponse mockResponse = UserResponse.builder()
                .id("uuid-1234")
                .email(mockIdFromSecurityContext)
                .build();

        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mockSecurityContext(mocked);

            when(userRepository.findById(mockIdFromSecurityContext)).thenReturn(Optional.of(user));
            when(userMapper.toUserResponse(user)).thenReturn(mockResponse);

            UserResponse response = userService.getMyInfo();

            assertThat(response).isNotNull();
            verify(userRepository).findById(mockIdFromSecurityContext);
        }
    }

    @Nested
    @DisplayName("Change Password: test hàm changePassword")
    class ChangePasswordTest {

        private PasswordChangeRequest validRequest;

        @BeforeEach
        void setUpChangePassword() {
            validRequest = PasswordChangeRequest.builder()
                    .oldPassword("OldPassword123@")
                    .newPassword("Password123@")
                    .build();
        }

        @Test
        @DisplayName("Success: Đổi mật khẩu thành công")
        void changePassword_ValidRequest_ShouldEncodeAndSave() {
            try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
                mockSecurityContext(mocked);

                when(userRepository.findById("test@example.com")).thenReturn(Optional.of(user));

                when(passwordEncoder.matches(validRequest.getOldPassword(), user.getPassword()))
                        .thenReturn(true);
                when(passwordEncoder.encode(validRequest.getNewPassword())).thenReturn("newEncodedPassword");
                when(userRepository.save(any(User.class))).thenReturn(user);

                assertThatCode(() -> userService.changePassword(validRequest)).doesNotThrowAnyException();

                verify(userRepository, times(1)).findById("test@example.com");
                verify(userRepository, times(1)).save(any(User.class));
            }
        }

        @Test
        @DisplayName("Fail: mật khẩu cũ không đúng, trả về lỗi OLD_PASSWORD_INCORRECT")
        void changePassword_WrongOldPassword_ShouldThrowOldPasswordIncorrect() {
            try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
                mockSecurityContext(mocked);

                when(userRepository.findById("test@example.com")).thenReturn(Optional.of(user));

                when(passwordEncoder.matches(validRequest.getOldPassword(), user.getPassword()))
                        .thenReturn(false);

                assertThatThrownBy(() -> userService.changePassword(validRequest))
                        .isInstanceOf(AppException.class)
                        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.OLD_PASSWORD_INCORRECT);

                verify(userRepository, never()).save(any());

                verify(userRepository, times(1)).findById("test@example.com");
            }
        }
    }

    @Nested
    @DisplayName("Delete: test hàm delete")
    class DeleteTest {

        @Test
        @DisplayName("Success: xóa user thành công")
        void delete_ValidId_ShouldDeleteUser() {
            when(userRepository.existsById("uuid-1234")).thenReturn(true);
            doNothing().when(userRepository).deleteById("uuid-1234");

            assertThatCode(() -> userService.delete("uuid-1234")).doesNotThrowAnyException();

            verify(userRepository, times(1)).deleteById("uuid-1234");
        }
    }

    @Nested
    @DisplayName("Search By Email: test hàm searchByEmail")
    class SearchByEmailTest {

        @Test
        @DisplayName("Success: tìm kiếm user theo email thành công, trả về List<UserSearchResponse>")
        void searchByEmail_ValidKeyword_ShouldReturnList() {
            UserSearchResponse searchResponse = UserSearchResponse.builder()
                    .id("uuid-1234")
                    .email("test@example.com")
                    .build();

            when(userRepository.findTop10ByEmailContainingIgnoreCase("test")).thenReturn(List.of(user));
            when(userMapper.toUserSearchResponse(user)).thenReturn(searchResponse);

            List<UserSearchResponse> result = userService.searchByEmail("test");

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getEmail()).isEqualTo("test@example.com");

            verify(userRepository, times(1)).findTop10ByEmailContainingIgnoreCase("test");
        }

        @Test
        @DisplayName("Success: không tìm thấy user nào, trả về danh sách rỗng")
        void searchByEmail_NoMatch_ShouldReturnEmptyList() {
            when(userRepository.findTop10ByEmailContainingIgnoreCase("notfound"))
                    .thenReturn(List.of());

            List<UserSearchResponse> result = userService.searchByEmail("notfound");

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Search By UserIds: test hàm searchByUserIds")
    class SearchByUserIdsTest {

        @Test
        @DisplayName("Success: tìm kiếm user theo danh sách id thành công, trả về List<UserSearchResponse>")
        void searchByUserIds_ValidIds_ShouldReturnList() {
            User user2 =
                    User.builder().id("uuid-5678").email("user2@example.com").build();
            UserSearchResponse response1 = UserSearchResponse.builder()
                    .id("uuid-1234")
                    .email("test@example.com")
                    .build();
            UserSearchResponse response2 = UserSearchResponse.builder()
                    .id("uuid-5678")
                    .email("user2@example.com")
                    .build();

            when(userRepository.findAllById(List.of("uuid-1234", "uuid-5678"))).thenReturn(List.of(user, user2));
            when(userMapper.toUserSearchResponse(user)).thenReturn(response1);
            when(userMapper.toUserSearchResponse(user2)).thenReturn(response2);

            List<UserSearchResponse> result = userService.searchByUserIds(List.of("uuid-1234", "uuid-5678"));

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getId()).isEqualTo("uuid-1234");
            assertThat(result.get(1).getId()).isEqualTo("uuid-5678");

            verify(userRepository, times(1)).findAllById(List.of("uuid-1234", "uuid-5678"));
        }

        @Test
        @DisplayName("Success: danh sách id rỗng, trả về danh sách rỗng")
        void searchByUserIds_EmptyIds_ShouldReturnEmptyList() {
            when(userRepository.findAllById(List.of())).thenReturn(List.of());

            List<UserSearchResponse> result = userService.searchByUserIds(List.of());

            assertThat(result).isEmpty();
        }
    }
}
