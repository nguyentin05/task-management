package com.ntt.profile_service.mapper;

import com.ntt.profile_service.domain.Profile;
import com.ntt.profile_service.dto.request.ProfileCreationRequest;
import com.ntt.profile_service.dto.request.ProfileUpdateRequest;
import com.ntt.profile_service.dto.response.ProfileResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    Profile toProfile(ProfileCreationRequest request);

    ProfileResponse toProfileResponse(Profile userProfile);

    void update(@MappingTarget Profile entity, ProfileUpdateRequest request);
}
