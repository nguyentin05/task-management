package com.ntt.task_service.repository.httpclient;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ntt.task_service.configuration.AuthenticationRequestInterceptor;
import com.ntt.task_service.dto.response.ApiResponse;
import com.ntt.task_service.dto.response.ProfileSearchResponse;

@FeignClient(
        name = "profile-service",
        url = "${services.profile.url}",
        configuration = {AuthenticationRequestInterceptor.class})
public interface ProfileClient {

    @GetMapping(value = "/internal/profiles/search")
    ApiResponse<List<ProfileSearchResponse>> searchByUserIds(@RequestParam List<String> userIds);
}
