package com.ntt.authentication.mapper;

import com.ntt.authentication.domain.User;
import com.ntt.authentication.dto.request.RegisterRequest;
import com.ntt.authentication.dto.response.UserResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(RegisterRequest request);

    UserResponse toUserResponse(User user);
}
