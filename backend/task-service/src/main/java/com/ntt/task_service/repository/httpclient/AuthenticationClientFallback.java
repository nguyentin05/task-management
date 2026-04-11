package com.ntt.task_service.repository.httpclient;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ntt.task_service.dto.response.ApiResponse;
import com.ntt.task_service.dto.response.UserSearchResponse;

@Component
public class AuthenticationClientFallback implements AuthenticationClient {
    @Override
    public ApiResponse<List<UserSearchResponse>> searchByEmail(String email) {
        return ApiResponse.<List<UserSearchResponse>>builder().result(List.of()).build();
    }
}
