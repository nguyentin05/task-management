package com.ntt.authentication.controller.external;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ntt.authentication.dto.response.ApiResponse;
import com.ntt.authentication.dto.response.PageResponse;
import com.ntt.authentication.dto.response.RoleResponse;
import com.ntt.authentication.service.RoleService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/auth/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
    RoleService roleService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<PageResponse<RoleResponse>> getAllRole(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "5") int size) {
        return ApiResponse.<PageResponse<RoleResponse>>builder()
                .result(roleService.getAllRole(page, size))
                .build();
    }
}
