package com.ntt.authentication.mapper;

import com.ntt.authentication.dto.request.ProfileCreationRequest;
import com.ntt.authentication.dto.request.RegisterRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    ProfileCreationRequest toProfileCreationRequest(RegisterRequest request);
}
