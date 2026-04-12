package com.ntt.task_service.repository.httpclient;

import com.ntt.task_service.dto.response.ApiResponse;
import com.ntt.task_service.dto.response.UserSearchResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuthenticationClientFallback implements AuthenticationClient {
    @Override
    public ApiResponse<List<UserSearchResponse>> searchByEmail(String email) {
        return ApiResponse.<List<UserSearchResponse>>builder()
                .result(List.of())
                .build();
    }

    @Override
    public ApiResponse<List<UserSearchResponse>> searchByUserIds(List<String> userIds) {
        return ApiResponse.<List<UserSearchResponse>>builder()
                .result(List.of())
                .build();
    }
}
