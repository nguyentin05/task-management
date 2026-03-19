package com.ntt.authentication.controller.external;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ntt.authentication.configuration.CustomJwtDecoder;
import com.ntt.authentication.configuration.SecurityConfig;
import com.ntt.authentication.dto.response.RoleResponse;
import com.ntt.authentication.service.RoleService;

@WebMvcTest(controllers = RoleController.class)
@Import(SecurityConfig.class)
class RoleControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    RoleService roleService;

    @MockitoBean
    CustomJwtDecoder customJwtDecoder;

    @MockitoBean
    UserDetailsService userDetailsService;

    @Test
    @DisplayName("Get all roles - Success: có quyền admin, trả về danh sách role")
    @WithMockUser(roles = "ADMIN")
    void getAllRole_AdminRole_ShouldReturnListRoles() throws Exception {
        RoleResponse roleAdmin = RoleResponse.builder()
                .name("ADMIN")
                .description("Quản trị viên")
                .build();

        RoleResponse roleUser =
                RoleResponse.builder().name("USER").description("Người dùng").build();

        List<RoleResponse> mockResponses = List.of(roleAdmin, roleUser);

        when(roleService.getAllRole()).thenReturn(mockResponses);

        mockMvc.perform(get("/auth/roles").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").isArray())
                .andExpect(jsonPath("$.result.size()").value(2))
                .andExpect(jsonPath("$.result[0].name").value("ADMIN"))
                .andExpect(jsonPath("$.result[1].name").value("USER"));

        verify(roleService, times(1)).getAllRole();
    }

    @Test
    @DisplayName("Get all roles - Fail: bị chặn khi không có quyền admin, trả về forbidden")
    @WithMockUser(roles = "USER")
    void getAllRole_UserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/auth/roles").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(roleService, never()).getAllRole();
    }

    @Test
    @DisplayName("Get all roles - Fail: bị chặn khi chưa xác thực, trả về unauthenticated")
    void getAllRole_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/auth/roles").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(roleService, never()).getAllRole();
    }
}
