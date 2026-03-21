package com.ntt.profile_service.controller.external;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ntt.profile_service.dto.request.AvatarUpdateRequest;
import com.ntt.profile_service.dto.request.ProfileUpdateRequest;
import com.ntt.profile_service.dto.response.ApiResponse;
import com.ntt.profile_service.dto.response.AvatarResponse;
import com.ntt.profile_service.dto.response.ProfileResponse;
import com.ntt.profile_service.service.ProfileService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profiles")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileController {
    ProfileService profileService;

    @GetMapping("/me")
    ApiResponse<ProfileResponse> getMyProfile() {
        return ApiResponse.<ProfileResponse>builder()
                .result(profileService.getMyProfile())
                .build();
    }

    @PatchMapping("/me")
    ApiResponse<ProfileResponse> updateMyProfile(@RequestBody @Valid ProfileUpdateRequest request) {
        return ApiResponse.<ProfileResponse>builder()
                .result(profileService.updateMyProfile(request))
                .build();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<List<ProfileResponse>> getAll() {
        return ApiResponse.<List<ProfileResponse>>builder()
                .result(profileService.getAll())
                .build();
    }

    @GetMapping("/{profileId}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<ProfileResponse> getProfile(@PathVariable String profileId) {
        return ApiResponse.<ProfileResponse>builder()
                .result(profileService.getDetail(profileId))
                .build();
    }

    @PatchMapping("/{profileId}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<ProfileResponse> updateProfile(
            @PathVariable String profileId, @RequestBody @Valid ProfileUpdateRequest request) {
        return ApiResponse.<ProfileResponse>builder()
                .result(profileService.updateProfile(profileId, request))
                .build();
    }

    @PutMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<AvatarResponse> updateMyAvatar(@ModelAttribute @Valid AvatarUpdateRequest request) {
        return ApiResponse.<AvatarResponse>builder()
                .result(profileService.updateMyAvatar(request))
                .build();
    }

    @PutMapping(value = "/{profileId}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<AvatarResponse> updateAvatar(
            @PathVariable String profileId, @ModelAttribute @Valid AvatarUpdateRequest request) {
        return ApiResponse.<AvatarResponse>builder()
                .result(profileService.updateAvatar(profileId, request))
                .build();
    }
}