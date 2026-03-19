package com.ntt.authentication.controller.external;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ntt.authentication.dto.request.AuthenticationRequest;
import com.ntt.authentication.dto.request.LogoutRequest;
import com.ntt.authentication.dto.request.TokenRefreshRequest;
import com.ntt.authentication.dto.response.AuthenticationResponse;
import com.ntt.authentication.service.AuthenticationService;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthenticationControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    AuthenticationService authenticationService;

    @Test
    @DisplayName("Authenticate - Success: trả về token khi đăng nhập hợp lệ")
    void authenticate_ValidRequest_ShouldReturnToken() throws Exception {
        AuthenticationRequest request = AuthenticationRequest.builder()
                .email("test@example.com")
                .password("Password123!")
                .build();

        AuthenticationResponse mockResponse = AuthenticationResponse.builder()
                .token("mock-jwt-token")
                .isAuthenticated(true)
                .build();

        when(authenticationService.authenticate(any(AuthenticationRequest.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.token").value("mock-jwt-token"))
                .andExpect(jsonPath("$.result.isAuthenticated").value(true));
    }

    static Stream<Arguments> provideInvalidAuthRequests() {
        return Stream.of(
                Arguments.of(
                        AuthenticationRequest.builder().password("Pass123!").build(), "thiếu email"),
                Arguments.of(
                        AuthenticationRequest.builder()
                                .email("test@example.com")
                                .build(),
                        "thiếu password"),
                Arguments.of(
                        AuthenticationRequest.builder()
                                .email("invalid-email")
                                .password("Pass123!")
                                .build(),
                        "sai định dạng email"),
                Arguments.of(
                        AuthenticationRequest.builder()
                                .email("test@example.com")
                                .password("Pass123!".repeat(32))
                                .build(),
                        "password vượt quá ký tự"),
                Arguments.of(
                        AuthenticationRequest.builder()
                                .email("a".repeat(250) + "@gmail.com")
                                .password("Pass123!")
                                .build(),
                        "email vượt quá ký tự"));
    }

    @ParameterizedTest(name = "Validation - Fail: {1}")
    @MethodSource("provideInvalidAuthRequests")
    void authenticate_InvalidData_ShouldReturnBadRequest(AuthenticationRequest invalidRequest, String testName)
            throws Exception {
        mockMvc.perform(post("/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Logout - Success: cấp lại token mới")
    void refresh_ValidRequest_ShouldReturnNewToken() throws Exception {
        TokenRefreshRequest request =
                TokenRefreshRequest.builder().token("old-refresh-token").build();

        AuthenticationResponse mockResponse = AuthenticationResponse.builder()
                .token("new-jwt-token")
                .isAuthenticated(true)
                .build();

        when(authenticationService.refresh(any(TokenRefreshRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.token").value("new-jwt-token"));
    }

    @Test
    @DisplayName("Refresh - Fail: thiếu token")
    void refresh_MissingToken_ShouldReturnBadRequest() throws Exception {
        TokenRefreshRequest invalidRequest = TokenRefreshRequest.builder().build();

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Logout - Success: đăng xuất thành công")
    void logout_ValidRequest_ShouldReturnSuccessMessage() throws Exception {
        LogoutRequest request = LogoutRequest.builder().token("valid-token").build();

        doNothing().when(authenticationService).logout(any(LogoutRequest.class));

        mockMvc.perform(post("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Đăng xuất thành công"));
    }

    @Test
    @DisplayName("Logout - Fail: thiếu token")
    void logout_MissingToken_ShouldReturnBadRequest() throws Exception {
        LogoutRequest invalidRequest = LogoutRequest.builder().build();

        mockMvc.perform(post("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
