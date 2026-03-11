package com.ntt.authentication.mapper;

import org.mapstruct.Mapper;

import com.ntt.authentication.domain.Role;
import com.ntt.authentication.dto.response.RoleResponse;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleResponse toRoleResponse(Role role);
}
