package com.ntt.task_service.repository.httpclient;

import com.ntt.task_service.dto.response.ApiResponse;
import com.ntt.task_service.dto.response.ProfileSearchResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProfileClientFallback implements ProfileClient {
    @Override
    public ApiResponse<List<ProfileSearchResponse>> searchByUserIds(List<String> userIds) {
        return ApiResponse.<List<ProfileSearchResponse>>builder()
                .result(List.of())
                .build();
    }
}
