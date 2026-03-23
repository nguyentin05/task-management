package com.ntt.authentication.controller.external;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
import com.ntt.authentication.dto.response.PageResponse;
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

    @Nested
    @DisplayName("Get All Role: test hàm getAllRole")
    class GetAllRoleTest {

        @Test
        @DisplayName("Get All Role - Success: admin lấy danh sách role thành công, trả về PageResponse")
        @WithMockUser(roles = "ADMIN")
        void getAllRole_AdminRole_ShouldReturnPageResponse() throws Exception {
            PageResponse<RoleResponse> mockResponse = PageResponse.<RoleResponse>builder()
                    .currentPage(1)
                    .pageSize(5)
                    .totalPages(1)
                    .totalElements(2)
                    .data(List.of(
                            RoleResponse.builder()
                                    .name("ADMIN")
                                    .description("Quản trị viên")
                                    .build(),
                            RoleResponse.builder()
                                    .name("USER")
                                    .description("Người dùng")
                                    .build()))
                    .build();

            when(roleService.getAllRole(1, 5)).thenReturn(mockResponse);

            mockMvc.perform(get("/auth/roles")
                            .param("page", "1")
                            .param("size", "5")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.currentPage").value(1))
                    .andExpect(jsonPath("$.result.totalElements").value(2))
                    .andExpect(jsonPath("$.result.data").isArray())
                    .andExpect(jsonPath("$.result.data.size()").value(2))
                    .andExpect(jsonPath("$.result.data[0].name").value("ADMIN"))
                    .andExpect(jsonPath("$.result.data[1].name").value("USER"));

            verify(roleService, times(1)).getAllRole(1, 5);
        }

        @Test
        @DisplayName("Get All Role - Success: không truyền param, dùng default page=1 size=5")
        @WithMockUser(roles = "ADMIN")
        void getAllRole_DefaultParams_ShouldUseDefaults() throws Exception {
            PageResponse<RoleResponse> mockResponse = PageResponse.<RoleResponse>builder()
                    .currentPage(1)
                    .pageSize(5)
                    .totalPages(0)
                    .totalElements(0)
                    .data(List.of())
                    .build();

            when(roleService.getAllRole(1, 5)).thenReturn(mockResponse);

            mockMvc.perform(get("/auth/roles").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.currentPage").value(1))
                    .andExpect(jsonPath("$.result.data").isArray());

            verify(roleService, times(1)).getAllRole(1, 5);
        }

        @Test
        @DisplayName("Get All Role - Fail: bị chặn khi không có quyền admin, trả về forbidden")
        @WithMockUser(roles = "USER")
        void getAllRole_UserRole_ShouldReturnForbidden() throws Exception {
            mockMvc.perform(get("/auth/roles").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());

            verify(roleService, never()).getAllRole(anyInt(), anyInt());
        }

        @Test
        @DisplayName("Get All Role - Fail: bị chặn khi chưa xác thực, trả về unauthorized")
        void getAllRole_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(get("/auth/roles").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());

            verify(roleService, never()).getAllRole(anyInt(), anyInt());
        }
    }
}
