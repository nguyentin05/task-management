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
import com.ntt.task_service.dto.request.WorkspaceUpdateRequest;
import com.ntt.task_service.dto.response.ProjectResponse;
import com.ntt.task_service.dto.response.WorkspaceResponse;
import com.ntt.task_service.service.WorkspaceService;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = WorkspaceController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class WorkspaceControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    WorkspaceService workspaceService;

    @MockitoBean
    CustomJwtDecoder customJwtDecoder;

    @MockitoBean
    UserDetailsService userDetailsService;

    @Nested
    @DisplayName("Get My Workspace: test hàm getMyWorkspace")
    class GetMyWorkspaceTest {

        @Test
        @DisplayName("Get My Workspace - Success: lấy workspace bản thân thành công, trả về WorkspaceResponse")
        @WithMockUser(username = "user-uuid-1234")
        void getMyWorkspace_Authenticated_ShouldReturnWorkspaceResponse() throws Exception {
            WorkspaceResponse mockResponse = WorkspaceResponse.builder()
                    .id("workspace-uuid-1234")
                    .name("My Workspace")
                    .build();

            when(workspaceService.getMyWorkspace()).thenReturn(mockResponse);

            mockMvc.perform(get("/workspaces/me").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.id").value("workspace-uuid-1234"))
                    .andExpect(jsonPath("$.result.name").value("My Workspace"));

            verify(workspaceService, times(1)).getMyWorkspace();
        }

        @Test
        @DisplayName("Get My Workspace - Fail: bị chặn khi chưa xác thực, trả về unauthorized")
        void getMyWorkspace_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(get("/workspaces/me").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());

            verify(workspaceService, never()).getMyWorkspace();
        }
    }

    @Nested
    @DisplayName("Get Projects In My Workspace: test hàm getProjectsInMyWorkspace")
    class GetProjectsInMyWorkspaceTest {

        @Test
        @DisplayName(
                "Get Projects In My Workspace - Success: lấy danh sách project thành công, trả về List<ProjectResponse>")
        @WithMockUser(username = "user-uuid-1234")
        void getProjectsInMyWorkspace_Authenticated_ShouldReturnListProjects() throws Exception {
            ProjectResponse project1 =
                    ProjectResponse.builder().id("project-uuid-1").build();
            ProjectResponse project2 =
                    ProjectResponse.builder().id("project-uuid-2").build();

            when(workspaceService.getProjectsInMyWorkspace()).thenReturn(List.of(project1, project2));

            mockMvc.perform(get("/workspaces/me/projects").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").isArray())
                    .andExpect(jsonPath("$.result.size()").value(2));

            verify(workspaceService, times(1)).getProjectsInMyWorkspace();
        }

        @Test
        @DisplayName("Get Projects In My Workspace - Fail: bị chặn khi chưa xác thực, trả về unauthorized")
        void getProjectsInMyWorkspace_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(get("/workspaces/me/projects").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());

            verify(workspaceService, never()).getProjectsInMyWorkspace();
        }
    }

    @Nested
    @DisplayName("Update My Workspace: test hàm updateMyWorkspace")
    class UpdateMyWorkspaceTest {

        @Test
        @DisplayName("Update My Workspace - Success: cập nhật workspace bản thân thành công, trả về WorkspaceResponse")
        @WithMockUser(username = "user-uuid-1234")
        void updateMyWorkspace_ValidRequest_ShouldReturnWorkspaceResponse() throws Exception {
            WorkspaceUpdateRequest request = WorkspaceUpdateRequest.builder()
                    .name("Updated Workspace")
                    .description("Updated description")
                    .build();

            WorkspaceResponse mockResponse = WorkspaceResponse.builder()
                    .id("workspace-uuid-1234")
                    .name("Updated Workspace")
                    .build();

            when(workspaceService.updateMyWorkspace(any(WorkspaceUpdateRequest.class)))
                    .thenReturn(mockResponse);

            mockMvc.perform(patch("/workspaces/me")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.id").value("workspace-uuid-1234"))
                    .andExpect(jsonPath("$.result.name").value("Updated Workspace"));

            verify(workspaceService, times(1)).updateMyWorkspace(any(WorkspaceUpdateRequest.class));
        }

        @Test
        @DisplayName("Update My Workspace - Fail: bị chặn khi chưa xác thực, trả về unauthorized")
        void updateMyWorkspace_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(patch("/workspaces/me")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(WorkspaceUpdateRequest.builder()
                                    .name("test")
                                    .build())))
                    .andExpect(status().isUnauthorized());

            verify(workspaceService, never()).updateMyWorkspace(any());
        }

        static Stream<Arguments> provideInvalidUpdateRequests() {
            String tooLongString = "test".repeat(64);
            return Stream.of(Arguments.of(
                    WorkspaceUpdateRequest.builder().name(tooLongString).build(), 3004));
        }

        @ParameterizedTest
        @MethodSource("provideInvalidUpdateRequests")
        @WithMockUser
        @DisplayName("Update My Workspace - Fail: bắt lỗi validation, trả về bad request")
        void updateMyWorkspace_InvalidRequest_ShouldReturnBadRequest(
                WorkspaceUpdateRequest invalidRequest, int errorCode) throws Exception {
            mockMvc.perform(patch("/workspaces/me")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(errorCode));

            verify(workspaceService, never()).updateMyWorkspace(any());
        }
    }

    @Nested
    @DisplayName("Delete Project In My Workspace: test hàm deleteProjectInMyWorkspace")
    class DeleteProjectInMyWorkspaceTest {

        @Test
        @DisplayName("Delete Project In My Workspace - Success: xóa project thành công, trả về message")
        @WithMockUser(username = "user-uuid-1234")
        void deleteProjectInMyWorkspace_Authenticated_ShouldReturnSuccessMessage() throws Exception {
            doNothing().when(workspaceService).deleteProjectInMyWorkspace("project-uuid-1234");

            mockMvc.perform(delete("/workspaces/me/projects/{projectId}", "project-uuid-1234")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Xóa dự án khỏi không gian làm việc thành công"));

            verify(workspaceService, times(1)).deleteProjectInMyWorkspace("project-uuid-1234");
        }

        @Test
        @DisplayName("Delete Project In My Workspace - Fail: bị chặn khi chưa xác thực, trả về unauthorized")
        void deleteProjectInMyWorkspace_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(delete("/workspaces/me/projects/{projectId}", "project-uuid-1234")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());

            verify(workspaceService, never()).deleteProjectInMyWorkspace(any());
        }
    }

    @Nested
    @DisplayName("Get All Workspace: test hàm getAllWorkspace")
    class GetAllWorkspaceTest {

        @Test
        @DisplayName(
                "Get All Workspace - Success: admin lấy danh sách workspace thành công, trả về List<WorkspaceResponse>")
        @WithMockUser(roles = "ADMIN")
        void getAllWorkspace_AdminRole_ShouldReturnListWorkspaces() throws Exception {
            WorkspaceResponse ws1 = WorkspaceResponse.builder().id("ws-uuid-1").build();
            WorkspaceResponse ws2 = WorkspaceResponse.builder().id("ws-uuid-2").build();

            when(workspaceService.getAllWorkspace()).thenReturn(List.of(ws1, ws2));

            mockMvc.perform(get("/workspaces").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").isArray())
                    .andExpect(jsonPath("$.result.size()").value(2));

            verify(workspaceService, times(1)).getAllWorkspace();
        }

        @Test
        @DisplayName("Get All Workspace - Fail: bị chặn khi không có quyền admin, trả về forbidden")
        @WithMockUser(roles = "USER")
        void getAllWorkspace_UserRole_ShouldReturnForbidden() throws Exception {
            mockMvc.perform(get("/workspaces").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());

            verify(workspaceService, never()).getAllWorkspace();
        }
    }

    @Nested
    @DisplayName("Get Workspace: test hàm getWorkspace")
    class GetWorkspaceTest {

        @Test
        @DisplayName("Get Workspace - Success: admin lấy chi tiết workspace thành công, trả về WorkspaceResponse")
        @WithMockUser(roles = "ADMIN")
        void getWorkspace_AdminRole_ShouldReturnWorkspaceResponse() throws Exception {
            String workspaceId = "workspace-uuid-1234";
            WorkspaceResponse mockResponse = WorkspaceResponse.builder()
                    .id(workspaceId)
                    .name("Test Workspace")
                    .build();

            when(workspaceService.getWorkspace(workspaceId)).thenReturn(mockResponse);

            mockMvc.perform(get("/workspaces/{workspaceId}", workspaceId).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.id").value(workspaceId))
                    .andExpect(jsonPath("$.result.name").value("Test Workspace"));

            verify(workspaceService, times(1)).getWorkspace(workspaceId);
        }

        @Test
        @DisplayName("Get Workspace - Fail: bị chặn khi không có quyền admin, trả về forbidden")
        @WithMockUser(roles = "USER")
        void getWorkspace_UserRole_ShouldReturnForbidden() throws Exception {
            mockMvc.perform(get("/workspaces/{workspaceId}", "workspace-uuid-1234")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());

            verify(workspaceService, never()).getWorkspace(any());
        }
    }

    @Nested
    @DisplayName("Get Projects In Workspace: test hàm getProjectsInWorkspace")
    class GetProjectsInWorkspaceTest {

        @Test
        @DisplayName(
                "Get Projects In Workspace - Success: admin lấy danh sách project thành công, trả về List<ProjectResponse>")
        @WithMockUser(roles = "ADMIN")
        void getProjectsInWorkspace_AdminRole_ShouldReturnListProjects() throws Exception {
            String workspaceId = "workspace-uuid-1234";
            ProjectResponse project1 =
                    ProjectResponse.builder().id("project-uuid-1").build();
            ProjectResponse project2 =
                    ProjectResponse.builder().id("project-uuid-2").build();

            when(workspaceService.getProjectsInWorkspace(workspaceId)).thenReturn(List.of(project1, project2));

            mockMvc.perform(get("/workspaces/{workspaceId}/projects", workspaceId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").isArray())
                    .andExpect(jsonPath("$.result.size()").value(2));

            verify(workspaceService, times(1)).getProjectsInWorkspace(workspaceId);
        }

        @Test
        @DisplayName("Get Projects In Workspace - Fail: bị chặn khi không có quyền admin, trả về forbidden")
        @WithMockUser(roles = "USER")
        void getProjectsInWorkspace_UserRole_ShouldReturnForbidden() throws Exception {
            mockMvc.perform(get("/workspaces/{workspaceId}/projects", "workspace-uuid-1234")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());

            verify(workspaceService, never()).getProjectsInWorkspace(any());
        }
    }

    @Nested
    @DisplayName("Update Workspace: test hàm updateWorkspace")
    class UpdateWorkspaceTest {

        @Test
        @DisplayName("Update Workspace - Success: admin cập nhật workspace thành công, trả về WorkspaceResponse")
        @WithMockUser(roles = "ADMIN")
        void updateWorkspace_AdminRole_ValidRequest_ShouldReturnWorkspaceResponse() throws Exception {
            String workspaceId = "workspace-uuid-1234";
            WorkspaceUpdateRequest request =
                    WorkspaceUpdateRequest.builder().name("Updated Workspace").build();

            WorkspaceResponse mockResponse = WorkspaceResponse.builder()
                    .id(workspaceId)
                    .name("Updated Workspace")
                    .build();

            when(workspaceService.updateWorkspace(eq(workspaceId), any(WorkspaceUpdateRequest.class)))
                    .thenReturn(mockResponse);

            mockMvc.perform(patch("/workspaces/{workspaceId}", workspaceId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.id").value(workspaceId))
                    .andExpect(jsonPath("$.result.name").value("Updated Workspace"));

            verify(workspaceService, times(1)).updateWorkspace(eq(workspaceId), any(WorkspaceUpdateRequest.class));
        }

        @Test
        @DisplayName("Update Workspace - Fail: bị chặn khi không có quyền admin, trả về forbidden")
        @WithMockUser(roles = "USER")
        void updateWorkspace_UserRole_ShouldReturnForbidden() throws Exception {
            mockMvc.perform(patch("/workspaces/{workspaceId}", "workspace-uuid-1234")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(WorkspaceUpdateRequest.builder()
                                    .name("test")
                                    .build())))
                    .andExpect(status().isForbidden());

            verify(workspaceService, never()).updateWorkspace(any(), any());
        }
    }

    @Nested
    @DisplayName("Delete Project In Workspace: test hàm deleteProjectInWorkspace")
    class DeleteProjectInWorkspaceTest {

        @Test
        @DisplayName("Delete Project In Workspace - Success: admin xóa project thành công, trả về message")
        @WithMockUser(roles = "ADMIN")
        void deleteProjectInWorkspace_AdminRole_ShouldReturnSuccessMessage() throws Exception {
            String workspaceId = "workspace-uuid-1234";
            String projectId = "project-uuid-1234";

            doNothing().when(workspaceService).deleteProjectInWorkspace(workspaceId, projectId);

            mockMvc.perform(delete("/workspaces/{workspaceId}/projects/{projectId}", workspaceId, projectId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Xóa dự án khỏi không gian làm việc thành công"));

            verify(workspaceService, times(1)).deleteProjectInWorkspace(workspaceId, projectId);
        }

        @Test
        @DisplayName("Delete Project In Workspace - Fail: bị chặn khi không có quyền admin, trả về forbidden")
        @WithMockUser(roles = "USER")
        void deleteProjectInWorkspace_UserRole_ShouldReturnForbidden() throws Exception {
            mockMvc.perform(delete(
                                    "/workspaces/{workspaceId}/projects/{projectId}",
                                    "workspace-uuid-1234",
                                    "project-uuid-1234")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());

            verify(workspaceService, never()).deleteProjectInWorkspace(any(), any());
        }
    }
}
