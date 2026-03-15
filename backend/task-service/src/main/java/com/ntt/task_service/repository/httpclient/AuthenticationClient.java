package com.ntt.task_service.repository.httpclient;

import com.ntt.task_service.dto.response.ApiResponse;
import com.ntt.task_service.dto.response.UserSearchResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "authentication-service", url = "${services.auth.url}")
public interface AuthenticationClient {
    @PostMapping(value = "/internal/users/search")
    ApiResponse<List<UserSearchResponse>> searchByEmail(@RequestParam String email);
}
