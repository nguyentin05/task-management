package com.ntt.authentication.controller.internal;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ntt.authentication.dto.request.TokenIntrospectRequest;
import com.ntt.authentication.dto.response.ApiResponse;
import com.ntt.authentication.dto.response.IntrospectResponse;
import com.ntt.authentication.service.AuthenticationService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/auth")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InternalAuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> introspect(@RequestBody @Valid TokenIntrospectRequest request) {
        return ApiResponse.<IntrospectResponse>builder()
                .result(authenticationService.introspect(request))
                .build();
    }
}
