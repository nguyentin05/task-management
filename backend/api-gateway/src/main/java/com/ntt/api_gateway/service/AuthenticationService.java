package com.ntt.api_gateway.service;

import com.ntt.api_gateway.dto.request.IntrospectRequest;
import com.ntt.api_gateway.dto.response.ApiResponse;
import com.ntt.api_gateway.dto.response.IntrospectResponse;
import com.ntt.api_gateway.repository.AuthenticationClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    AuthenticationClient identityClient;

    public Mono<ApiResponse<IntrospectResponse>> introspect(String token) {
        return identityClient.introspect(IntrospectRequest.builder().token(token).build());
    }
}
