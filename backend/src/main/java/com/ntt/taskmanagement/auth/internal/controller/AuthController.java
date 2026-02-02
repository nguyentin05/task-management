package com.ntt.taskmanagement.auth.internal.controller;

import com.ntt.taskmanagement.auth.dto.request.LoginRequest;
import com.ntt.taskmanagement.auth.dto.request.RegisterRequest;
import com.ntt.taskmanagement.auth.dto.response.AuthResponse;
import com.ntt.taskmanagement.auth.internal.service.AuthService;
import com.ntt.taskmanagement.common.api.ApiResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@RequestBody @Valid RegisterRequest request) {
        AuthResponse result = authService.register(request);

        return ApiResponse.<AuthResponse>builder()
                .result(result)
                .message("Đăng ký thành công")
                .build();
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        AuthResponse result = authService.login(request);

        return ApiResponse.<AuthResponse>builder()
                .result(result)
                .message("Đăng nhập thành công")
                .build();
    }

    @PostMapping("/logout")
    public ApiResponse<Void> login() {
        return ApiResponse.<Void>builder()
                .message("Đăng xuất thành công")
                .build();
    }

}
