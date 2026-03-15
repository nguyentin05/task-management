package com.ntt.profile_service.service;

import com.ntt.profile_service.domain.Profile;
import com.ntt.profile_service.dto.request.AvatarUpdateRequest;
import com.ntt.profile_service.dto.request.ProfileCreationRequest;
import com.ntt.profile_service.dto.request.ProfileUpdateRequest;
import com.ntt.profile_service.dto.response.AvatarResponse;
import com.ntt.profile_service.dto.response.ProfileResponse;
import com.ntt.profile_service.dto.response.ProfileSearchResponse;
import com.ntt.profile_service.exception.AppException;
import com.ntt.profile_service.exception.ErrorCode;
import com.ntt.profile_service.mapper.ProfileMapper;
import com.ntt.profile_service.repository.ProfileRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileService {
    ProfileRepository profileRepository;
    ProfileMapper profileMapper;
    CloudinaryService cloudinaryService;

    @Transactional
    public void create(ProfileCreationRequest request) {
        if (profileRepository.existsByUserId(request.getUserId())) {
            throw new AppException(ErrorCode.PROFILE_EXISTED);
        }

        Profile profile = profileMapper.toProfile(request);
        profile = profileRepository.save(profile);

        profileMapper.toProfileResponse(profile);
    }

    public ProfileResponse getMyProfile() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        var profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));

        return profileMapper.toProfileResponse(profile);
    }

    @Transactional
    public ProfileResponse updateMyProfile(ProfileUpdateRequest request) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        var profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));

        profileMapper.update(profile, request);

        return profileMapper.toProfileResponse(profileRepository.save(profile));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<ProfileResponse> getAll() {
        var profiles = profileRepository.findAll();

        return profiles.stream().map(profileMapper::toProfileResponse).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ProfileResponse getDetail(String id) {
        Profile userProfile = profileRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));

        return profileMapper.toProfileResponse(userProfile);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ProfileResponse updateProfile(String id, ProfileUpdateRequest request) {
        var profile = profileRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));

        profileMapper.update(profile, request);

        return profileMapper.toProfileResponse(profileRepository.save(profile));
    }

    @Transactional
    public AvatarResponse updateMyAvatar(AvatarUpdateRequest request) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));
        return doUpdateAvatar(profile, request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public AvatarResponse updateAvatar(String id, AvatarUpdateRequest request) {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));
        return doUpdateAvatar(profile, request);
    }

    public List<ProfileSearchResponse> searchByUserIds(List<String> userIds) {
        return profileRepository.findByUserIdIn(userIds)
                .stream()
                .map(profileMapper::toProfileSearchResponse)
                .toList();
    }

    private AvatarResponse doUpdateAvatar(Profile profile, AvatarUpdateRequest request) {
        try {
            String avatarUrl = cloudinaryService.uploadFile(request.getAvatar());
            profile.setAvatar(avatarUrl);
            profileRepository.save(profile);
            return AvatarResponse.builder().avatar(avatarUrl).build();
        } catch (IOException e) {
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

}
