package com.ntt.comment_service.repository.httpclient;

import com.ntt.comment_service.configuration.AuthenticationRequestInterceptor;
import com.ntt.comment_service.dto.response.ApiResponse;
import com.ntt.comment_service.dto.response.UserSearchResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
        name = "authentication-service",
        url = "${services.authentication.url}",
        configuration = {AuthenticationRequestInterceptor.class},
        fallback = AuthenticationClientFallback.class)
public interface AuthenticationClient {
    @GetMapping(value = "/internal/auth/users/search")
    ApiResponse<List<UserSearchResponse>> searchByUserIds(@RequestParam List<String> userIds);
}
