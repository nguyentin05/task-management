package com.ntt.authentication.controller.internal;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ntt.authentication.configuration.CustomJwtDecoder;
import com.ntt.authentication.configuration.SecurityConfig;
import com.ntt.authentication.dto.request.TokenIntrospectRequest;
import com.ntt.authentication.dto.response.IntrospectResponse;
import com.ntt.authentication.dto.response.UserSearchResponse;
import com.ntt.authentication.service.AuthenticationService;
import com.ntt.authentication.service.UserService;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = InternalAuthenticationController.class)
@Import(SecurityConfig.class)
class InternalAuthenticationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    AuthenticationService authenticationService;

    @MockitoBean
    UserService userService;

    @MockitoBean
    CustomJwtDecoder customJwtDecoder;

    @MockitoBean
    UserDetailsService userDetailsService;

    // ─── introspect ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("Introspect - Success: token hợp lệ, trả về valid=true")
    void introspect_ValidToken_ShouldReturnValidTrue() throws Exception {
        TokenIntrospectRequest request =
                TokenIntrospectRequest.builder().token("valid.jwt.token").build();

        IntrospectResponse mockResponse =
                IntrospectResponse.builder().isValid(true).build();

        when(authenticationService.introspect(any(TokenIntrospectRequest.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/internal/auth/introspect")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.valid").value(true));

        verify(authenticationService, times(1)).introspect(any(TokenIntrospectRequest.class));
    }

    @Test
    @DisplayName("Introspect - Success: token không hợp lệ, trả về valid=false")
    void introspect_InvalidToken_ShouldReturnValidFalse() throws Exception {
        TokenIntrospectRequest request =
                TokenIntrospectRequest.builder().token("expired.jwt.token").build();

        IntrospectResponse mockResponse =
                IntrospectResponse.builder().isValid(false).build();

        when(authenticationService.introspect(any(TokenIntrospectRequest.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/internal/auth/introspect")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.valid").value(false));
    }

    // ─── search ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Search - Success: tìm theo email, trả về danh sách user")
    void search_ByEmail_ShouldReturnUserList() throws Exception {
        UserSearchResponse mockUser = UserSearchResponse.builder()
                .id("uuid-1234")
                .email("test@example.com")
                .build();

        when(userService.searchByEmail("test")).thenReturn(List.of(mockUser));

        mockMvc.perform(get("/internal/auth/users/search")
                        .param("email", "test")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").isArray())
                .andExpect(jsonPath("$.result[0].id").value("uuid-1234"))
                .andExpect(jsonPath("$.result[0].email").value("test@example.com"));

        verify(userService, times(1)).searchByEmail("test");
        verify(userService, never()).searchByUserIds(any());
    }

    @Test
    @DisplayName("Search - Success: tìm theo userIds, trả về danh sách user")
    void search_ByUserIds_ShouldReturnUserList() throws Exception {
        UserSearchResponse mockUser1 = UserSearchResponse.builder()
                .id("uuid-1")
                .email("user1@example.com")
                .build();
        UserSearchResponse mockUser2 = UserSearchResponse.builder()
                .id("uuid-2")
                .email("user2@example.com")
                .build();

        when(userService.searchByUserIds(List.of("uuid-1", "uuid-2"))).thenReturn(List.of(mockUser1, mockUser2));

        mockMvc.perform(get("/internal/auth/users/search")
                        .param("userIds", "uuid-1", "uuid-2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").isArray())
                .andExpect(jsonPath("$.result.length()").value(2))
                .andExpect(jsonPath("$.result[0].id").value("uuid-1"))
                .andExpect(jsonPath("$.result[1].id").value("uuid-2"));

        verify(userService, times(1)).searchByUserIds(List.of("uuid-1", "uuid-2"));
        verify(userService, never()).searchByEmail(any());
    }

    @Test
    @DisplayName("Search - Fail: truyền cả email lẫn userIds, trả về lỗi INVALID_REQUEST")
    void search_BothEmailAndUserIds_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/internal/auth/users/search")
                        .param("email", "test")
                        .param("userIds", "uuid-1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService, never()).searchByEmail(any());
        verify(userService, never()).searchByUserIds(any());
    }

    @Test
    @DisplayName("Search - Fail: không truyền tham số nào, trả về lỗi INVALID_REQUEST")
    void search_NoParams_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/internal/auth/users/search").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService, never()).searchByEmail(any());
        verify(userService, never()).searchByUserIds(any());
    }

    @Test
    @DisplayName("Search - Success: tìm theo userIds rỗng, trả về lỗi INVALID_REQUEST")
    void search_EmptyUserIds_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/internal/auth/users/search").param("userIds", "").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
