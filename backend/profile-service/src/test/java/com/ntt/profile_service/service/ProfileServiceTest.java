package com.ntt.profile_service.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ntt.profile_service.domain.Profile;
import com.ntt.profile_service.dto.request.AvatarUpdateRequest;
import com.ntt.profile_service.dto.request.ProfileCreationRequest;
import com.ntt.profile_service.dto.request.ProfileUpdateRequest;
import com.ntt.profile_service.dto.response.AvatarResponse;
import com.ntt.profile_service.dto.response.PageResponse;
import com.ntt.profile_service.dto.response.ProfileResponse;
import com.ntt.profile_service.dto.response.ProfileSearchResponse;
import com.ntt.profile_service.exception.AppException;
import com.ntt.profile_service.exception.ErrorCode;
import com.ntt.profile_service.mapper.ProfileMapper;
import com.ntt.profile_service.repository.ProfileRepository;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    ProfileRepository profileRepository;

    @Mock
    ProfileMapper profileMapper;

    @Mock
    CloudinaryService cloudinaryService;

    @InjectMocks
    ProfileService profileService;

    private Profile profile;

    @BeforeEach
    void setUpGlobal() {
        profile = Profile.builder()
                .id("profile-uuid-1234")
                .userId("user-uuid-1234")
                .firstName("test")
                .lastName("test")
                .build();
    }

    private void mockSecurityContext(MockedStatic<SecurityContextHolder> mocked, String userId) {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        mocked.when(SecurityContextHolder::getContext).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(userId);
    }

    @Nested
    @DisplayName("Scenario - Fail: profile không tồn tại")
    @MockitoSettings(strictness = Strictness.LENIENT)
    class ProfileNotFoundScenario {

        @BeforeEach
        void setupScenario() {
            when(profileRepository.findById(anyString())).thenReturn(Optional.empty());
            when(profileRepository.findByUserId(anyString())).thenReturn(Optional.empty());
        }

        @Test
        @DisplayName("Get Detail: trả về lỗi PROFILE_NOT_FOUND")
        void getDetailTest() {
            assertThatThrownBy(() -> profileService.getDetailProfile("profile-uuid-1234"))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PROFILE_NOT_FOUND);

            verify(profileRepository, times(1)).findById("profile-uuid-1234");
        }

        @Test
        @DisplayName("Update Profile: trả về lỗi PROFILE_NOT_FOUND")
        void updateProfileTest() {
            assertThatThrownBy(() -> profileService.updateProfile(
                            "profile-uuid-1234", ProfileUpdateRequest.builder().build()))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PROFILE_NOT_FOUND);

            verify(profileRepository, never()).save(any());
        }

        @Test
        @DisplayName("Update Avatar: trả về lỗi PROFILE_NOT_FOUND")
        void updateAvatarTest() {
            AvatarUpdateRequest request = AvatarUpdateRequest.builder()
                    .avatar(new MockMultipartFile("avatar", "avatar.jpg", "image/jpeg", new byte[10]))
                    .build();

            assertThatThrownBy(() -> profileService.updateAvatar("profile-uuid-1234", request))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PROFILE_NOT_FOUND);
        }

        @Test
        @DisplayName("Get My Profile: trả về lỗi PROFILE_NOT_FOUND")
        void getMyProfileTest() {
            try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
                mockSecurityContext(mocked, "user-uuid-1234");

                assertThatThrownBy(() -> profileService.getMyProfile())
                        .isInstanceOf(AppException.class)
                        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PROFILE_NOT_FOUND);
            }
        }

        @Test
        @DisplayName("Update My Profile: trả về lỗi PROFILE_NOT_FOUND")
        void updateMyProfileTest() {
            try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
                mockSecurityContext(mocked, "user-uuid-1234");

                assertThatThrownBy(() -> profileService.updateMyProfile(
                                ProfileUpdateRequest.builder().build()))
                        .isInstanceOf(AppException.class)
                        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PROFILE_NOT_FOUND);

                verify(profileRepository, never()).save(any());
            }
        }

        @Test
        @DisplayName("Update My Avatar: trả về lỗi PROFILE_NOT_FOUND")
        void updateMyAvatarTest() {
            try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
                mockSecurityContext(mocked, "user-uuid-1234");

                AvatarUpdateRequest request = AvatarUpdateRequest.builder()
                        .avatar(new MockMultipartFile("avatar", "avatar.jpg", "image/jpeg", new byte[10]))
                        .build();

                assertThatThrownBy(() -> profileService.updateMyAvatar(request))
                        .isInstanceOf(AppException.class)
                        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PROFILE_NOT_FOUND);
            }
        }
    }

    @Nested
    @DisplayName("Create: test hàm create")
    class CreateTest {

        @Test
        @DisplayName("Success: tạo profile thành công")
        void create_ValidRequest_ShouldSaveProfile() {
            ProfileCreationRequest request = ProfileCreationRequest.builder()
                    .userId("user-uuid-1234")
                    .firstName("test")
                    .lastName("test")
                    .build();

            when(profileRepository.existsByUserId(request.getUserId())).thenReturn(false);
            when(profileMapper.toProfile(request)).thenReturn(profile);
            when(profileRepository.save(any(Profile.class))).thenReturn(profile);

            assertThatCode(() -> profileService.create(request)).doesNotThrowAnyException();

            verify(profileRepository, times(1)).save(any(Profile.class));
        }

        @Test
        @DisplayName("Fail: profile đã tồn tại, trả về lỗi PROFILE_EXISTED")
        void create_DuplicateUserId_ShouldThrowProfileExisted() {
            ProfileCreationRequest request =
                    ProfileCreationRequest.builder().userId("user-uuid-1234").build();

            when(profileRepository.existsByUserId(request.getUserId())).thenReturn(true);

            assertThatThrownBy(() -> profileService.create(request))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PROFILE_EXISTED);

            verify(profileRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Get My Profile: test hàm getMyProfile")
    class GetMyProfileTest {

        @Test
        @DisplayName("Success: lấy thông tin profile bản thân thành công, trả về ProfileResponse")
        void getMyProfile_Authenticated_ShouldReturnProfileResponse() {
            ProfileResponse mockResponse = ProfileResponse.builder()
                    .id("profile-uuid-1234")
                    .firstName("test")
                    .build();

            try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
                mockSecurityContext(mocked, "user-uuid-1234");

                when(profileRepository.findByUserId("user-uuid-1234")).thenReturn(Optional.of(profile));
                when(profileMapper.toProfileResponse(profile)).thenReturn(mockResponse);

                ProfileResponse response = profileService.getMyProfile();

                assertThat(response).isNotNull();
                assertThat(response.getId()).isEqualTo("profile-uuid-1234");

                verify(profileRepository, times(1)).findByUserId("user-uuid-1234");
            }
        }
    }

    @Nested
    @DisplayName("Update My Profile: test hàm updateMyProfile")
    class UpdateMyProfileTest {

        @Test
        @DisplayName("Success: cập nhật profile bản thân thành công, trả về ProfileResponse")
        void updateMyProfile_ValidRequest_ShouldReturnProfileResponse() {
            ProfileUpdateRequest request = ProfileUpdateRequest.builder()
                    .firstName("newFirst")
                    .lastName("newLast")
                    .build();

            ProfileResponse mockResponse = ProfileResponse.builder()
                    .id("profile-uuid-1234")
                    .firstName("newFirst")
                    .build();

            try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
                mockSecurityContext(mocked, "user-uuid-1234");

                when(profileRepository.findByUserId("user-uuid-1234")).thenReturn(Optional.of(profile));
                when(profileRepository.save(any(Profile.class))).thenReturn(profile);
                when(profileMapper.toProfileResponse(profile)).thenReturn(mockResponse);

                ProfileResponse response = profileService.updateMyProfile(request);

                assertThat(response).isNotNull();
                assertThat(response.getId()).isEqualTo("profile-uuid-1234");

                verify(profileMapper, times(1)).update(eq(profile), eq(request));
                verify(profileRepository, times(1)).save(any(Profile.class));
            }
        }
    }

    @Nested
    @DisplayName("Get All: test hàm getAllProfile")
    class GetAllTest {

        @Test
        @DisplayName("Success: lấy danh sách profile thành công, trả về PageResponse")
        void getAllProfile_ShouldReturnPageResponse() {
            Profile profile2 = Profile.builder()
                    .id("profile-uuid-5678")
                    .userId("user-uuid-5678")
                    .build();
            ProfileResponse response1 =
                    ProfileResponse.builder().id("profile-uuid-1234").build();
            ProfileResponse response2 =
                    ProfileResponse.builder().id("profile-uuid-5678").build();

            Page<Profile> mockPage = new PageImpl<>(List.of(profile, profile2), PageRequest.of(0, 20), 2);

            when(profileRepository.findAll(any(Pageable.class))).thenReturn(mockPage);
            when(profileMapper.toProfileResponse(profile)).thenReturn(response1);
            when(profileMapper.toProfileResponse(profile2)).thenReturn(response2);

            PageResponse<ProfileResponse> result = profileService.getAllProfile(1, 20);

            assertThat(result.getCurrentPage()).isEqualTo(1);
            assertThat(result.getPageSize()).isEqualTo(20);
            assertThat(result.getTotalElements()).isEqualTo(2);
            assertThat(result.getData()).hasSize(2);
            assertThat(result.getData().get(0).getId()).isEqualTo("profile-uuid-1234");
            assertThat(result.getData().get(1).getId()).isEqualTo("profile-uuid-5678");

            verify(profileRepository, times(1)).findAll(any(Pageable.class));
        }

        @Test
        @DisplayName("Success: không có profile nào, trả về PageResponse rỗng")
        void getAllProfile_NoProfiles_ShouldReturnEmptyPageResponse() {
            Page<Profile> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);

            when(profileRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

            PageResponse<ProfileResponse> result = profileService.getAllProfile(1, 20);

            assertThat(result.getData()).isEmpty();
            assertThat(result.getTotalElements()).isZero();
            assertThat(result.getTotalPages()).isZero();
        }
    }

    @Nested
    @DisplayName("Get Detail: test hàm getDetailProfile")
    class GetDetailTest {

        @Test
        @DisplayName("Success: lấy chi tiết profile thành công, trả về ProfileResponse")
        void getDetail_ValidId_ShouldReturnProfileResponse() {
            ProfileResponse mockResponse = ProfileResponse.builder()
                    .id("profile-uuid-1234")
                    .firstName("test")
                    .build();

            when(profileRepository.findById("profile-uuid-1234")).thenReturn(Optional.of(profile));
            when(profileMapper.toProfileResponse(profile)).thenReturn(mockResponse);

            ProfileResponse response = profileService.getDetailProfile("profile-uuid-1234");

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo("profile-uuid-1234");

            verify(profileRepository, times(1)).findById("profile-uuid-1234");
        }
    }

    @Nested
    @DisplayName("Update Profile: test hàm updateProfile")
    class UpdateProfileTest {

        @Test
        @DisplayName("Success: cập nhật profile thành công, trả về ProfileResponse")
        void updateProfile_ValidRequest_ShouldReturnProfileResponse() {
            ProfileUpdateRequest request =
                    ProfileUpdateRequest.builder().firstName("newFirst").build();

            ProfileResponse mockResponse = ProfileResponse.builder()
                    .id("profile-uuid-1234")
                    .firstName("newFirst")
                    .build();

            when(profileRepository.findById("profile-uuid-1234")).thenReturn(Optional.of(profile));
            when(profileRepository.save(any(Profile.class))).thenReturn(profile);
            when(profileMapper.toProfileResponse(profile)).thenReturn(mockResponse);

            ProfileResponse response = profileService.updateProfile("profile-uuid-1234", request);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo("profile-uuid-1234");

            verify(profileMapper, times(1)).update(eq(profile), eq(request));
            verify(profileRepository, times(1)).save(any(Profile.class));
        }
    }

    @Nested
    @DisplayName("Update My Avatar: test hàm updateMyAvatar")
    class UpdateMyAvatarTest {

        @Test
        @DisplayName("Success: upload avatar bản thân thành công, trả về AvatarResponse")
        void updateMyAvatar_ValidFile_ShouldReturnAvatarResponse() throws IOException {
            AvatarUpdateRequest request = AvatarUpdateRequest.builder()
                    .avatar(new MockMultipartFile("avatar", "avatar.jpg", "image/jpeg", new byte[10]))
                    .build();

            try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
                mockSecurityContext(mocked, "user-uuid-1234");

                when(profileRepository.findByUserId("user-uuid-1234")).thenReturn(Optional.of(profile));
                when(cloudinaryService.uploadFile(any())).thenReturn("https://cloudinary.com/avatar.jpg");
                when(profileRepository.save(any(Profile.class))).thenReturn(profile);

                AvatarResponse response = profileService.updateMyAvatar(request);

                assertThat(response).isNotNull();
                assertThat(response.getAvatar()).isEqualTo("https://cloudinary.com/avatar.jpg");

                verify(cloudinaryService, times(1)).uploadFile(any());
                verify(profileRepository, times(1)).save(any(Profile.class));
            }
        }

        @Test
        @DisplayName("Fail: cloudinary upload thất bại, trả về lỗi INTERNAL_SERVER_ERROR")
        void updateMyAvatar_CloudinaryFails_ShouldThrowInternalServerError() throws IOException {
            AvatarUpdateRequest request = AvatarUpdateRequest.builder()
                    .avatar(new MockMultipartFile("avatar", "avatar.jpg", "image/jpeg", new byte[10]))
                    .build();

            try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
                mockSecurityContext(mocked, "user-uuid-1234");

                when(profileRepository.findByUserId("user-uuid-1234")).thenReturn(Optional.of(profile));
                when(cloudinaryService.uploadFile(any())).thenThrow(new IOException("upload failed"));

                assertThatThrownBy(() -> profileService.updateMyAvatar(request))
                        .isInstanceOf(AppException.class)
                        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INTERNAL_SERVER_ERROR);

                verify(profileRepository, never()).save(any());
            }
        }
    }

    @Nested
    @DisplayName("Update Avatar: test hàm updateAvatar")
    class UpdateAvatarTest {

        @Test
        @DisplayName("Success: upload avatar user thành công, trả về AvatarResponse")
        void updateAvatar_ValidRequest_ShouldReturnAvatarResponse() throws IOException {
            AvatarUpdateRequest request = AvatarUpdateRequest.builder()
                    .avatar(new MockMultipartFile("avatar", "avatar.jpg", "image/jpeg", new byte[10]))
                    .build();

            when(profileRepository.findById("profile-uuid-1234")).thenReturn(Optional.of(profile));
            when(cloudinaryService.uploadFile(any())).thenReturn("https://cloudinary.com/avatar.jpg");
            when(profileRepository.save(any(Profile.class))).thenReturn(profile);

            AvatarResponse response = profileService.updateAvatar("profile-uuid-1234", request);

            assertThat(response).isNotNull();
            assertThat(response.getAvatar()).isEqualTo("https://cloudinary.com/avatar.jpg");

            verify(cloudinaryService, times(1)).uploadFile(any());
            verify(profileRepository, times(1)).save(any(Profile.class));
        }

        @Test
        @DisplayName("Fail: cloudinary upload thất bại, trả về lỗi INTERNAL_SERVER_ERROR")
        void updateAvatar_CloudinaryFails_ShouldThrowInternalServerError() throws IOException {
            AvatarUpdateRequest request = AvatarUpdateRequest.builder()
                    .avatar(new MockMultipartFile("avatar", "avatar.jpg", "image/jpeg", new byte[10]))
                    .build();

            when(profileRepository.findById("profile-uuid-1234")).thenReturn(Optional.of(profile));
            when(cloudinaryService.uploadFile(any())).thenThrow(new IOException("upload failed"));

            assertThatThrownBy(() -> profileService.updateAvatar("profile-uuid-1234", request))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INTERNAL_SERVER_ERROR);

            verify(profileRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Search By User Ids: test hàm searchByUserIds")
    class SearchByUserIdsTest {

        @Test
        @DisplayName("Success: tìm kiếm profile theo userIds thành công, trả về List<ProfileSearchResponse>")
        void searchByUserIds_ValidIds_ShouldReturnList() {
            List<String> userIds = List.of("user-uuid-1234", "user-uuid-5678");
            Profile profile2 = Profile.builder()
                    .id("profile-uuid-5678")
                    .userId("user-uuid-5678")
                    .build();
            ProfileSearchResponse res1 =
                    ProfileSearchResponse.builder().userId("user-uuid-1234").build();
            ProfileSearchResponse res2 =
                    ProfileSearchResponse.builder().userId("user-uuid-5678").build();

            when(profileRepository.findByUserIdIn(userIds)).thenReturn(List.of(profile, profile2));
            when(profileMapper.toProfileSearchResponse(profile)).thenReturn(res1);
            when(profileMapper.toProfileSearchResponse(profile2)).thenReturn(res2);

            List<ProfileSearchResponse> result = profileService.searchByUserIds(userIds);

            assertThat(result).hasSize(2);
            assertThat(result.getFirst().getUserId()).isEqualTo("user-uuid-1234");

            verify(profileRepository, times(1)).findByUserIdIn(userIds);
        }

        @Test
        @DisplayName("Success: không tìm thấy profile nào, trả về danh sách rỗng")
        void searchByUserIds_NoMatch_ShouldReturnEmptyList() {
            when(profileRepository.findByUserIdIn(any())).thenReturn(List.of());

            List<ProfileSearchResponse> result = profileService.searchByUserIds(List.of("nonexistent"));

            assertThat(result).isEmpty();
        }
    }
}
