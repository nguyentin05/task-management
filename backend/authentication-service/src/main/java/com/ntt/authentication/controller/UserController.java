package com.ntt.authentication.controller;

import com.ntt.authentication.dto.request.RegisterRequest;
import com.ntt.authentication.dto.response.ApiResponse;
import com.ntt.authentication.dto.response.IntrospectResponse;
import com.ntt.authentication.dto.response.UserResponse;
import com.ntt.authentication.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @PostMapping("/register")
    ApiResponse<UserResponse> register(@RequestBody @Valid RegisterRequest request) {
        UserResponse result = userService.register(request);

        return ApiResponse.<UserResponse>builder()
                .result(result)
                .build();
    }
}
