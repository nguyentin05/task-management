package com.ntt.profile_service.controller.external;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ntt.profile_service.configuration.CustomJwtDecoder;
import com.ntt.profile_service.configuration.SecurityConfig;
import com.ntt.profile_service.dto.request.AvatarUpdateRequest;
import com.ntt.profile_service.dto.request.ProfileUpdateRequest;
import com.ntt.profile_service.dto.response.AvatarResponse;
import com.ntt.profile_service.dto.response.ProfileResponse;
import com.ntt.profile_service.service.ProfileService;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = ProfileController.class)
@Import(SecurityConfig.class)
class ProfileControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    ProfileService profileService;

    @MockitoBean
    CustomJwtDecoder customJwtDecoder;

    @MockitoBean
    UserDetailsService userDetailsService;

    private static final byte[] VALID_JPEG = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, 0x00, 0x00, 0x00};

    @Nested
    @DisplayName("Get My Profile: test hàm getMyProfile")
    class GetMyProfileTest {

        @Test
        @DisplayName("Get My Profile - Success: lấy thông tin profile bản thân thành công, trả về ProfileResponse")
        @WithMockUser(username = "test@example.com")
        void getMyProfile_Authenticated_ShouldReturnProfileResponse() throws Exception {
            ProfileResponse mockResponse = ProfileResponse.builder()
                    .id("profile-uuid-1234")
                    .firstName("test")
                    .lastName("test")
                    .build();

            when(profileService.getMyProfile()).thenReturn(mockResponse);

            mockMvc.perform(get("/profiles/me")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.id").value("profile-uuid-1234"))
                    .andExpect(jsonPath("$.result.firstName").value("test"));

            verify(profileService, times(1)).getMyProfile();
        }

        @Test
        @DisplayName("Get My Profile - Fail: bị chặn khi chưa xác thực, trả về unauthorized")
        void getMyProfile_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(get("/profiles/me")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());

            verify(profileService, never()).getMyProfile();
        }
    }

    @Nested
    @DisplayName("Update My Profile: test hàm updateMyProfile")
    class UpdateMyProfileTest {

        @Test
        @DisplayName("Update My Profile - Success: cập nhật profile bản thân thành công, trả về ProfileResponse")
        @WithMockUser(username = "test@example.com")
        void updateMyProfile_ValidRequest_ShouldReturnProfileResponse() throws Exception {
            ProfileUpdateRequest request = ProfileUpdateRequest.builder()
                    .firstName("test")
                    .lastName("test")
                    .dob(LocalDate.of(2000, 1, 1))
                    .phoneNumber("0901234567")
                    .build();

            ProfileResponse mockResponse = ProfileResponse.builder()
                    .id("profile-uuid-1234")
                    .firstName("test")
                    .lastName("test")
                    .build();

            when(profileService.updateMyProfile(any(ProfileUpdateRequest.class))).thenReturn(mockResponse);

            mockMvc.perform(patch("/profiles/me")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.id").value("profile-uuid-1234"))
                    .andExpect(jsonPath("$.result.firstName").value("test"));

            verify(profileService, times(1)).updateMyProfile(any(ProfileUpdateRequest.class));
        }

        @Test
        @DisplayName("Update My Profile - Fail: bị chặn khi chưa xác thực, trả về unauthorized")
        void updateMyProfile_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(patch("/profiles/me")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(ProfileUpdateRequest.builder().build())))
                    .andExpect(status().isUnauthorized());

            verify(profileService, never()).updateMyProfile(any());
        }

        static Stream<Arguments> provideInvalidUpdateProfileRequests() {
            String tooLongString = "test".repeat(64);

            return Stream.of(
                    Arguments.of(ProfileUpdateRequest.builder().firstName(tooLongString).build(), 3004),
                    Arguments.of(ProfileUpdateRequest.builder().lastName(tooLongString).build(), 3004));
        }

        @ParameterizedTest
        @MethodSource("provideInvalidUpdateProfileRequests")
        @WithMockUser
        @DisplayName("Update My Profile - Fail: bắt lỗi validation, trả về bad request")
        void updateMyProfile_InvalidRequest_ShouldReturnBadRequest(
                ProfileUpdateRequest invalidRequest, int errorCode) throws Exception {
            mockMvc.perform(patch("/profiles/me")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(errorCode));

            verify(profileService, never()).updateMyProfile(any());
        }
    }

    @Nested
    @DisplayName("Get All: test hàm getAll")
    class GetAllTest {

        @Test
        @DisplayName("Get All - Success: admin lấy danh sách profile thành công, trả về List<ProfileResponse>")
        @WithMockUser(roles = "ADMIN")
        void getAll_AdminRole_ShouldReturnListProfiles() throws Exception {
            ProfileResponse profile1 = ProfileResponse.builder().id("uuid-1").build();
            ProfileResponse profile2 = ProfileResponse.builder().id("uuid-2").build();

            when(profileService.getAll()).thenReturn(List.of(profile1, profile2));

            mockMvc.perform(get("/profiles")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").isArray())
                    .andExpect(jsonPath("$.result.size()").value(2));

            verify(profileService, times(1)).getAll();
        }

        @Test
        @DisplayName("Get All - Fail: bị chặn khi không có quyền admin, trả về forbidden")
        @WithMockUser(roles = "USER")
        void getAll_UserRole_ShouldReturnForbidden() throws Exception {
            mockMvc.perform(get("/profiles")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());

            verify(profileService, never()).getAll();
        }
    }

    @Nested
    @DisplayName("Get Profile: test hàm getProfile")
    class GetProfileTest {

        @Test
        @DisplayName("Get Profile - Success: admin lấy chi tiết profile thành công, trả về ProfileResponse")
        @WithMockUser(roles = "ADMIN")
        void getProfile_AdminRole_ShouldReturnProfileResponse() throws Exception {
            String profileId = "profile-uuid-1234";
            ProfileResponse mockResponse = ProfileResponse.builder()
                    .id(profileId)
                    .firstName("test")
                    .build();

            when(profileService.getDetail(profileId)).thenReturn(mockResponse);

            mockMvc.perform(get("/profiles/{profileId}", profileId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.id").value(profileId));

            verify(profileService, times(1)).getDetail(profileId);
        }

        @Test
        @DisplayName("Get Profile - Fail: bị chặn khi không có quyền admin, trả về forbidden")
        @WithMockUser(roles = "USER")
        void getProfile_UserRole_ShouldReturnForbidden() throws Exception {
            mockMvc.perform(get("/profiles/{profileId}", "profile-uuid-1234")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());

            verify(profileService, never()).getDetail(any());
        }
    }

    @Nested
    @DisplayName("Update Profile: test hàm updateProfile")
    class UpdateProfileTest {

        @Test
        @DisplayName("Update Profile - Success: admin cập nhật profile thành công, trả về ProfileResponse")
        @WithMockUser(roles = "ADMIN")
        void updateProfile_AdminRole_ValidRequest_ShouldReturnProfileResponse() throws Exception {
            String profileId = "profile-uuid-1234";
            ProfileUpdateRequest request = ProfileUpdateRequest.builder()
                    .firstName("test")
                    .lastName("test")
                    .build();

            ProfileResponse mockResponse = ProfileResponse.builder()
                    .id(profileId)
                    .firstName("test")
                    .build();

            when(profileService.updateProfile(eq(profileId), any(ProfileUpdateRequest.class)))
                    .thenReturn(mockResponse);

            mockMvc.perform(patch("/profiles/{profileId}", profileId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.id").value(profileId));

            verify(profileService, times(1)).updateProfile(eq(profileId), any(ProfileUpdateRequest.class));
        }

        @Test
        @DisplayName("Update Profile - Fail: bị chặn khi không có quyền admin, trả về forbidden")
        @WithMockUser(roles = "USER")
        void updateProfile_UserRole_ShouldReturnForbidden() throws Exception {
            mockMvc.perform(patch("/profiles/{profileId}", "profile-uuid-1234")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(ProfileUpdateRequest.builder().build())))
                    .andExpect(status().isForbidden());

            verify(profileService, never()).updateProfile(any(), any());
        }
    }

    @Nested
    @DisplayName("Update My Avatar: test hàm updateMyAvatar")
    class UpdateMyAvatarTest {

        @Test
        @DisplayName("Update My Avatar - Success: upload avatar bản thân thành công, trả về AvatarResponse")
        @WithMockUser(username = "test@example.com")
        void updateMyAvatar_ValidFile_ShouldReturnAvatarResponse() throws Exception {
            MockMultipartFile file = new MockMultipartFile(
                    "avatar", "avatar.jpg", "image/jpeg", VALID_JPEG);

            AvatarResponse mockResponse = AvatarResponse.builder()
                    .avatar("https://cloudinary.com/avatar.jpg")
                    .build();

            when(profileService.updateMyAvatar(any(AvatarUpdateRequest.class))).thenReturn(mockResponse);

            mockMvc.perform(multipart("/profiles/me/avatar")
                            .file(file)
                            .with(csrf())
                            .with(request -> {
                                request.setMethod("PUT");
                                return request;
                            })
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.avatar").value("https://cloudinary.com/avatar.jpg"));

            verify(profileService, times(1)).updateMyAvatar(any(AvatarUpdateRequest.class));
        }

        @Test
        @DisplayName("Update My Avatar - Fail: không gửi file, trả về bad request")
        @WithMockUser
        void updateMyAvatar_NoFile_ShouldReturnBadRequest() throws Exception {
            mockMvc.perform(multipart("/profiles/me/avatar")
                            .with(csrf())
                            .with(request -> {
                                request.setMethod("PUT");
                                return request;
                            })
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isBadRequest());

            verify(profileService, never()).updateMyAvatar(any());
        }

        @Test
        @DisplayName("Update My Avatar - Fail: bị chặn khi chưa xác thực, trả về unauthorized")
        void updateMyAvatar_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
            MockMultipartFile file = new MockMultipartFile(
                    "avatar", "avatar.jpg", "image/jpeg", VALID_JPEG);

            mockMvc.perform(multipart("/profiles/me/avatar")
                            .file(file)
                            .with(csrf())
                            .with(request -> {
                                request.setMethod("PUT");
                                return request;
                            })
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isUnauthorized());

            verify(profileService, never()).updateMyAvatar(any());
        }
    }

    @Nested
    @DisplayName("Update Avatar: test hàm updateAvatar")
    class UpdateAvatarTest {

        @Test
        @DisplayName("Update Avatar - Success: admin upload avatar user thành công, trả về AvatarResponse")
        @WithMockUser(roles = "ADMIN")
        void updateAvatar_AdminRole_ValidFile_ShouldReturnAvatarResponse() throws Exception {
            String profileId = "profile-uuid-1234";
            MockMultipartFile file = new MockMultipartFile(
                    "avatar", "avatar.jpg", "image/jpeg", VALID_JPEG);

            AvatarResponse mockResponse = AvatarResponse.builder()
                    .avatar("https://cloudinary.com/avatar.jpg")
                    .build();

            when(profileService.updateAvatar(eq(profileId), any(AvatarUpdateRequest.class)))
                    .thenReturn(mockResponse);

            mockMvc.perform(multipart("/profiles/{profileId}/avatar", profileId)
                            .file(file)
                            .with(csrf())
                            .with(request -> {
                                request.setMethod("PUT");
                                return request;
                            })
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.avatar").value("https://cloudinary.com/avatar.jpg"));

            verify(profileService, times(1)).updateAvatar(eq(profileId), any(AvatarUpdateRequest.class));
        }

        @Test
        @DisplayName("Update Avatar - Fail: bị chặn khi không có quyền admin, trả về forbidden")
        @WithMockUser(roles = "USER")
        void updateAvatar_UserRole_ShouldReturnForbidden() throws Exception {
            MockMultipartFile file = new MockMultipartFile(
                    "avatar", "avatar.jpg", "image/jpeg", VALID_JPEG);

            mockMvc.perform(multipart("/profiles/{profileId}/avatar", "profile-uuid-1234")
                            .file(file)
                            .with(csrf())
                            .with(request -> {
                                request.setMethod("PUT");
                                return request;
                            })
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isForbidden());

            verify(profileService, never()).updateAvatar(any(), any());
        }

        @Test
        @DisplayName("Update Avatar - Fail: không gửi file, trả về bad request")
        @WithMockUser(roles = "ADMIN")
        void updateAvatar_NoFile_ShouldReturnBadRequest() throws Exception {
            mockMvc.perform(multipart("/profiles/{profileId}/avatar", "profile-uuid-1234")
                            .with(csrf())
                            .with(request -> {
                                request.setMethod("PUT");
                                return request;
                            })
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isBadRequest());

            verify(profileService, never()).updateAvatar(any(), any());
        }
    }
}