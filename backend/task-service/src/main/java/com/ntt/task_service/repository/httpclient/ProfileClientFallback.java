package com.ntt.task_service.repository.httpclient;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ntt.task_service.dto.response.ApiResponse;
import com.ntt.task_service.dto.response.ProfileSearchResponse;

@Component
public class ProfileClientFallback implements ProfileClient {
    @Override
    public ApiResponse<List<ProfileSearchResponse>> searchByUserIds(List<String> userIds) {
        return ApiResponse.<List<ProfileSearchResponse>>builder()
                .result(List.of())
                .build();
    }
}
