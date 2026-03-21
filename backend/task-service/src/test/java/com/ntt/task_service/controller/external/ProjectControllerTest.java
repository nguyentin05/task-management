package com.ntt.task_service.controller.external;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
import com.ntt.task_service.dto.request.ProjectCreationRequest;
import com.ntt.task_service.dto.request.ProjectUpdateRequest;
import com.ntt.task_service.dto.response.ProjectResponse;
import com.ntt.task_service.dto.response.ProjectStatisticsResponse;
import com.ntt.task_service.service.ProjectService;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = ProjectController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class ProjectControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    ProjectService projectService;

    @MockitoBean
    CustomJwtDecoder customJwtDecoder;

    @MockitoBean
    UserDetailsService userDetailsService;

    @Nested
    @DisplayName("Create Project: test hàm createProject")
    class CreateProjectTest {

        @Test
        @DisplayName("Create Project - Success: tạo project thành công, trả về ProjectResponse")
        @WithMockUser(username = "user-uuid-1234")
        void createProject_ValidRequest_ShouldReturnProjectResponse() throws Exception {
            ProjectCreationRequest request = ProjectCreationRequest.builder()
                    .name("Test Project")
                    .description("Test Description")
                    .startAt(Instant.now().plus(1, ChronoUnit.DAYS))
                    .endAt(Instant.now().plus(10, ChronoUnit.DAYS))
                    .build();

            ProjectResponse mockResponse = ProjectResponse.builder()
                    .id("project-uuid-1234")
                    .name("Test Project")
                    .build();

            when(projectService.createProject(any(ProjectCreationRequest.class)))
                    .thenReturn(mockResponse);

            mockMvc.perform(post("/projects")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.id").value("project-uuid-1234"))
                    .andExpect(jsonPath("$.result.name").value("Test Project"));

            verify(projectService, times(1)).createProject(any(ProjectCreationRequest.class));
        }

        @Test
        @DisplayName("Create Project - Fail: bị chặn khi chưa xác thực, trả về unauthorized")
        void createProject_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
            ProjectCreationRequest request = ProjectCreationRequest.builder()
                    .name("Test Project")
                    .startAt(Instant.now().plus(1, ChronoUnit.DAYS))
                    .endAt(Instant.now().plus(10, ChronoUnit.DAYS))
                    .build();

            mockMvc.perform(post("/projects")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());

            verify(projectService, never()).createProject(any());
        }

        static Stream<Arguments> provideInvalidCreationRequests() {
            Instant future = Instant.now().plus(1, ChronoUnit.DAYS);
            Instant future10 = Instant.now().plus(10, ChronoUnit.DAYS);
            String tooLongName = "test".repeat(64);

            return Stream.of(
                    Arguments.of(
                            ProjectCreationRequest.builder()
                                    .startAt(future)
                                    .endAt(future10)
                                    .build(),
                            3001),
                    Arguments.of(
                            ProjectCreationRequest.builder()
                                    .name("Test")
                                    .endAt(future10)
                                    .build(),
                            3001),
                    Arguments.of(
                            ProjectCreationRequest.builder()
                                    .name("Test")
                                    .startAt(future)
                                    .build(),
                            3001),
                    Arguments.of(
                            ProjectCreationRequest.builder()
                                    .name(tooLongName)
                                    .startAt(future)
                                    .endAt(future10)
                                    .build(),
                            3004),
                    Arguments.of(
                            ProjectCreationRequest.builder()
                                    .name("Test")
                                    .startAt(Instant.now().minus(1, ChronoUnit.DAYS))
                                    .endAt(future10)
                                    .build(),
                            3010));
        }

        @ParameterizedTest
        @MethodSource("provideInvalidCreationRequests")
        @WithMockUser
        @DisplayName("Create Project - Fail: bắt lỗi validation, trả về bad request")
        void createProject_InvalidRequest_ShouldReturnBadRequest(ProjectCreationRequest invalidRequest, int errorCode)
                throws Exception {
            mockMvc.perform(post("/projects")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(errorCode));

            verify(projectService, never()).createProject(any());
        }
    }

    @Nested
    @DisplayName("Get All Project: test hàm getAllProject")
    class GetAllProjectTest {

        @Test
        @DisplayName("Get All Project - Success: admin lấy danh sách project thành công, trả về List<ProjectResponse>")
        @WithMockUser(roles = "ADMIN")
        void getAllProject_AdminRole_ShouldReturnListProjects() throws Exception {
            ProjectResponse p1 = ProjectResponse.builder().id("project-uuid-1").build();
            ProjectResponse p2 = ProjectResponse.builder().id("project-uuid-2").build();

            when(projectService.getAllProject()).thenReturn(List.of(p1, p2));

            mockMvc.perform(get("/projects").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").isArray())
                    .andExpect(jsonPath("$.result.size()").value(2));

            verify(projectService, times(1)).getAllProject();
        }

        @Test
        @DisplayName("Get All Project - Fail: bị chặn khi không có quyền admin, trả về forbidden")
        @WithMockUser(roles = "USER")
        void getAllProject_UserRole_ShouldReturnForbidden() throws Exception {
            mockMvc.perform(get("/projects").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());

            verify(projectService, never()).getAllProject();
        }
    }

    @Nested
    @DisplayName("Get Project: test hàm getProject")
    class GetProjectTest {

        @Test
        @DisplayName("Get Project - Success: lấy chi tiết project thành công, trả về ProjectResponse")
        @WithMockUser(username = "user-uuid-1234")
        void getProject_Authenticated_ShouldReturnProjectResponse() throws Exception {
            String projectId = "project-uuid-1234";
            ProjectResponse mockResponse =
                    ProjectResponse.builder().id(projectId).name("Test Project").build();

            when(projectService.getProject(projectId)).thenReturn(mockResponse);

            mockMvc.perform(get("/projects/{projectId}", projectId).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.id").value(projectId))
                    .andExpect(jsonPath("$.result.name").value("Test Project"));

            verify(projectService, times(1)).getProject(projectId);
        }

        @Test
        @DisplayName("Get Project - Fail: bị chặn khi chưa xác thực, trả về unauthorized")
        void getProject_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(get("/projects/{projectId}", "project-uuid-1234").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());

            verify(projectService, never()).getProject(any());
        }
    }

    @Nested
    @DisplayName("Update Project: test hàm updateProject")
    class UpdateProjectTest {

        @Test
        @DisplayName("Update Project - Success: cập nhật project thành công, trả về ProjectResponse")
        @WithMockUser(username = "user-uuid-1234")
        void updateProject_ValidRequest_ShouldReturnProjectResponse() throws Exception {
            String projectId = "project-uuid-1234";
            ProjectUpdateRequest request =
                    ProjectUpdateRequest.builder().name("Updated Project").build();

            ProjectResponse mockResponse = ProjectResponse.builder()
                    .id(projectId)
                    .name("Updated Project")
                    .build();

            when(projectService.updateProject(eq(projectId), any(ProjectUpdateRequest.class)))
                    .thenReturn(mockResponse);

            mockMvc.perform(patch("/projects/{projectId}", projectId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.id").value(projectId))
                    .andExpect(jsonPath("$.result.name").value("Updated Project"));

            verify(projectService, times(1)).updateProject(eq(projectId), any(ProjectUpdateRequest.class));
        }

        @Test
        @DisplayName("Update Project - Fail: bị chặn khi chưa xác thực, trả về unauthorized")
        void updateProject_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(patch("/projects/{projectId}", "project-uuid-1234")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    ProjectUpdateRequest.builder().name("test").build())))
                    .andExpect(status().isUnauthorized());

            verify(projectService, never()).updateProject(any(), any());
        }

        static Stream<Arguments> provideInvalidUpdateRequests() {
            String tooLongName = "test".repeat(64);

            return Stream.of(
                    Arguments.of(
                            ProjectUpdateRequest.builder().name(tooLongName).build(), 3004),
                    Arguments.of(
                            ProjectUpdateRequest.builder()
                                    .startAt(Instant.now().minus(1, ChronoUnit.DAYS))
                                    .build(),
                            3010));
        }

        @ParameterizedTest
        @MethodSource("provideInvalidUpdateRequests")
        @WithMockUser
        @DisplayName("Update Project - Fail: bắt lỗi validation, trả về bad request")
        void updateProject_InvalidRequest_ShouldReturnBadRequest(ProjectUpdateRequest invalidRequest, int errorCode)
                throws Exception {
            mockMvc.perform(patch("/projects/{projectId}", "project-uuid-1234")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(errorCode));

            verify(projectService, never()).updateProject(any(), any());
        }
    }

    @Nested
    @DisplayName("Delete Project: test hàm deleteProject")
    class DeleteProjectTest {

        @Test
        @DisplayName("Delete Project - Success: xóa project thành công, trả về message")
        @WithMockUser(username = "user-uuid-1234")
        void deleteProject_Authenticated_ShouldReturnSuccessMessage() throws Exception {
            String projectId = "project-uuid-1234";

            doNothing().when(projectService).deleteProject(projectId);

            mockMvc.perform(delete("/projects/{projectId}", projectId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Xóa project thành công"));

            verify(projectService, times(1)).deleteProject(projectId);
        }

        @Test
        @DisplayName("Delete Project - Fail: bị chặn khi chưa xác thực, trả về unauthorized")
        void deleteProject_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(delete("/projects/{projectId}", "project-uuid-1234")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());

            verify(projectService, never()).deleteProject(any());
        }
    }

    @Nested
    @DisplayName("Get Project Statistics: test hàm getProjectStatistics")
    class GetProjectStatisticsTest {

        @Test
        @DisplayName(
                "Get Project Statistics - Success: lấy thống kê project thành công, trả về ProjectStatisticsResponse")
        @WithMockUser(username = "user-uuid-1234")
        void getProjectStatistics_Authenticated_ShouldReturnStatistics() throws Exception {
            String projectId = "project-uuid-1234";
            ProjectStatisticsResponse mockResponse = ProjectStatisticsResponse.builder()
                    .projectId(projectId)
                    .projectName("Test Project")
                    .totalTasks(10)
                    .completedTasks(5)
                    .completionRate(50.0)
                    .totalMembers(3)
                    .totalColumns(4)
                    .build();

            when(projectService.getProjectStatistics(projectId)).thenReturn(mockResponse);

            mockMvc.perform(get("/projects/{projectId}/statistics", projectId).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.projectId").value(projectId))
                    .andExpect(jsonPath("$.result.totalTasks").value(10))
                    .andExpect(jsonPath("$.result.completedTasks").value(5))
                    .andExpect(jsonPath("$.result.completionRate").value(50.0));

            verify(projectService, times(1)).getProjectStatistics(projectId);
        }

        @Test
        @DisplayName("Get Project Statistics - Fail: bị chặn khi chưa xác thực, trả về unauthorized")
        void getProjectStatistics_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(get("/projects/{projectId}/statistics", "project-uuid-1234")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());

            verify(projectService, never()).getProjectStatistics(any());
        }
    }
}
