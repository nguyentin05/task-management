package com.ntt.authentication.controller.external;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.ntt.authentication.dto.response.ApiResponse;
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
    ApiResponse<List<RoleResponse>> getAll() {
        return ApiResponse.<List<RoleResponse>>builder()
                .result(roleService.getAll())
                .build();
    }
}
