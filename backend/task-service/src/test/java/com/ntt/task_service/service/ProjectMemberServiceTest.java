package com.ntt.task_service.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.ntt.task_service.dto.response.*;
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
import com.ntt.task_service.domain.ProjectRole;
import com.ntt.task_service.dto.request.ProjectMemberAddRequest;
import com.ntt.task_service.dto.request.RoleMemberUpdateRequest;
import com.ntt.task_service.exception.AppException;
import com.ntt.task_service.exception.ErrorCode;
import com.ntt.task_service.mapper.ProjectMemberMapper;
import com.ntt.task_service.repository.ProjectMemberRepository;
import com.ntt.task_service.repository.ProjectRepository;
import com.ntt.task_service.repository.httpclient.AuthenticationClient;
import com.ntt.task_service.repository.httpclient.ProfileClient;
import org.springframework.data.domain.*;

@ExtendWith(MockitoExtension.class)
class ProjectMemberServiceTest {

    @Mock
    ProjectMemberRepository projectMemberRepository;

    @Mock
    ProjectMemberMapper projectMemberMapper;

    @Mock
    AuthenticationClient authenticationClient;

    @Mock
    ProfileClient profileClient;

    @Mock
    ProjectRepository projectRepository;

    @Mock
    ProjectAuthorizationService projectAuthorizationService;

    @InjectMocks
    ProjectMemberService projectMemberService;

    private static final String USER_ID = "user-uuid-1234";
    private static final String OTHER_USER_ID = "other-user-uuid-5678";
    private static final String PROJECT_ID = "project-uuid-1234";
    private static final String MEMBER_ID = "member-uuid-1234";

    private Project project;
    private ProjectMember projectMember;

    @BeforeEach
    void setUpGlobal() {
        project = Project.builder()
                .id(PROJECT_ID)
                .name("Test Project")
                .createdBy(USER_ID)
                .workspaces(new HashSet<>())
                .build();

        projectMember = ProjectMember.builder()
                .id(MEMBER_ID)
                .project(project)
                .userId(OTHER_USER_ID)
                .role(ProjectRole.MEMBER)
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
        @DisplayName("Get Members In Project: trả về lỗi PROJECT_NOT_FOUND")
        void getMembersInProjectTest() {
            assertThatThrownBy(() -> projectMemberService.getMembersInProject(PROJECT_ID, 1, 10))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PROJECT_NOT_FOUND);
        }

        @Test
        @DisplayName("Search Users To Invite: trả về lỗi PROJECT_NOT_FOUND")
        void searchUsersToInviteTest() {
            assertThatThrownBy(() -> projectMemberService.searchUsersToInvite(PROJECT_ID, "test@email.com"))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PROJECT_NOT_FOUND);
        }

        @Test
        @DisplayName("Add Member To Project: trả về lỗi PROJECT_NOT_FOUND")
        void addMemberToProjectTest() {
            assertThatThrownBy(() -> projectMemberService.addMemberToProject(
                            PROJECT_ID,
                            ProjectMemberAddRequest.builder()
                                    .userId(OTHER_USER_ID)
                                    .role(ProjectRole.MEMBER)
                                    .build()))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PROJECT_NOT_FOUND);
        }

        @Test
        @DisplayName("Update Role Member In Project: trả về lỗi PROJECT_NOT_FOUND")
        void updateRoleMemberInProjectTest() {
            assertThatThrownBy(() -> projectMemberService.updateRoleMemberInProject(
                            PROJECT_ID,
                            OTHER_USER_ID,
                            RoleMemberUpdateRequest.builder()
                                    .role(ProjectRole.MANAGER)
                                    .build()))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PROJECT_NOT_FOUND);
        }

        @Test
        @DisplayName("Remove Member From Project: trả về lỗi PROJECT_NOT_FOUND")
        void removeMemberFromProjectTest() {
            assertThatThrownBy(() -> projectMemberService.removeMemberFromProject(PROJECT_ID, OTHER_USER_ID))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PROJECT_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("Get Members In Project: test hàm getMembersInProject phân trang")
    class GetMembersInProjectTest {

        int page = 1;
        int size = 10;
        Pageable pageable = PageRequest.of(0, size, Sort.by("createdAt").descending());

        @Test
        @DisplayName("Success: lấy danh sách member thành công, trả về PageResponse<ProjectMemberResponse>")
        void getMembersInProject_ValidId_ShouldReturnPageResponse() {
            ProjectMemberResponse mockResponse = ProjectMemberResponse.builder()
                    .userId(OTHER_USER_ID)
                    .role(ProjectRole.MEMBER)
                    .build();

            Page<ProjectMember> pageResult = new PageImpl<>(List.of(projectMember), pageable, 1);

            when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
            doNothing().when(projectAuthorizationService).validateCanView(PROJECT_ID);

            when(projectMemberRepository.findByProjectId(eq(PROJECT_ID), any(Pageable.class))).thenReturn(pageResult);
            when(projectMemberMapper.toProjectMemberResponse(projectMember)).thenReturn(mockResponse);

            PageResponse<ProjectMemberResponse> result = projectMemberService.getMembersInProject(PROJECT_ID, page, size);

            assertThat(result.getCurrentPage()).isEqualTo(page);
            assertThat(result.getPageSize()).isEqualTo(size);
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getData()).hasSize(1);
            assertThat(result.getData().getFirst().getUserId()).isEqualTo(OTHER_USER_ID);

            verify(projectMemberRepository, times(1)).findByProjectId(eq(PROJECT_ID), any(Pageable.class));
            verify(projectAuthorizationService, times(1)).validateCanView(PROJECT_ID);
        }

        @Test
        @DisplayName("Fail: user không có quyền xem, trả về lỗi ACCESS_DENIED")
        void getMembersInProject_AccessDenied_ShouldThrow() {
            when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
            doThrow(new AppException(ErrorCode.ACCESS_DENIED))
                    .when(projectAuthorizationService)
                    .validateCanView(PROJECT_ID);

            assertThatThrownBy(() -> projectMemberService.getMembersInProject(PROJECT_ID, page, size))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED);

            verify(projectMemberRepository, never()).findByProjectId(anyString(), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("Search Users To Invite: test hàm searchUsersToInvite")
    class SearchUsersToInviteTest {

        private UserSearchResponse userSearchResponse;
        private ProfileSearchResponse profileSearchResponse;

        @BeforeEach
        void setup() {
            userSearchResponse = UserSearchResponse.builder()
                    .id(OTHER_USER_ID)
                    .email("other@email.com")
                    .build();

            profileSearchResponse = ProfileSearchResponse.builder()
                    .userId(OTHER_USER_ID)
                    .firstName("John")
                    .lastName("Doe")
                    .avatar("avatar-url")
                    .build();
        }

        @Test
        @DisplayName("Success: tìm thấy user chưa là member, trả về danh sách với alreadyMember=false")
        void searchUsersToInvite_UserNotYetMember_ShouldReturnWithAlreadyMemberFalse() {
            var authResult = mock(com.ntt.task_service.dto.response.ApiResponse.class);
            var profileResult = mock(com.ntt.task_service.dto.response.ApiResponse.class);

            when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
            doNothing().when(projectAuthorizationService).validateCanManage(PROJECT_ID);
            when(authenticationClient.searchByEmail("other@email.com")).thenReturn(authResult);
            when(authResult.getResult()).thenReturn(List.of(userSearchResponse));
            when(profileClient.searchByUserIds(List.of(OTHER_USER_ID))).thenReturn(profileResult);
            when(profileResult.getResult()).thenReturn(List.of(profileSearchResponse));
            when(projectMemberRepository.findUserIdsByProjectId(PROJECT_ID)).thenReturn(Set.of());

            List<MemberSearchResponse> result = projectMemberService.searchUsersToInvite(PROJECT_ID, "other@email.com");

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getUserId()).isEqualTo(OTHER_USER_ID);
            assertThat(result.getFirst().getEmail()).isEqualTo("other@email.com");
            assertThat(result.getFirst().isAlreadyMember()).isFalse();
        }

        @Test
        @DisplayName("Success: tìm thấy user đã là member, trả về danh sách với alreadyMember=true")
        void searchUsersToInvite_UserAlreadyMember_ShouldReturnWithAlreadyMemberTrue() {
            var authResult = mock(com.ntt.task_service.dto.response.ApiResponse.class);
            var profileResult = mock(com.ntt.task_service.dto.response.ApiResponse.class);

            when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
            doNothing().when(projectAuthorizationService).validateCanManage(PROJECT_ID);
            when(authenticationClient.searchByEmail("other@email.com")).thenReturn(authResult);
            when(authResult.getResult()).thenReturn(List.of(userSearchResponse));
            when(profileClient.searchByUserIds(List.of(OTHER_USER_ID))).thenReturn(profileResult);
            when(profileResult.getResult()).thenReturn(List.of(profileSearchResponse));
            when(projectMemberRepository.findUserIdsByProjectId(PROJECT_ID)).thenReturn(Set.of(OTHER_USER_ID));

            List<MemberSearchResponse> result = projectMemberService.searchUsersToInvite(PROJECT_ID, "other@email.com");

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().isAlreadyMember()).isTrue();
        }

        @Test
        @DisplayName("Success: không tìm thấy user nào theo email, trả về danh sách rỗng")
        void searchUsersToInvite_NoUsersFound_ShouldReturnEmptyList() {
            var authResult = mock(com.ntt.task_service.dto.response.ApiResponse.class);

            when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
            doNothing().when(projectAuthorizationService).validateCanManage(PROJECT_ID);
            when(authenticationClient.searchByEmail("notfound@email.com")).thenReturn(authResult);
            when(authResult.getResult()).thenReturn(List.of());

            List<MemberSearchResponse> result =
                    projectMemberService.searchUsersToInvite(PROJECT_ID, "notfound@email.com");

            assertThat(result).isEmpty();

            verify(profileClient, never()).searchByUserIds(any());
            verify(projectMemberRepository, never()).findUserIdsByProjectId(any());
        }

        @Test
        @DisplayName("Success: user không có profile, trả về MemberSearchResponse với profile rỗng")
        void searchUsersToInvite_UserWithoutProfile_ShouldReturnWithEmptyProfile() {
            var authResult = mock(com.ntt.task_service.dto.response.ApiResponse.class);
            var profileResult = mock(com.ntt.task_service.dto.response.ApiResponse.class);

            when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
            doNothing().when(projectAuthorizationService).validateCanManage(PROJECT_ID);
            when(authenticationClient.searchByEmail("other@email.com")).thenReturn(authResult);
            when(authResult.getResult()).thenReturn(List.of(userSearchResponse));
            when(profileClient.searchByUserIds(List.of(OTHER_USER_ID))).thenReturn(profileResult);
            when(profileResult.getResult()).thenReturn(List.of());
            when(projectMemberRepository.findUserIdsByProjectId(PROJECT_ID)).thenReturn(Set.of());

            List<MemberSearchResponse> result = projectMemberService.searchUsersToInvite(PROJECT_ID, "other@email.com");

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getUserId()).isEqualTo(OTHER_USER_ID);
            assertThat(result.getFirst().getFirstName()).isNull();
        }

        @Test
        @DisplayName("Fail: user không có quyền manage, trả về lỗi ACCESS_DENIED")
        void searchUsersToInvite_AccessDenied_ShouldThrow() {
            when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
            doThrow(new AppException(ErrorCode.ACCESS_DENIED))
                    .when(projectAuthorizationService)
                    .validateCanManage(PROJECT_ID);

            assertThatThrownBy(() -> projectMemberService.searchUsersToInvite(PROJECT_ID, "other@email.com"))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED);

            verify(authenticationClient, never()).searchByEmail(any());
        }
    }

    @Nested
    @DisplayName("Add Member To Project: test hàm addMemberToProject")
    class AddMemberToProjectTest {

        private ProjectMemberAddRequest request;

        @BeforeEach
        void setup() {
            request = ProjectMemberAddRequest.builder()
                    .userId(OTHER_USER_ID)
                    .role(ProjectRole.MEMBER)
                    .build();
        }

        @Test
        @DisplayName("Success: thêm member thành công, trả về ProjectMemberResponse")
        void addMemberToProject_ValidRequest_ShouldReturnProjectMemberResponse() {
            ProjectMemberResponse mockResponse = ProjectMemberResponse.builder()
                    .userId(OTHER_USER_ID)
                    .role(ProjectRole.MEMBER)
                    .build();

            when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
            doNothing().when(projectAuthorizationService).validateCanManage(PROJECT_ID);
            when(projectMemberRepository.existsByProjectIdAndUserId(PROJECT_ID, OTHER_USER_ID))
                    .thenReturn(false);
            when(projectMemberRepository.save(any(ProjectMember.class))).thenReturn(projectMember);
            when(projectMemberMapper.toProjectMemberResponse(projectMember)).thenReturn(mockResponse);

            ProjectMemberResponse result = projectMemberService.addMemberToProject(PROJECT_ID, request);

            assertThat(result).isNotNull();
            assertThat(result.getUserId()).isEqualTo(OTHER_USER_ID);

            verify(projectMemberRepository, times(1)).save(any(ProjectMember.class));
        }

        @Test
        @DisplayName("Fail: user đã là member, trả về lỗi USER_ALREADY_IN_PROJECT")
        void addMemberToProject_UserAlreadyInProject_ShouldThrow() {
            when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
            doNothing().when(projectAuthorizationService).validateCanManage(PROJECT_ID);
            when(projectMemberRepository.existsByProjectIdAndUserId(PROJECT_ID, OTHER_USER_ID))
                    .thenReturn(true);

            assertThatThrownBy(() -> projectMemberService.addMemberToProject(PROJECT_ID, request))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_ALREADY_IN_PROJECT);

            verify(projectMemberRepository, never()).save(any());
        }

        @Test
        @DisplayName("Fail: user không có quyền manage, trả về lỗi ACCESS_DENIED")
        void addMemberToProject_AccessDenied_ShouldThrow() {
            when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
            doThrow(new AppException(ErrorCode.ACCESS_DENIED))
                    .when(projectAuthorizationService)
                    .validateCanManage(PROJECT_ID);

            assertThatThrownBy(() -> projectMemberService.addMemberToProject(PROJECT_ID, request))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED);

            verify(projectMemberRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Update Role Member In Project: test hàm updateRoleMemberInProject")
    class UpdateRoleMemberInProjectTest {

        private RoleMemberUpdateRequest request;

        @BeforeEach
        void setup() {
            request =
                    RoleMemberUpdateRequest.builder().role(ProjectRole.MANAGER).build();
        }

        @Test
        @DisplayName("Success: cập nhật role thành công, trả về ProjectMemberResponse")
        void updateRoleMemberInProject_ValidRequest_ShouldReturnProjectMemberResponse() {
            ProjectMemberResponse mockResponse = ProjectMemberResponse.builder()
                    .userId(OTHER_USER_ID)
                    .role(ProjectRole.MANAGER)
                    .build();

            when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
            doNothing().when(projectAuthorizationService).validateCanManage(PROJECT_ID);
            when(projectMemberRepository.findByProjectIdAndUserId(PROJECT_ID, OTHER_USER_ID))
                    .thenReturn(Optional.of(projectMember));
            when(projectMemberRepository.save(any(ProjectMember.class))).thenReturn(projectMember);
            when(projectMemberMapper.toProjectMemberResponse(projectMember)).thenReturn(mockResponse);

            ProjectMemberResponse result =
                    projectMemberService.updateRoleMemberInProject(PROJECT_ID, OTHER_USER_ID, request);

            assertThat(result).isNotNull();
            assertThat(result.getRole()).isEqualTo(ProjectRole.MANAGER);

            verify(projectMemberRepository, times(1)).save(any(ProjectMember.class));
        }

        @Test
        @DisplayName("Fail: user không có trong project, trả về lỗi USER_NOT_IN_PROJECT")
        void updateRoleMemberInProject_UserNotInProject_ShouldThrow() {
            when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
            doNothing().when(projectAuthorizationService).validateCanManage(PROJECT_ID);
            when(projectMemberRepository.findByProjectIdAndUserId(PROJECT_ID, OTHER_USER_ID))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> projectMemberService.updateRoleMemberInProject(PROJECT_ID, OTHER_USER_ID, request))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_IN_PROJECT);

            verify(projectMemberRepository, never()).save(any());
        }

        @Test
        @DisplayName("Fail: user không có quyền manage, trả về lỗi ACCESS_DENIED")
        void updateRoleMemberInProject_AccessDenied_ShouldThrow() {
            when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
            doThrow(new AppException(ErrorCode.ACCESS_DENIED))
                    .when(projectAuthorizationService)
                    .validateCanManage(PROJECT_ID);

            assertThatThrownBy(() -> projectMemberService.updateRoleMemberInProject(PROJECT_ID, OTHER_USER_ID, request))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED);

            verify(projectMemberRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Remove Member From Project: test hàm removeMemberFromProject")
    class RemoveMemberFromProjectTest {

        @Test
        @DisplayName("Success: xóa member thành công")
        void removeMemberFromProject_ValidRequest_ShouldDeleteMember() {
            when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
            doNothing().when(projectAuthorizationService).validateCanManage(PROJECT_ID);
            when(projectMemberRepository.findByProjectIdAndUserId(PROJECT_ID, OTHER_USER_ID))
                    .thenReturn(Optional.of(projectMember));
            when(projectAuthorizationService.getCurrentUserId()).thenReturn(USER_ID);

            assertThatCode(() -> projectMemberService.removeMemberFromProject(PROJECT_ID, OTHER_USER_ID))
                    .doesNotThrowAnyException();

            verify(projectMemberRepository, times(1)).delete(projectMember);
        }

        @Test
        @DisplayName("Fail: xóa chính mình, trả về lỗi CANNOT_REMOVE_YOURSELF")
        void removeMemberFromProject_RemoveYourself_ShouldThrow() {
            projectMember.setUserId(USER_ID);

            when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
            doNothing().when(projectAuthorizationService).validateCanManage(PROJECT_ID);
            when(projectMemberRepository.findByProjectIdAndUserId(PROJECT_ID, USER_ID))
                    .thenReturn(Optional.of(projectMember));
            when(projectAuthorizationService.getCurrentUserId()).thenReturn(USER_ID);

            assertThatThrownBy(() -> projectMemberService.removeMemberFromProject(PROJECT_ID, USER_ID))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CANNOT_REMOVE_YOURSELF);

            verify(projectMemberRepository, never()).delete(any());
        }

        @Test
        @DisplayName("Fail: xóa project owner, trả về lỗi CANNOT_REMOVE_PROJECT_OWNER")
        void removeMemberFromProject_RemoveProjectOwner_ShouldThrow() {
            project.setCreatedBy(OTHER_USER_ID);

            when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
            doNothing().when(projectAuthorizationService).validateCanManage(PROJECT_ID);
            when(projectMemberRepository.findByProjectIdAndUserId(PROJECT_ID, OTHER_USER_ID))
                    .thenReturn(Optional.of(projectMember));
            when(projectAuthorizationService.getCurrentUserId()).thenReturn(USER_ID);

            assertThatThrownBy(() -> projectMemberService.removeMemberFromProject(PROJECT_ID, OTHER_USER_ID))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CANNOT_REMOVE_PROJECT_OWNER);

            verify(projectMemberRepository, never()).delete(any());
        }

        @Test
        @DisplayName("Fail: user không có trong project, trả về lỗi USER_NOT_IN_PROJECT")
        void removeMemberFromProject_UserNotInProject_ShouldThrow() {
            when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
            doNothing().when(projectAuthorizationService).validateCanManage(PROJECT_ID);
            when(projectMemberRepository.findByProjectIdAndUserId(PROJECT_ID, OTHER_USER_ID))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> projectMemberService.removeMemberFromProject(PROJECT_ID, OTHER_USER_ID))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_IN_PROJECT);

            verify(projectMemberRepository, never()).delete(any());
        }

        @Test
        @DisplayName("Fail: user không có quyền manage, trả về lỗi ACCESS_DENIED")
        void removeMemberFromProject_AccessDenied_ShouldThrow() {
            when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
            doThrow(new AppException(ErrorCode.ACCESS_DENIED))
                    .when(projectAuthorizationService)
                    .validateCanManage(PROJECT_ID);

            assertThatThrownBy(() -> projectMemberService.removeMemberFromProject(PROJECT_ID, OTHER_USER_ID))
                    .isInstanceOf(AppException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED);

            verify(projectMemberRepository, never()).delete(any());
        }
    }
}
