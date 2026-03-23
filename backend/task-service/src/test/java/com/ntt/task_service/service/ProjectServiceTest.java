package com.ntt.task_service.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import com.ntt.task_service.dto.response.PageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.ntt.task_service.domain.Project;
import com.ntt.task_service.domain.ProjectMember;
import com.ntt.task_service.domain.Workspace;
import com.ntt.task_service.dto.request.ProjectCreationRequest;
import com.ntt.task_service.dto.request.ProjectUpdateRequest;
import com.ntt.task_service.dto.response.ProjectResponse;
import com.ntt.task_service.dto.response.ProjectStatisticsResponse;
import com.ntt.task_service.exception.AppException;
import com.ntt.task_service.exception.ErrorCode;
import com.ntt.task_service.mapper.ProjectMapper;
import com.ntt.task_service.repository.ColumnRepository;
import com.ntt.task_service.repository.ProjectMemberRepository;
import com.ntt.task_service.repository.ProjectRepository;
import com.ntt.task_service.repository.TaskRepository;
import com.ntt.task_service.repository.WorkspaceRepository;
import org.springframework.data.domain.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    ProjectRepository projectRepository;

    @Mock
    WorkspaceRepository workspaceRepository;

    @Mock
    ProjectMapper projectMapper;

    @Mock
    ProjectMemberRepository projectMemberRepository;

    @Mock
    ProjectAuthorizationService projectAuthorizationService;

    @Mock
    ColumnRepository columnRepository;

    @Mock
    TaskRepository taskRepository;

    @InjectMocks
    ProjectService projectService;

    private static final String USER_ID = "user-uuid-1234";
    private static final String PROJECT_ID = "project-uuid-1234";
    private static final String WORKSPACE_ID = "workspace-uuid-1234";

    private Project project;
    private Workspace workspace;

    @BeforeEach
    void setUpGlobal() {
        workspace = Workspace.builder()
                .id(WORKSPACE_ID)
                .userId(USER_ID)
                .projects(new HashSet<>())
                .build();

        project = Project.builder()
                .id(PROJECT_ID)
                .name("Test Project")
                .createdBy(USER_ID)
                .workspaces(new HashSet<>())
                .build();
    }

    @Nested
    @DisplayName("Scenario - Fail: project không tồn tại")
    @MockitoSettings(strictness = Strictness.LENIENT)
    class ProjectNotFoundScenario {

        @BeforeEach
        void setupScenario() {
            when(projectRepository.findById(anyString())).thenReturn(Optional.empty());
        }

        @Test
        @DisplayName("Get Project: trả về lỗi PROJECT_NOT_FOUND")
        void getProjectTest() {
            assertThatThrownBy(() -> projectService.getProject(PROJECT_ID))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PROJECT_NOT_FOUND);
        }

        @Test
        @DisplayName("Update Project: trả về lỗi PROJECT_NOT_FOUND")
        void updateProjectTest() {
            assertThatThrownBy(() -> projectService.updateProject(
                    PROJECT_ID,
                    ProjectUpdateRequest.builder().name("new").build()))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PROJECT_NOT_FOUND);

            verify(projectRepository, never()).save(any());
        }

        @Test
        @DisplayName("Delete Project: trả về lỗi PROJECT_NOT_FOUND")
        void deleteProjectTest() {
            assertThatThrownBy(() -> projectService.deleteProject(PROJECT_ID))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PROJECT_NOT_FOUND);

            verify(projectRepository, never()).delete(any());
        }

        @Test
        @DisplayName("Get Project Statistics: trả về lỗi PROJECT_NOT_FOUND")
        void getProjectStatisticsTest() {
            assertThatThrownBy(() -> projectService.getProjectStatistics(PROJECT_ID))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PROJECT_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("Create Project: test hàm createProject")
    class CreateProjectTest {

        @Test
        @DisplayName("Success: tạo project thành công, trả về ProjectResponse")
        void createProject_ValidRequest_ShouldReturnProjectResponse() {
            ProjectCreationRequest request =
                    ProjectCreationRequest.builder().name("Test Project").build();

            ProjectResponse mockResponse = ProjectResponse.builder()
                    .id(PROJECT_ID)
                    .name("Test Project")
                    .build();

            when(projectAuthorizationService.getCurrentUserId()).thenReturn(USER_ID);
            when(workspaceRepository.findByUserId(USER_ID)).thenReturn(Optional.of(workspace));
            when(projectMapper.toProject(request)).thenReturn(project);
            when(projectRepository.save(any(Project.class))).thenReturn(project);
            when(projectMemberRepository.save(any(ProjectMember.class))).thenReturn(mock(ProjectMember.class));
            when(projectMapper.toProjectResponse(project)).thenReturn(mockResponse);

            ProjectResponse response = projectService.createProject(request);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(PROJECT_ID);
            assertThat(response.getName()).isEqualTo("Test Project");

            verify(projectRepository, times(1)).save(any(Project.class));
            verify(projectMemberRepository, times(1)).save(any(ProjectMember.class));
        }

        @Test
        @DisplayName("Fail: workspace không tồn tại, trả về lỗi WORKSPACE_NOT_FOUND")
        void createProject_WorkspaceNotFound_ShouldThrow() {
            when(projectAuthorizationService.getCurrentUserId()).thenReturn(USER_ID);
            when(workspaceRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> projectService.createProject(
                    ProjectCreationRequest.builder().name("Test").build()))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.WORKSPACE_NOT_FOUND);

            verify(projectRepository, never()).save(any());
            verify(projectMemberRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Get All Project: test hàm getAllProject")
    class GetAllProjectTest {

        int page = 1;
        int size = 10;
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());

        @Test
        @DisplayName("Success: lấy tất cả project phân trang thành công, trả về PageResponse<ProjectResponse>")
        void getAllProject_ShouldReturnPageResponse() {
            Project project2 = Project.builder().id("project-uuid-5678").name("Project 2").build();
            ProjectResponse res1 = ProjectResponse.builder().id(PROJECT_ID).build();
            ProjectResponse res2 = ProjectResponse.builder().id("project-uuid-5678").build();

            Page<Project> pageResult = new PageImpl<>(List.of(project, project2), pageable, 2);

            when(projectRepository.findAll(any(Pageable.class))).thenReturn(pageResult);
            when(projectMapper.toProjectResponse(project)).thenReturn(res1);
            when(projectMapper.toProjectResponse(project2)).thenReturn(res2);

            PageResponse<ProjectResponse> result = projectService.getAllProject(page, size);

            assertThat(result.getCurrentPage()).isEqualTo(page);
            assertThat(result.getPageSize()).isEqualTo(size);
            assertThat(result.getTotalElements()).isEqualTo(2);

            assertThat(result.getData()).hasSize(2);

            verify(projectRepository, times(1)).findAll(any(Pageable.class));
        }

        @Test
        @DisplayName("Success: không có project nào, trả về data rỗng")
        void getAllProject_Empty_ShouldReturnEmptyPage() {
            Page<Project> emptyPage = new PageImpl<>(List.of(), pageable, 0);

            when(projectRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

            PageResponse<ProjectResponse> result = projectService.getAllProject(page, size);

            assertThat(result.getData()).isEmpty();
            assertThat(result.getTotalElements()).isEqualTo(0);

            verify(projectRepository, times(1)).findAll(any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("Get Project: test hàm getProject")
    class GetProjectTest {

        @Test
        @DisplayName("Success: lấy chi tiết project thành công, trả về ProjectResponse")
        void getProject_ValidId_ShouldReturnProjectResponse() {
            ProjectResponse mockResponse = ProjectResponse.builder()
                    .id(PROJECT_ID)
                    .name("Test Project")
                    .build();

            when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
            doNothing().when(projectAuthorizationService).validateCanView(PROJECT_ID);
            when(projectMapper.toProjectResponse(project)).thenReturn(mockResponse);

            ProjectResponse response = projectService.getProject(PROJECT_ID);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(PROJECT_ID);

            verify(projectAuthorizationService, times(1)).validateCanView(PROJECT_ID);
        }

        @Test
        @DisplayName("Fail: không có quyền xem, trả về lỗi ACCESS_DENIED")
        void getProject_AccessDenied_ShouldThrow() {
            when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
            doThrow(new AppException(ErrorCode.ACCESS_DENIED))
                    .when(projectAuthorizationService)
                    .validateCanView(PROJECT_ID);

            assertThatThrownBy(() -> projectService.getProject(PROJECT_ID))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED);
        }
    }

    @Nested
    @DisplayName("Update Project: test hàm updateProject")
    class UpdateProjectTest {

        @Test
        @DisplayName("Success: cập nhật project thành công, trả về ProjectResponse")
        void updateProject_ValidRequest_ShouldReturnProjectResponse() {
            ProjectUpdateRequest request =
                    ProjectUpdateRequest.builder().name("Updated Project").build();

            ProjectResponse mockResponse = ProjectResponse.builder()
                    .id(PROJECT_ID)
                    .name("Updated Project")
                    .build();

            when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
            doNothing().when(projectAuthorizationService).validateCanManage(PROJECT_ID);
            when(projectRepository.save(any(Project.class))).thenReturn(project);
            when(projectMapper.toProjectResponse(project)).thenReturn(mockResponse);

            ProjectResponse response = projectService.updateProject(PROJECT_ID, request);

            assertThat(response).isNotNull();
            assertThat(response.getName()).isEqualTo("Updated Project");

            verify(projectMapper, times(1)).updateProject(eq(project), eq(request));
            verify(projectRepository, times(1)).save(any(Project.class));
        }

        @Test
        @DisplayName("Fail: không có quyền quản lý, trả về lỗi ACCESS_DENIED")
        void updateProject_AccessDenied_ShouldThrow() {
            when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
            doThrow(new AppException(ErrorCode.ACCESS_DENIED))
                    .when(projectAuthorizationService)
                    .validateCanManage(PROJECT_ID);

            assertThatThrownBy(() -> projectService.updateProject(
                    PROJECT_ID,
                    ProjectUpdateRequest.builder().name("new").build()))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED);

            verify(projectRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Delete Project: test hàm deleteProject")
    class DeleteProjectTest {

        @Test
        @DisplayName("Success: xóa project thành công")
        void deleteProject_ValidId_ShouldDeleteProject() {
            when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
            doNothing().when(projectAuthorizationService).validateCanManage(PROJECT_ID);

            assertThatCode(() -> projectService.deleteProject(PROJECT_ID)).doesNotThrowAnyException();

            verify(projectRepository, times(1)).delete(project);
        }

        @Test
        @DisplayName("Fail: không có quyền quản lý, trả về lỗi ACCESS_DENIED")
        void deleteProject_AccessDenied_ShouldThrow() {
            when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
            doThrow(new AppException(ErrorCode.ACCESS_DENIED))
                    .when(projectAuthorizationService)
                    .validateCanManage(PROJECT_ID);

            assertThatThrownBy(() -> projectService.deleteProject(PROJECT_ID))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED);

            verify(projectRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("Get Project Statistics: test hàm getProjectStatistics")
    class GetProjectStatisticsTest {

        @Test
        @DisplayName("Success: lấy thống kê thành công với đầy đủ tasks")
        void getProjectStatistics_WithTasks_ShouldReturnCorrectStatistics() {
            List<String> columnIds = List.of("col-uuid-1", "col-uuid-2");

            when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
            doNothing().when(projectAuthorizationService).validateCanView(PROJECT_ID);
            when(columnRepository.findColumnIdsByProjectId(PROJECT_ID)).thenReturn(columnIds);
            when(taskRepository.countByColumnIdIn(columnIds)).thenReturn(10L);
            when(taskRepository.countByColumnIdInAndCompletedAtIsNotNull(columnIds))
                    .thenReturn(5L);
            when(projectMemberRepository.countByProjectId(PROJECT_ID)).thenReturn(3L);

            ProjectStatisticsResponse response = projectService.getProjectStatistics(PROJECT_ID);

            assertThat(response).isNotNull();
            assertThat(response.getProjectId()).isEqualTo(PROJECT_ID);
            assertThat(response.getProjectName()).isEqualTo("Test Project");
            assertThat(response.getTotalTasks()).isEqualTo(10);
            assertThat(response.getCompletedTasks()).isEqualTo(5);
            assertThat(response.getCompletionRate()).isEqualTo(50.0);
            assertThat(response.getTotalMembers()).isEqualTo(3);
            assertThat(response.getTotalColumns()).isEqualTo(2);
        }

        @Test
        @DisplayName("Success: không có task nào, completionRate = 0")
        void getProjectStatistics_NoTasks_ShouldReturnZeroCompletionRate() {
            List<String> columnIds = List.of("col-uuid-1");

            when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
            doNothing().when(projectAuthorizationService).validateCanView(PROJECT_ID);
            when(columnRepository.findColumnIdsByProjectId(PROJECT_ID)).thenReturn(columnIds);
            when(taskRepository.countByColumnIdIn(columnIds)).thenReturn(0L);
            when(taskRepository.countByColumnIdInAndCompletedAtIsNotNull(columnIds))
                    .thenReturn(0L);
            when(projectMemberRepository.countByProjectId(PROJECT_ID)).thenReturn(1L);

            ProjectStatisticsResponse response = projectService.getProjectStatistics(PROJECT_ID);

            assertThat(response.getTotalTasks()).isZero();
            assertThat(response.getCompletionRate()).isEqualTo(0.0);
        }

        @Test
        @DisplayName("Success: tất cả task đã hoàn thành, completionRate = 100")
        void getProjectStatistics_AllTasksCompleted_ShouldReturn100CompletionRate() {
            List<String> columnIds = List.of("col-uuid-1");

            when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
            doNothing().when(projectAuthorizationService).validateCanView(PROJECT_ID);
            when(columnRepository.findColumnIdsByProjectId(PROJECT_ID)).thenReturn(columnIds);
            when(taskRepository.countByColumnIdIn(columnIds)).thenReturn(4L);
            when(taskRepository.countByColumnIdInAndCompletedAtIsNotNull(columnIds))
                    .thenReturn(4L);
            when(projectMemberRepository.countByProjectId(PROJECT_ID)).thenReturn(2L);

            ProjectStatisticsResponse response = projectService.getProjectStatistics(PROJECT_ID);

            assertThat(response.getCompletionRate()).isEqualTo(100.0);
        }

        @Test
        @DisplayName("Fail: không có quyền xem, trả về lỗi ACCESS_DENIED")
        void getProjectStatistics_AccessDenied_ShouldThrow() {
            when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
            doThrow(new AppException(ErrorCode.ACCESS_DENIED))
                    .when(projectAuthorizationService)
                    .validateCanView(PROJECT_ID);

            assertThatThrownBy(() -> projectService.getProjectStatistics(PROJECT_ID))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED);
        }
    }
}
