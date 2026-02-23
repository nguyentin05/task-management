package com.ntt.authentication.mapper;

import com.ntt.authentication.domain.User;
import com.ntt.authentication.dto.request.RegisterRequest;
import com.ntt.authentication.dto.request.UserUpdateRequest;
import com.ntt.authentication.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(RegisterRequest request);

    UserResponse toUserResponse(User user);

    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
