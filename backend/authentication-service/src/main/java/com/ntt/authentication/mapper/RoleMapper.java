package com.ntt.authentication.mapper;

import com.ntt.authentication.domain.Role;
import com.ntt.authentication.dto.request.RoleRequest;
import com.ntt.authentication.dto.response.RoleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}
