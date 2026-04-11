package com.ntt.task_service.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ntt.task_service.domain.Project;
import com.ntt.task_service.domain.Workspace;
import com.ntt.task_service.dto.request.WorkspaceCreationRequest;
import com.ntt.task_service.dto.request.WorkspaceUpdateRequest;
import com.ntt.task_service.dto.response.PageResponse;
import com.ntt.task_service.dto.response.ProjectResponse;
import com.ntt.task_service.dto.response.WorkspaceResponse;
import com.ntt.task_service.exception.AppException;
import com.ntt.task_service.exception.ErrorCode;
import com.ntt.task_service.mapper.ProjectMapper;
import com.ntt.task_service.mapper.WorkspaceMapper;
import com.ntt.task_service.repository.ProjectRepository;
import com.ntt.task_service.repository.WorkspaceRepository;

@ExtendWith(MockitoExtension.class)
class WorkspaceServiceTest {

    @Mock
    WorkspaceRepository workspaceRepository;

    @Mock
    WorkspaceMapper workspaceMapper;

    @Mock
    ProjectRepository projectRepository;

    @Mock
    ProjectMapper projectMapper;

    @InjectMocks
    WorkspaceService workspaceService;

    private static final String USER_ID = "user-uuid-1234";
    private static final String WORKSPACE_ID = "workspace-uuid-1234";
    private static final String PROJECT_ID = "project-uuid-1234";

    private Workspace workspace;
    private Project project;

    @BeforeEach
    void setUpGlobal() {
        workspace = Workspace.builder()
                .id(WORKSPACE_ID)
                .userId(USER_ID)
                .name("My Workspace")
                .projects(new HashSet<>())
                .build();

        project = Project.builder()
                .id(PROJECT_ID)
                .name("Test Project")
                .createdBy(USER_ID)
                .workspaces(new HashSet<>())
                .build();

        workspace.getProjects().add(project);
        project.getWorkspaces().add(workspace);
    }

    private void mockSecurityContext(MockedStatic<SecurityContextHolder> mocked, String userId) {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        mocked.when(SecurityContextHolder::getContext).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(userId);
    }

    @Nested
    @DisplayName("Scenario - Fail: workspace không tồn tại")
    @MockitoSettings(strictness = Strictness.LENIENT)
    class WorkspaceNotFoundScenario {

        @BeforeEach
        void setupScenario() {
            when(workspaceRepository.findByUserId(anyString())).thenReturn(Optional.empty());
            when(workspaceRepository.findById(anyString())).thenReturn(Optional.empty());
        }

        @Test
        @DisplayName("Get My Workspace: trả về lỗi WORKSPACE_NOT_FOUND")
        void getMyWorkspaceTest() {
            try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
                mockSecurityContext(mocked, USER_ID);

                assertThatThrownBy(() -> workspaceService.getMyWorkspace())
                        .isInstanceOf(AppException.class)
                        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.WORKSPACE_NOT_FOUND);
            }
        }

        @Test
        @DisplayName("Get Projects In My Workspace: trả về lỗi WORKSPACE_NOT_FOUND")
        void getProjectsInMyWorkspaceTest() {
            try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
                mockSecurityContext(mocked, USER_ID);

                when(workspaceRepository.existsByUserId(USER_ID)).thenReturn(false);

                assertThatThrownBy(() -> workspaceService.getProjectsInMyWorkspace(1, 10))
                        .isInstanceOf(AppException.class)
                        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.WORKSPACE_NOT_FOUND);
            }
        }

        @Test
        @DisplayName("Update My Workspace: trả về lỗi WORKSPACE_NOT_FOUND")
        void updateMyWorkspaceTest() {
            try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
                mockSecurityContext(mocked, USER_ID);

                assertThatThrownBy(() -> workspaceService.updateMyWorkspace(
                                WorkspaceUpdateRequest.builder().name("new").build()))
                        .isInstanceOf(AppException.class)
                        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.WORKSPACE_NOT_FOUND);
            }
        }

        @Test
        @DisplayName("Delete Project In My Workspace: trả về lỗi WORKSPACE_NOT_FOUND")
        void deleteProjectInMyWorkspaceTest() {
            try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
                mockSecurityContext(mocked, USER_ID);

                assertThatThrownBy(() -> workspaceService.deleteProjectInMyWorkspace(PROJECT_ID))
                        .isInstanceOf(AppException.class)
                        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.WORKSPACE_NOT_FOUND);
            }
        }

        @Test
        @DisplayName("Get Workspace: trả về lỗi WORKSPACE_NOT_FOUND")
        void getWorkspaceTest() {
            assertThatThrownBy(() -> workspaceService.getWorkspace(WORKSPACE_ID))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.WORKSPACE_NOT_FOUND);
        }

        @Test
        @DisplayName("Get Projects In Workspace: trả về lỗi WORKSPACE_NOT_FOUND")
        void getProjectsInWorkspaceTest() {
            when(workspaceRepository.existsById(WORKSPACE_ID)).thenReturn(false);

            assertThatThrownBy(() -> workspaceService.getProjectsInWorkspace(WORKSPACE_ID, 1, 10))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.WORKSPACE_NOT_FOUND);
        }

        @Test
        @DisplayName("Update Workspace: trả về lỗi WORKSPACE_NOT_FOUND")
        void updateWorkspaceTest() {
            assertThatThrownBy(() -> workspaceService.updateWorkspace(
                            WORKSPACE_ID,
                            WorkspaceUpdateRequest.builder().name("new").build()))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.WORKSPACE_NOT_FOUND);
        }

        @Test
        @DisplayName("Delete Project In Workspace: trả về lỗi WORKSPACE_NOT_FOUND")
        void deleteProjectInWorkspaceTest() {
            assertThatThrownBy(() -> workspaceService.deleteProjectInWorkspace(WORKSPACE_ID, PROJECT_ID))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.WORKSPACE_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("Create: test hàm create")
    class CreateTest {

        @Test
        @DisplayName("Success: tạo workspace thành công, trả về WorkspaceResponse")
        void create_ValidRequest_ShouldReturnWorkspaceResponse() {
            WorkspaceCreationRequest request =
                    WorkspaceCreationRequest.builder().name("My Workspace").build();

            WorkspaceResponse mockResponse = WorkspaceResponse.builder()
                    .id(WORKSPACE_ID)
                    .name("My Workspace")
                    .build();

            when(workspaceMapper.toWorkspace(request)).thenReturn(workspace);
            when(workspaceRepository.save(any(Workspace.class))).thenReturn(workspace);
            when(workspaceMapper.toWorkspaceResponse(workspace)).thenReturn(mockResponse);

            WorkspaceResponse response = workspaceService.create(request);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(WORKSPACE_ID);
            assertThat(response.getName()).isEqualTo("My Workspace");

            verify(workspaceRepository, times(1)).save(any(Workspace.class));
        }
    }

    @Nested
    @DisplayName("Get My Workspace: test hàm getMyWorkspace")
    class GetMyWorkspaceTest {

        @Test
        @DisplayName("Success: lấy workspace bản thân thành công, trả về WorkspaceResponse")
        void getMyWorkspace_Authenticated_ShouldReturnWorkspaceResponse() {
            WorkspaceResponse mockResponse = WorkspaceResponse.builder()
                    .id(WORKSPACE_ID)
                    .name("My Workspace")
                    .build();

            try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
                mockSecurityContext(mocked, USER_ID);

                when(workspaceRepository.findByUserId(USER_ID)).thenReturn(Optional.of(workspace));
                when(workspaceMapper.toWorkspaceResponse(workspace)).thenReturn(mockResponse);

                WorkspaceResponse response = workspaceService.getMyWorkspace();

                assertThat(response).isNotNull();
                assertThat(response.getId()).isEqualTo(WORKSPACE_ID);

                verify(workspaceRepository, times(1)).findByUserId(USER_ID);
            }
        }
    }

    @Nested
    @DisplayName("Get Projects In My Workspace: test hàm getProjectsInMyWorkspace")
    class GetProjectsInMyWorkspaceTest {
        int page = 1;
        int size = 10;
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());

        @Test
        @DisplayName("Success: lấy danh sách project thành công, trả về PageResponse")
        void getProjectsInMyWorkspace_ShouldReturnPageResponse() {
            ProjectResponse mockProjectResponse = ProjectResponse.builder()
                    .id(PROJECT_ID)
                    .name("Test Project")
                    .build();

            Page<Project> pageResult = new PageImpl<>(List.of(project), pageable, 1);

            try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
                mockSecurityContext(mocked, USER_ID);

                when(workspaceRepository.existsByUserId(USER_ID)).thenReturn(true);
                when(projectRepository.findProjectsByUserId(eq(USER_ID), any(Pageable.class)))
                        .thenReturn(pageResult);
                when(projectMapper.toProjectResponse(project)).thenReturn(mockProjectResponse);

                PageResponse<ProjectResponse> result = workspaceService.getProjectsInMyWorkspace(page, size);

                assertThat(result.getCurrentPage()).isEqualTo(page);
                assertThat(result.getTotalElements()).isEqualTo(1);
                assertThat(result.getData()).hasSize(1);
                assertThat(result.getData().getFirst().getId()).isEqualTo(PROJECT_ID);

                verify(projectRepository, times(1)).findProjectsByUserId(eq(USER_ID), any(Pageable.class));
            }
        }

        @Test
        @DisplayName("Success: workspace không có project nào, trả về data rỗng")
        void getProjectsInMyWorkspace_NoProjects_ShouldReturnEmptyPage() {
            Page<Project> emptyPage = new PageImpl<>(List.of(), pageable, 0);

            try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
                mockSecurityContext(mocked, USER_ID);

                when(workspaceRepository.existsByUserId(USER_ID)).thenReturn(true);
                when(projectRepository.findProjectsByUserId(eq(USER_ID), any(Pageable.class)))
                        .thenReturn(emptyPage);

                PageResponse<ProjectResponse> result = workspaceService.getProjectsInMyWorkspace(page, size);

                assertThat(result.getData()).isEmpty();
                assertThat(result.getTotalElements()).isEqualTo(0);
            }
        }
    }

    @Nested
    @DisplayName("Update My Workspace: test hàm updateMyWorkspace")
    class UpdateMyWorkspaceTest {

        @Test
        @DisplayName("Success: cập nhật workspace bản thân thành công, trả về WorkspaceResponse")
        void updateMyWorkspace_ValidRequest_ShouldReturnWorkspaceResponse() {
            WorkspaceUpdateRequest request =
                    WorkspaceUpdateRequest.builder().name("Updated Workspace").build();

            WorkspaceResponse mockResponse = WorkspaceResponse.builder()
                    .id(WORKSPACE_ID)
                    .name("Updated Workspace")
                    .build();

            try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
                mockSecurityContext(mocked, USER_ID);

                when(workspaceRepository.findByUserId(USER_ID)).thenReturn(Optional.of(workspace));
                when(workspaceRepository.save(any(Workspace.class))).thenReturn(workspace);
                when(workspaceMapper.toWorkspaceResponse(workspace)).thenReturn(mockResponse);

                WorkspaceResponse response = workspaceService.updateMyWorkspace(request);

                assertThat(response).isNotNull();
                assertThat(response.getName()).isEqualTo("Updated Workspace");

                verify(workspaceMapper, times(1)).updateWorkspace(eq(workspace), eq(request));
                verify(workspaceRepository, times(1)).save(any(Workspace.class));
            }
        }
    }

    @Nested
    @DisplayName("Delete Project In My Workspace: test hàm deleteProjectInMyWorkspace")
    class DeleteProjectInMyWorkspaceTest {

        @Test
        @DisplayName("Success: project thuộc nhiều workspace, chỉ remove khỏi workspace hiện tại")
        void deleteProjectInMyWorkspace_ProjectInMultipleWorkspaces_ShouldRemoveFromWorkspace() {
            Workspace otherWorkspace = Workspace.builder()
                    .id("other-workspace-uuid")
                    .userId("other-user-uuid")
                    .projects(new HashSet<>())
                    .build();
            project.getWorkspaces().add(otherWorkspace);
            otherWorkspace.getProjects().add(project);

            try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
                mockSecurityContext(mocked, USER_ID);

                when(workspaceRepository.findByUserId(USER_ID)).thenReturn(Optional.of(workspace));
                when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
                when(projectRepository.save(any(Project.class))).thenReturn(project);

                assertThatCode(() -> workspaceService.deleteProjectInMyWorkspace(PROJECT_ID))
                        .doesNotThrowAnyException();

                verify(projectRepository, never()).delete(any());
                verify(projectRepository, times(1)).save(any(Project.class));
            }
        }

        @Test
        @DisplayName("Success: project chỉ thuộc 1 workspace, xóa hẳn project")
        void deleteProjectInMyWorkspace_ProjectInSingleWorkspace_ShouldDeleteProject() {
            try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
                mockSecurityContext(mocked, USER_ID);

                when(workspaceRepository.findByUserId(USER_ID)).thenReturn(Optional.of(workspace));
                when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));

                assertThatCode(() -> workspaceService.deleteProjectInMyWorkspace(PROJECT_ID))
                        .doesNotThrowAnyException();

                verify(projectRepository, times(1)).delete(project);
                verify(projectRepository, never()).save(any());
            }
        }

        @Test
        @DisplayName("Fail: project không thuộc workspace này, trả về lỗi PROJECT_NOT_IN_WORKSPACE")
        void deleteProjectInMyWorkspace_ProjectNotInWorkspace_ShouldThrow() {
            project.getWorkspaces().remove(workspace);
            workspace.getProjects().remove(project);

            try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
                mockSecurityContext(mocked, USER_ID);

                when(workspaceRepository.findByUserId(USER_ID)).thenReturn(Optional.of(workspace));
                when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));

                assertThatThrownBy(() -> workspaceService.deleteProjectInMyWorkspace(PROJECT_ID))
                        .isInstanceOf(AppException.class)
                        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PROJECT_NOT_IN_WORKSPACE);

                verify(projectRepository, never()).delete(any());
                verify(projectRepository, never()).save(any());
            }
        }

        @Test
        @DisplayName("Fail: user không phải createdBy của project, trả về lỗi ACCESS_DENIED")
        void deleteProjectInMyWorkspace_NotProjectOwner_ShouldThrow() {
            project.setCreatedBy("other-user-uuid");

            try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
                mockSecurityContext(mocked, USER_ID);

                when(workspaceRepository.findByUserId(USER_ID)).thenReturn(Optional.of(workspace));
                when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));

                assertThatThrownBy(() -> workspaceService.deleteProjectInMyWorkspace(PROJECT_ID))
                        .isInstanceOf(AppException.class)
                        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED);
            }
        }

        @Test
        @DisplayName("Fail: project không tồn tại, trả về lỗi PROJECT_NOT_FOUND")
        void deleteProjectInMyWorkspace_ProjectNotFound_ShouldThrow() {
            try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
                mockSecurityContext(mocked, USER_ID);

                when(workspaceRepository.findByUserId(USER_ID)).thenReturn(Optional.of(workspace));
                when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.empty());

                assertThatThrownBy(() -> workspaceService.deleteProjectInMyWorkspace(PROJECT_ID))
                        .isInstanceOf(AppException.class)
                        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PROJECT_NOT_FOUND);
            }
        }
    }

    @Nested
    @DisplayName("Get All Workspace: test hàm getAllWorkspace")
    class GetAllWorkspaceTest {
        int page = 1;
        int size = 10;
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());

        @Test
        @DisplayName("Success: lấy tất cả workspace thành công, trả về PageResponse")
        void getAllWorkspace_ShouldReturnPageResponse() {
            Workspace workspace2 =
                    Workspace.builder().id("ws-uuid-2").userId("user-2").build();

            Page<Workspace> pageResult = new PageImpl<>(List.of(workspace, workspace2), pageable, 2);

            WorkspaceResponse res1 =
                    WorkspaceResponse.builder().id(WORKSPACE_ID).build();
            WorkspaceResponse res2 = WorkspaceResponse.builder().id("ws-uuid-2").build();

            when(workspaceRepository.findAll(any(Pageable.class))).thenReturn(pageResult);
            when(workspaceMapper.toWorkspaceResponse(workspace)).thenReturn(res1);
            when(workspaceMapper.toWorkspaceResponse(workspace2)).thenReturn(res2);

            PageResponse<WorkspaceResponse> result = workspaceService.getAllWorkspace(page, size);

            assertThat(result.getTotalElements()).isEqualTo(2);
            assertThat(result.getData()).hasSize(2);
            verify(workspaceRepository, times(1)).findAll(any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("Get Workspace: test hàm getWorkspace")
    class GetWorkspaceTest {

        @Test
        @DisplayName("Success: lấy chi tiết workspace thành công, trả về WorkspaceResponse")
        void getWorkspace_ValidId_ShouldReturnWorkspaceResponse() {
            WorkspaceResponse mockResponse = WorkspaceResponse.builder()
                    .id(WORKSPACE_ID)
                    .name("My Workspace")
                    .build();

            when(workspaceRepository.findById(WORKSPACE_ID)).thenReturn(Optional.of(workspace));
            when(workspaceMapper.toWorkspaceResponse(workspace)).thenReturn(mockResponse);

            WorkspaceResponse response = workspaceService.getWorkspace(WORKSPACE_ID);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(WORKSPACE_ID);

            verify(workspaceRepository, times(1)).findById(WORKSPACE_ID);
        }
    }

    @Nested
    @DisplayName("Get Projects In Workspace: test hàm getProjectsInWorkspace")
    class GetProjectsInWorkspaceTest {
        int page = 1;
        int size = 10;
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());

        @Test
        @DisplayName("Success: lấy danh sách project trong workspace thành công, trả về PageResponse")
        void getProjectsInWorkspace_ShouldReturnPageResponse() {
            ProjectResponse mockProjectResponse = ProjectResponse.builder()
                    .id(PROJECT_ID)
                    .name("Test Project")
                    .build();
            Page<Project> pageResult = new PageImpl<>(List.of(project), pageable, 1);

            when(workspaceRepository.existsById(WORKSPACE_ID)).thenReturn(true);
            when(workspaceRepository.findProjectsById(eq(WORKSPACE_ID), any(Pageable.class)))
                    .thenReturn(pageResult);
            when(projectMapper.toProjectResponse(project)).thenReturn(mockProjectResponse);

            PageResponse<ProjectResponse> result = workspaceService.getProjectsInWorkspace(WORKSPACE_ID, page, size);

            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getData()).hasSize(1);
            assertThat(result.getData().getFirst().getId()).isEqualTo(PROJECT_ID);
        }
    }

    @Nested
    @DisplayName("Update Workspace: test hàm updateWorkspace")
    class UpdateWorkspaceTest {

        @Test
        @DisplayName("Success: cập nhật workspace thành công, trả về WorkspaceResponse")
        void updateWorkspace_ValidRequest_ShouldReturnWorkspaceResponse() {
            WorkspaceUpdateRequest request =
                    WorkspaceUpdateRequest.builder().name("Updated Workspace").build();

            WorkspaceResponse mockResponse = WorkspaceResponse.builder()
                    .id(WORKSPACE_ID)
                    .name("Updated Workspace")
                    .build();

            when(workspaceRepository.findById(WORKSPACE_ID)).thenReturn(Optional.of(workspace));
            when(workspaceRepository.save(any(Workspace.class))).thenReturn(workspace);
            when(workspaceMapper.toWorkspaceResponse(workspace)).thenReturn(mockResponse);

            WorkspaceResponse response = workspaceService.updateWorkspace(WORKSPACE_ID, request);

            assertThat(response).isNotNull();
            assertThat(response.getName()).isEqualTo("Updated Workspace");

            verify(workspaceMapper, times(1)).updateWorkspace(eq(workspace), eq(request));
            verify(workspaceRepository, times(1)).save(any(Workspace.class));
        }
    }

    @Nested
    @DisplayName("Delete Project In Workspace: test hàm deleteProjectInWorkspace")
    class DeleteProjectInWorkspaceTest {

        @Test
        @DisplayName("Success: project thuộc nhiều workspace, chỉ remove khỏi workspace")
        void deleteProjectInWorkspace_ProjectInMultipleWorkspaces_ShouldRemoveFromWorkspace() {
            Workspace otherWorkspace = Workspace.builder()
                    .id("other-workspace-uuid")
                    .userId("other-user-uuid")
                    .projects(new HashSet<>())
                    .build();
            project.getWorkspaces().add(otherWorkspace);
            otherWorkspace.getProjects().add(project);

            when(workspaceRepository.findById(WORKSPACE_ID)).thenReturn(Optional.of(workspace));
            when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
            when(projectRepository.save(any(Project.class))).thenReturn(project);

            assertThatCode(() -> workspaceService.deleteProjectInWorkspace(WORKSPACE_ID, PROJECT_ID))
                    .doesNotThrowAnyException();

            verify(projectRepository, never()).delete(any());
            verify(projectRepository, times(1)).save(any(Project.class));
        }

        @Test
        @DisplayName("Success: project chỉ thuộc 1 workspace, xóa hẳn project")
        void deleteProjectInWorkspace_ProjectInSingleWorkspace_ShouldDeleteProject() {
            when(workspaceRepository.findById(WORKSPACE_ID)).thenReturn(Optional.of(workspace));
            when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));

            assertThatCode(() -> workspaceService.deleteProjectInWorkspace(WORKSPACE_ID, PROJECT_ID))
                    .doesNotThrowAnyException();

            verify(projectRepository, times(1)).delete(project);
            verify(projectRepository, never()).save(any());
        }

        @Test
        @DisplayName("Fail: project không thuộc workspace này, trả về lỗi PROJECT_NOT_IN_WORKSPACE")
        void deleteProjectInWorkspace_ProjectNotInWorkspace_ShouldThrow() {
            project.getWorkspaces().remove(workspace);
            workspace.getProjects().remove(project);

            when(workspaceRepository.findById(WORKSPACE_ID)).thenReturn(Optional.of(workspace));
            when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));

            assertThatThrownBy(() -> workspaceService.deleteProjectInWorkspace(WORKSPACE_ID, PROJECT_ID))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PROJECT_NOT_IN_WORKSPACE);

            verify(projectRepository, never()).delete(any());
            verify(projectRepository, never()).save(any());
        }

        @Test
        @DisplayName("Fail: project không tồn tại, trả về lỗi PROJECT_NOT_FOUND")
        void deleteProjectInWorkspace_ProjectNotFound_ShouldThrow() {
            when(workspaceRepository.findById(WORKSPACE_ID)).thenReturn(Optional.of(workspace));
            when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> workspaceService.deleteProjectInWorkspace(WORKSPACE_ID, PROJECT_ID))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PROJECT_NOT_FOUND);
        }
    }
}
