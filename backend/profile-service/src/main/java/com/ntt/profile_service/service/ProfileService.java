package com.ntt.profile_service.service;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

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

        var profile =
                profileRepository.findByUserId(userId).orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));

        return profileMapper.toProfileResponse(profile);
    }

    @Transactional
    public ProfileResponse updateMyProfile(ProfileUpdateRequest request) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        var profile =
                profileRepository.findByUserId(userId).orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));

        profileMapper.update(profile, request);

        return profileMapper.toProfileResponse(profileRepository.save(profile));
    }

    public PageResponse<ProfileResponse> getAllProfile(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<Profile> pageData = profileRepository.findAll(pageable);

        return PageResponse.<ProfileResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(pageData.getContent().stream()
                        .map(profileMapper::toProfileResponse)
                        .toList())
                .build();
    }

    public ProfileResponse getDetailProfile(String id) {
        Profile userProfile =
                profileRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));

        return profileMapper.toProfileResponse(userProfile);
    }

    @Transactional
    public ProfileResponse updateProfile(String id, ProfileUpdateRequest request) {
        var profile = profileRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));

        profileMapper.update(profile, request);

        return profileMapper.toProfileResponse(profileRepository.save(profile));
    }

    @Transactional
    public AvatarResponse updateMyAvatar(AvatarUpdateRequest request) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Profile profile =
                profileRepository.findByUserId(userId).orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));
        return doUpdateAvatar(profile, request);
    }

    @Transactional
    public AvatarResponse updateAvatar(String id, AvatarUpdateRequest request) {
        Profile profile =
                profileRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));
        return doUpdateAvatar(profile, request);
    }

    public List<ProfileSearchResponse> searchByUserIds(List<String> userIds) {
        return profileRepository.findByUserIdIn(userIds).stream()
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
