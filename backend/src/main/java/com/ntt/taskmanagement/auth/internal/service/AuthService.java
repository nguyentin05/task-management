package com.ntt.taskmanagement.auth.internal.service;

import com.ntt.taskmanagement.auth.dto.request.LoginRequest;
import com.ntt.taskmanagement.auth.dto.request.RegisterRequest;
import com.ntt.taskmanagement.auth.dto.response.AuthResponse;
import com.ntt.taskmanagement.auth.internal.security.JwtService;
import com.ntt.taskmanagement.auth.internal.security.SecurityUser;
import com.ntt.taskmanagement.common.api.ErrorCode;
import com.ntt.taskmanagement.common.exception.AppException;
import com.ntt.taskmanagement.user.UserModuleApi;
import com.ntt.taskmanagement.user.dto.CreateUserCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserModuleApi userModuleApi;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userModuleApi.isEmailExisted(request.getEmail())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        CreateUserCommand command = CreateUserCommand.builder()
                .email(request.getEmail())
                .password(hashedPassword)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();

        userModuleApi.createUser(command);

        String accessToken = jwtService.generateToken(request.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .isAuthenticated(true)
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        var userDetails = (SecurityUser) authentication.getPrincipal();

        String accessToken = jwtService.generateToken(userDetails.getUsername());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .isAuthenticated(true)
                .build();
    }
}
