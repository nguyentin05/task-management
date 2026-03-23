package com.ntt.task_service.controller.external;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ntt.task_service.configuration.CustomJwtDecoder;
import com.ntt.task_service.configuration.SecurityConfig;
import com.ntt.task_service.domain.ProjectRole;
import com.ntt.task_service.dto.request.ProjectMemberAddRequest;
import com.ntt.task_service.dto.request.RoleMemberUpdateRequest;
import com.ntt.task_service.dto.response.MemberSearchResponse;
import com.ntt.task_service.dto.response.PageResponse;
import com.ntt.task_service.dto.response.ProjectMemberResponse;
import com.ntt.task_service.service.ProjectMemberService;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = ProjectMemberController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class ProjectMemberControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    ProjectMemberService projectMemberService;

    @MockitoBean
    CustomJwtDecoder customJwtDecoder;

    @MockitoBean
    UserDetailsService userDetailsService;

    private static final String PROJECT_ID = "project-uuid-1234";
    private static final String USER_ID = "user-uuid-1234";

    @Nested
    @DisplayName("Get Members In Project: test hàm getMembersInProject")
    class GetMembersInProjectTest {

        @Test
        @DisplayName(
                "Get Members In Project - Success: lấy danh sách thành viên phân trang thành công, trả về PageResponse<ProjectMemberResponse>")
        @WithMockUser(username = USER_ID)
        void getMembersInProject_Authenticated_ShouldReturnPageResponse() throws Exception {
            ProjectMemberResponse member1 = ProjectMemberResponse.builder()
                    .userId("user-uuid-1")
                    .role(ProjectRole.MANAGER)
                    .build();
            ProjectMemberResponse member2 = ProjectMemberResponse.builder()
                    .userId("user-uuid-2")
                    .role(ProjectRole.MEMBER)
                    .build();

            int page = 1;
            int size = 20;

            PageResponse<ProjectMemberResponse> mockPageResponse = PageResponse.<ProjectMemberResponse>builder()
                    .currentPage(page)
                    .pageSize(size)
                    .totalElements(2)
                    .totalPages(1)
                    .data(List.of(member1, member2))
                    .build();

            when(projectMemberService.getMembersInProject(PROJECT_ID, page, size))
                    .thenReturn(mockPageResponse);

            mockMvc.perform(get("/projects/{projectId}/members", PROJECT_ID)
                            .param("page", String.valueOf(page))
                            .param("size", String.valueOf(size))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.currentPage").value(page))
                    .andExpect(jsonPath("$.result.pageSize").value(size))
                    .andExpect(jsonPath("$.result.totalElements").value(2))
                    .andExpect(jsonPath("$.result.data").isArray())
                    .andExpect(jsonPath("$.result.data.size()").value(2))
                    .andExpect(jsonPath("$.result.data[0].userId").value("user-uuid-1"));

            verify(projectMemberService, times(1)).getMembersInProject(PROJECT_ID, page, size);
        }

        @Test
        @DisplayName("Get Members In Project - Fail: bị chặn khi chưa xác thực, trả về unauthorized")
        void getMembersInProject_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(get("/projects/{projectId}/members", PROJECT_ID).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());

            verify(projectMemberService, never()).getMembersInProject(anyString(), anyInt(), anyInt());
        }
    }

    @Nested
    @DisplayName("Search Users To Invite: test hàm searchUsersToInvite")
    class SearchUsersToInviteTest {

        @Test
        @DisplayName(
                "Search Users To Invite - Success: tìm kiếm thành viên thành công, trả về List<MemberSearchResponse>")
        @WithMockUser(username = USER_ID)
        void searchUsersToInvite_Authenticated_ShouldReturnListMembers() throws Exception {
            MemberSearchResponse result1 = MemberSearchResponse.builder()
                    .userId("user-uuid-1")
                    .email("user1@example.com")
                    .build();

            when(projectMemberService.searchUsersToInvite(PROJECT_ID, "user1@example.com"))
                    .thenReturn(List.of(result1));

            mockMvc.perform(get("/projects/{projectId}/members/search", PROJECT_ID)
                            .param("email", "user1@example.com")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").isArray())
                    .andExpect(jsonPath("$.result.size()").value(1))
                    .andExpect(jsonPath("$.result[0].email").value("user1@example.com"));

            verify(projectMemberService, times(1)).searchUsersToInvite(PROJECT_ID, "user1@example.com");
        }

        @Test
        @DisplayName("Search Users To Invite - Fail: bị chặn khi chưa xác thực, trả về unauthorized")
        void searchUsersToInvite_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(get("/projects/{projectId}/members/search", PROJECT_ID)
                            .param("email", "test@example.com")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());

            verify(projectMemberService, never()).searchUsersToInvite(any(), any());
        }
    }

    @Nested
    @DisplayName("Add Member To Project: test hàm addMemberToProject")
    class AddMemberToProjectTest {

        @Test
        @DisplayName("Add Member To Project - Success: thêm thành viên thành công, trả về ProjectMemberResponse")
        @WithMockUser(username = USER_ID)
        void addMemberToProject_ValidRequest_ShouldReturnProjectMemberResponse() throws Exception {
            ProjectMemberAddRequest request = ProjectMemberAddRequest.builder()
                    .userId(USER_ID)
                    .role(ProjectRole.MEMBER)
                    .build();

            ProjectMemberResponse mockResponse = ProjectMemberResponse.builder()
                    .userId(USER_ID)
                    .role(ProjectRole.MEMBER)
                    .build();

            when(projectMemberService.addMemberToProject(eq(PROJECT_ID), any(ProjectMemberAddRequest.class)))
                    .thenReturn(mockResponse);

            mockMvc.perform(post("/projects/{projectId}/members", PROJECT_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.userId").value(USER_ID))
                    .andExpect(jsonPath("$.result.role").value("MEMBER"));

            verify(projectMemberService, times(1))
                    .addMemberToProject(eq(PROJECT_ID), any(ProjectMemberAddRequest.class));
        }

        @Test
        @DisplayName("Add Member To Project - Fail: bị chặn khi chưa xác thực, trả về unauthorized")
        void addMemberToProject_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
            ProjectMemberAddRequest request = ProjectMemberAddRequest.builder()
                    .userId(USER_ID)
                    .role(ProjectRole.MEMBER)
                    .build();

            mockMvc.perform(post("/projects/{projectId}/members", PROJECT_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());

            verify(projectMemberService, never()).addMemberToProject(any(), any());
        }

        static Stream<Arguments> provideInvalidAddMemberRequests() {
            return Stream.of(
                    Arguments.of(
                            ProjectMemberAddRequest.builder()
                                    .role(ProjectRole.MEMBER)
                                    .build(),
                            3001),
                    Arguments.of(
                            ProjectMemberAddRequest.builder().userId(USER_ID).build(), 3001));
        }

        @ParameterizedTest
        @MethodSource("provideInvalidAddMemberRequests")
        @WithMockUser
        @DisplayName("Add Member To Project - Fail: bắt lỗi validation, trả về bad request")
        void addMemberToProject_InvalidRequest_ShouldReturnBadRequest(
                ProjectMemberAddRequest invalidRequest, int errorCode) throws Exception {
            mockMvc.perform(post("/projects/{projectId}/members", PROJECT_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(errorCode));

            verify(projectMemberService, never()).addMemberToProject(any(), any());
        }
    }

    @Nested
    @DisplayName("Update Role Member In Project: test hàm updateRoleMemberInProject")
    class UpdateRoleMemberInProjectTest {

        @Test
        @DisplayName("Update Role Member In Project - Success: cập nhật role thành công, trả về ProjectMemberResponse")
        @WithMockUser(username = USER_ID)
        void updateRoleMemberInProject_ValidRequest_ShouldReturnProjectMemberResponse() throws Exception {
            RoleMemberUpdateRequest request =
                    RoleMemberUpdateRequest.builder().role(ProjectRole.MANAGER).build();

            ProjectMemberResponse mockResponse = ProjectMemberResponse.builder()
                    .userId(USER_ID)
                    .role(ProjectRole.MANAGER)
                    .build();

            when(projectMemberService.updateRoleMemberInProject(
                            eq(PROJECT_ID), eq(USER_ID), any(RoleMemberUpdateRequest.class)))
                    .thenReturn(mockResponse);

            mockMvc.perform(put("/projects/{projectId}/members/{userId}", PROJECT_ID, USER_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.userId").value(USER_ID))
                    .andExpect(jsonPath("$.result.role").value("MANAGER"));

            verify(projectMemberService, times(1))
                    .updateRoleMemberInProject(eq(PROJECT_ID), eq(USER_ID), any(RoleMemberUpdateRequest.class));
        }

        @Test
        @DisplayName("Update Role Member In Project - Fail: bị chặn khi chưa xác thực, trả về unauthorized")
        void updateRoleMemberInProject_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(put("/projects/{projectId}/members/{userId}", PROJECT_ID, USER_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(RoleMemberUpdateRequest.builder()
                                    .role(ProjectRole.MEMBER)
                                    .build())))
                    .andExpect(status().isUnauthorized());

            verify(projectMemberService, never()).updateRoleMemberInProject(any(), any(), any());
        }

        @Test
        @WithMockUser
        @DisplayName("Update Role Member In Project - Fail: thiếu role, trả về bad request")
        void updateRoleMemberInProject_MissingRole_ShouldReturnBadRequest() throws Exception {
            RoleMemberUpdateRequest invalidRequest =
                    RoleMemberUpdateRequest.builder().build();

            mockMvc.perform(put("/projects/{projectId}/members/{userId}", PROJECT_ID, USER_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(3001));

            verify(projectMemberService, never()).updateRoleMemberInProject(any(), any(), any());
        }
    }

    @Nested
    @DisplayName("Remove Member From Project: test hàm removeMemberFromProject")
    class RemoveMemberFromProjectTest {

        @Test
        @DisplayName("Remove Member From Project - Success: xóa thành viên thành công, trả về message")
        @WithMockUser(username = USER_ID)
        void removeMemberFromProject_Authenticated_ShouldReturnSuccessMessage() throws Exception {
            doNothing().when(projectMemberService).removeMemberFromProject(PROJECT_ID, USER_ID);

            mockMvc.perform(delete("/projects/{projectId}/members/{userId}", PROJECT_ID, USER_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Đã xóa thành viên thành công"));

            verify(projectMemberService, times(1)).removeMemberFromProject(PROJECT_ID, USER_ID);
        }

        @Test
        @DisplayName("Remove Member From Project - Fail: bị chặn khi chưa xác thực, trả về unauthorized")
        void removeMemberFromProject_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(delete("/projects/{projectId}/members/{userId}", PROJECT_ID, USER_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());

            verify(projectMemberService, never()).removeMemberFromProject(any(), any());
        }
    }
}
