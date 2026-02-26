package com.ntt.profile_service.controller;

import com.ntt.profile_service.dto.request.ProfileCreationRequest;
import com.ntt.profile_service.dto.request.UpdateProfileRequest;
import com.ntt.profile_service.dto.response.ApiResponse;
import com.ntt.profile_service.dto.response.UserProfileResponse;
import com.ntt.profile_service.service.UserProfileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserProfileController {
    UserProfileService userProfileService;

    @PostMapping("/users")
    ApiResponse<UserProfileResponse> create(@RequestBody ProfileCreationRequest request) {
        return ApiResponse.<UserProfileResponse>builder()
                .result(userProfileService.createProfile(request))
                .build();
    }

    @GetMapping("/users/{profileId}")
    ApiResponse<UserProfileResponse> getProfile(@PathVariable String profileId) {
        return ApiResponse.<UserProfileResponse>builder()
                .result(userProfileService.getProfile(profileId))
                .build();
    }

//    @GetMapping("/users")
//    ApiResponse<List<UserProfileResponse>> getAllProfiles() {
//        return ApiResponse.<List<UserProfileResponse>>builder()
//                .result(userProfileService.getAllProfiles())
//                .build();
//    }
//
//    @GetMapping("/users/my-profile")
//    ApiResponse<UserProfileResponse> getMyProfile() {
//        return ApiResponse.<UserProfileResponse>builder()
//                .result(userProfileService.getMyProfile())
//                .build();
//    }
//
//    @PutMapping("/users/my-profile")
//    ApiResponse<UserProfileResponse> updateMyProfile(@RequestBody UpdateProfileRequest request) {
//        return ApiResponse.<UserProfileResponse>builder()
//                .result(userProfileService.updateMyProfile(request))
//                .build();
//    }
//
//    @PutMapping("/users/avatar")
//    ApiResponse<UserProfileResponse> updateAvatar(@RequestParam("file") MultipartFile file) {
//        return ApiResponse.<UserProfileResponse>builder()
//                .result(userProfileService.updateAvatar(file))
//                .build();
//    }
//
//    @PostMapping("/users/search")
//    ApiResponse<List<UserProfileResponse>> search(@RequestBody SearchUserRequest request) {
//        return ApiResponse.<List<UserProfileResponse>>builder()
//                .result(userProfileService.search(request))
//                .build();
//    }
}
