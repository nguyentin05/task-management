package com.ntt.task_service.repository.httpclient;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ntt.task_service.configuration.AuthenticationRequestInterceptor;
import com.ntt.task_service.dto.response.ApiResponse;
import com.ntt.task_service.dto.response.UserSearchResponse;

@FeignClient(
        name = "authentication-service",
        url = "${services.authentication.url}",
        configuration = {AuthenticationRequestInterceptor.class},
        fallback = AuthenticationClientFallback.class)
public interface AuthenticationClient {
    @GetMapping(value = "/internal/auth/users/search")
    ApiResponse<List<UserSearchResponse>> searchByEmail(@RequestParam String email);
}
