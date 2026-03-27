package com.ntt.authentication.controller.external;

import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ntt.authentication.dto.request.*;
import com.ntt.authentication.dto.response.ApiResponse;
import com.ntt.authentication.dto.response.PageResponse;
import com.ntt.authentication.dto.response.UserResponse;
import com.ntt.authentication.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/auth/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @PostMapping("/register")
    ApiResponse<UserResponse> register(@RequestBody @Valid UserRegisterRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.register(request))
                .build();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<UserResponse> create(@RequestBody @Valid UserCreationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.create(request))
                .build();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<PageResponse<UserResponse>> getAllUser(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "20") int size) {
        return ApiResponse.<PageResponse<UserResponse>>builder()
                .result(userService.getAllUser(page, size))
                .build();
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<UserResponse> getDetail(@PathVariable("userId") String userId) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getDetail(userId))
                .build();
    }

    @PutMapping("/{userId}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<Void> resetPassword(
            @PathVariable("userId") String userId, @RequestBody @Valid PasswordResetRequest request) {
        userService.resetPassword(userId, request);
        return ApiResponse.<Void>builder()
                .message("Đặt lại mật khẩu cho người dùng thành công")
                .build();
    }

    @PutMapping("/{userId}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<UserResponse> updateRoles(
            @PathVariable("userId") String userId, @RequestBody @Valid RoleUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateRoles(userId, request))
                .build();
    }

    @GetMapping("/me")
    ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

    @PutMapping("/me/change-password")
    ApiResponse<Void> changePassword(@RequestBody @Valid PasswordChangeRequest request) {
        userService.changePassword(request);
        return ApiResponse.<Void>builder()
                .message("Cập nhật mật khẩu thành công")
                .build();
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<Void> delete(@PathVariable String userId) {
        userService.delete(userId);
        return ApiResponse.<Void>builder().message("Xóa user thành công").build();
    }
}
