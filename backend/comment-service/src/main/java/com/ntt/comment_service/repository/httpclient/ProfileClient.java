package com.ntt.comment_service.repository.httpclient;

import com.ntt.comment_service.configuration.AuthenticationRequestInterceptor;
import com.ntt.comment_service.dto.response.ApiResponse;
import com.ntt.comment_service.dto.response.ProfileSearchResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
        name = "profile-service",
        url = "${services.profile.url}",
        configuration = {AuthenticationRequestInterceptor.class},
        fallback = ProfileClientFallback.class)
public interface ProfileClient {
    @GetMapping(value = "/internal/profiles/search")
    ApiResponse<List<ProfileSearchResponse>> searchByUserIds(@RequestParam(value = "userIds") List<String> userIds);
}
