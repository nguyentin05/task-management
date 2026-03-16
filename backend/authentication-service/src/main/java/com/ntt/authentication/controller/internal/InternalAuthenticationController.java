package com.ntt.authentication.controller.internal;

import com.ntt.authentication.dto.response.UserSearchResponse;
import com.ntt.authentication.service.UserService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import com.ntt.authentication.dto.request.TokenIntrospectRequest;
import com.ntt.authentication.dto.response.ApiResponse;
import com.ntt.authentication.dto.response.IntrospectResponse;
import com.ntt.authentication.service.AuthenticationService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InternalAuthenticationController {
    AuthenticationService authenticationService;
    UserService userService;

    @PostMapping("/auth/introspect")
    ApiResponse<IntrospectResponse> introspect(@RequestBody @Valid TokenIntrospectRequest request) {
        return ApiResponse.<IntrospectResponse>builder()
                .result(authenticationService.introspect(request))
                .build();
    }

    @GetMapping("/users/search")
    ApiResponse<List<UserSearchResponse>> searchByEmail(@RequestParam String email) {
        return ApiResponse.<List<UserSearchResponse>>builder()
                .result(userService.searchByEmail(email))
                .build();
    }
}
