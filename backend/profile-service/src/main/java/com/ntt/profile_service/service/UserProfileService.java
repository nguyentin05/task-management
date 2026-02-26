package com.ntt.profile_service.service;

import com.ntt.profile_service.domain.UserProfile;
import com.ntt.profile_service.dto.request.ProfileCreationRequest;
import com.ntt.profile_service.dto.request.UpdateProfileRequest;
import com.ntt.profile_service.dto.response.UserProfileResponse;
import com.ntt.profile_service.exception.AppException;
import com.ntt.profile_service.exception.ErrorCode;
import com.ntt.profile_service.mapper.UserProfileMapper;
import com.ntt.profile_service.repository.UserProfileRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserProfileService {
    UserProfileRepository userProfileRepository;
    UserProfileMapper userProfileMapper;

    public UserProfileResponse createProfile(ProfileCreationRequest request) {
        UserProfile userProfile = userProfileMapper.toUserProfile(request);

        userProfile = userProfileRepository.save(userProfile);

        return userProfileMapper.toUserProfileResponse(userProfile);
    }

    public UserProfileResponse getProfile(String id) {
        UserProfile userProfile =
                userProfileRepository.findById(id).orElseThrow(
                        () -> new AppException(ErrorCode.USER_NOT_FOUND));

        return userProfileMapper.toUserProfileResponse(userProfile);
    }

//    @PreAuthorize("hasRole('ADMIN')")
//    public List<UserProfileResponse> getAllProfiles() {
//        var profiles = userProfileRepository.findAll();
//
//        return profiles.stream().map(userProfileMapper::toUserProfileResponse).toList();
//    }
//
//    public UserProfileResponse getMyProfile() {
//        var authentication = SecurityContextHolder.getContext().getAuthentication();
//        String userId = authentication.getName();
//
//        var profile = userProfileRepository.findByUserId(userId)
//                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
//
//        return userProfileMapper.toUserProfileResponse(profile);
//    }
//
//    public UserProfileResponse updateMyProfile(UpdateProfileRequest request) {
//        var authentication = SecurityContextHolder.getContext().getAuthentication();
//        String userId = authentication.getName();
//
//        var profile = userProfileRepository.findByUserId(userId)
//                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
//
//        userProfileMapper.update(profile, request);
//
//        return userProfileMapper.toUserProfileResponse(userProfileRepository.save(profile));
//    }
}
