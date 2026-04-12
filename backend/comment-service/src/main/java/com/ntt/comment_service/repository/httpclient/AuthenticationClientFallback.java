package com.ntt.comment_service.repository.httpclient;

import com.ntt.comment_service.dto.response.ApiResponse;
import com.ntt.comment_service.dto.response.UserSearchResponse;
import com.ntt.comment_service.repository.httpclient.AuthenticationClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuthenticationClientFallback implements AuthenticationClient {
    @Override
    public ApiResponse<List<UserSearchResponse>> searchByUserIds(List<String> userIds) {
        return ApiResponse.<List<UserSearchResponse>>builder()
                .result(List.of())
                .build();
    }
}
