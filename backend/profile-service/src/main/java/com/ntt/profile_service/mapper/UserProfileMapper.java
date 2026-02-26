package com.ntt.profile_service.mapper;

import com.ntt.profile_service.domain.UserProfile;
import com.ntt.profile_service.dto.request.ProfileCreationRequest;
import com.ntt.profile_service.dto.request.UpdateProfileRequest;
import com.ntt.profile_service.dto.response.UserProfileResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {
    UserProfile toUserProfile(ProfileCreationRequest request);

    UserProfileResponse toUserProfileResponse(UserProfile userProfile);

    void update(@MappingTarget UserProfile entity, UpdateProfileRequest request);
}
