package com.ntt.profile_service.controller.internal;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
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

import com.ntt.profile_service.configuration.CustomJwtDecoder;
import com.ntt.profile_service.configuration.SecurityConfig;
import com.ntt.profile_service.dto.response.ProfileSearchResponse;
import com.ntt.profile_service.service.ProfileService;

@WebMvcTest(controllers = InternalProfileController.class)
@Import(SecurityConfig.class)
class InternalProfileControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ProfileService profileService;

    @MockitoBean
    CustomJwtDecoder customJwtDecoder;

    @MockitoBean
    UserDetailsService userDetailsService;

    @Test
    @DisplayName("Search - Success: tìm theo userIds, trả về danh sách profile")
    void searchByUserIds_ValidIds_ShouldReturnProfileList() throws Exception {
        ProfileSearchResponse profile1 = ProfileSearchResponse.builder()
                .userId("uuid-1")
                .firstName("Alice")
                .lastName("Nguyen")
                .build();
        ProfileSearchResponse profile2 = ProfileSearchResponse.builder()
                .userId("uuid-2")
                .firstName("Bob")
                .lastName("Tran")
                .build();

        when(profileService.searchByUserIds(List.of("uuid-1", "uuid-2"))).thenReturn(List.of(profile1, profile2));

        mockMvc.perform(get("/internal/profiles/search")
                        .param("userIds", "uuid-1", "uuid-2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").isArray())
                .andExpect(jsonPath("$.result.length()").value(2))
                .andExpect(jsonPath("$.result[0].userId").value("uuid-1"))
                .andExpect(jsonPath("$.result[1].userId").value("uuid-2"));

        verify(profileService, times(1)).searchByUserIds(List.of("uuid-1", "uuid-2"));
    }

    @Test
    @DisplayName("Search - Success: không có user nào khớp, trả về danh sách rỗng")
    void searchByUserIds_NoMatch_ShouldReturnEmptyList() throws Exception {
        when(profileService.searchByUserIds(anyList())).thenReturn(List.of());

        mockMvc.perform(get("/internal/profiles/search")
                        .param("userIds", "non-existent-id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").isArray())
                .andExpect(jsonPath("$.result.length()").value(0));
    }
}
