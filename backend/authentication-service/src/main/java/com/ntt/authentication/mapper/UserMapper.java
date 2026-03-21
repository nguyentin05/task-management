package com.ntt.authentication.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ntt.authentication.domain.User;
import com.ntt.authentication.dto.request.UserCreationRequest;
import com.ntt.authentication.dto.request.UserRegisterRequest;
import com.ntt.authentication.dto.response.UserResponse;
import com.ntt.authentication.dto.response.UserSearchResponse;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserRegisterRequest request);

    @Mapping(target = "roles", ignore = true)
    User toUser(UserCreationRequest request);

    UserResponse toUserResponse(User user);

    UserSearchResponse toUserSearchResponse(User user);
}
