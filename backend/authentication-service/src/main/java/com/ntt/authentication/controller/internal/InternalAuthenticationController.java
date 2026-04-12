package com.ntt.authentication.controller.internal;

import java.util.List;

import com.ntt.authentication.exception.AppException;
import com.ntt.authentication.exception.ErrorCode;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.ntt.authentication.dto.request.TokenIntrospectRequest;
import com.ntt.authentication.dto.response.ApiResponse;
import com.ntt.authentication.dto.response.IntrospectResponse;
import com.ntt.authentication.dto.response.UserSearchResponse;
import com.ntt.authentication.service.AuthenticationService;
import com.ntt.authentication.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/auth")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InternalAuthenticationController {
    AuthenticationService authenticationService;
    UserService userService;

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> introspect(@RequestBody @Valid TokenIntrospectRequest request) {
        return ApiResponse.<IntrospectResponse>builder()
                .result(authenticationService.introspect(request))
                .build();
    }

    @GetMapping("/users/search")
    ApiResponse<List<UserSearchResponse>> search(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) List<String> userIds) {

        if (email != null && userIds != null) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }
        if (email != null) {
            return ApiResponse.<List<UserSearchResponse>>builder()
                    .result(userService.searchByEmail(email))
                    .build();
        }
        if (userIds != null && !userIds.isEmpty()) {
            return ApiResponse.<List<UserSearchResponse>>builder()
                    .result(userService.searchByUserIds(userIds))
                    .build();
        }
        throw new AppException(ErrorCode.INVALID_REQUEST);
    }
}
