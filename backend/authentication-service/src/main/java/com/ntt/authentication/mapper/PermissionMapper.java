package com.ntt.authentication.mapper;

import com.ntt.authentication.domain.Permission;
import com.ntt.authentication.dto.request.PermissionRequest;
import com.ntt.authentication.dto.response.PermissionResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission permission);
}
