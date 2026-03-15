package com.ntt.task_service.service;

import com.ntt.task_service.domain.Project;
import com.ntt.task_service.domain.ProjectMember;
import com.ntt.task_service.domain.ProjectRole;
import com.ntt.task_service.domain.Workspace;
import com.ntt.task_service.dto.request.ProjectCreationRequest;
import com.ntt.task_service.dto.request.ProjectMemberAddRequest;
import com.ntt.task_service.dto.request.ProjectUpdateRequest;
import com.ntt.task_service.dto.request.RoleMemberUpdateRequest;
import com.ntt.task_service.dto.response.*;
import com.ntt.task_service.exception.AppException;
import com.ntt.task_service.exception.ErrorCode;
import com.ntt.task_service.mapper.ProjectMapper;
import com.ntt.task_service.mapper.ProjectMemberMapper;
import com.ntt.task_service.repository.ProjectMemberRepository;
import com.ntt.task_service.repository.ProjectRepository;
import com.ntt.task_service.repository.WorkspaceRepository;
import com.ntt.task_service.repository.httpclient.AuthenticationClient;
import com.ntt.task_service.repository.httpclient.ProfileClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProjectService {
    ProjectRepository projectRepository;
    WorkspaceRepository workspaceRepository;
    ProjectMapper projectMapper;
    ProjectMemberMapper projectMemberMapper;
    ProjectMemberRepository projectMemberRepository;
    AuthenticationClient authenticationClient;
    ProfileClient profileClient;

    @Transactional
    public ProjectResponse createProject(ProjectCreationRequest request) {
        String userId = getCurrentUserId();

        Workspace workspace = workspaceRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.WORKSPACE_NOT_FOUND));

        Project project = projectMapper.toProject(request);
        project.setCreatedBy(userId);
        project.getWorkspaces().add(workspace);
        project = projectRepository.save(project);

        ProjectMember creator = ProjectMember.builder()
                .project(project)
                .userId(userId)
                .role(ProjectRole.MANAGER)
                .build();

        projectMemberRepository.save(creator);

        return projectMapper.toProjectResponse(project);
    }

    public List<ProjectResponse> getAllProject() {
        var projects = projectRepository.findAll();

        return projects.stream().map(projectMapper::toProjectResponse).toList();
    }

    public ProjectResponse getProject(String id) {
        validateCanViewProject(id);

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PROJECT_NOT_FOUND));

        return projectMapper.toProjectResponse(project);
    }

    @Transactional
    public ProjectResponse updateProject(String id, ProjectUpdateRequest request) {
        validateCanManageProject(id);

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PROJECT_NOT_FOUND));

        projectMapper.updateProject(project, request);
        project = projectRepository.save(project);

        return projectMapper.toProjectResponse(project);
    }

    public void deleteProject(String id) {
        if (!projectRepository.existsById(id))
            throw new AppException(ErrorCode.PROJECT_NOT_FOUND);

        validateCanManageProject(id);

        projectRepository.deleteById(id);
    }

    public List<ProjectMemberResponse> getMembersInProject(String id) {
        validateCanViewProject(id);

        var members = projectMemberRepository.findByProjectId(id);

        return members.stream().map(projectMemberMapper::toProjectMemberResponse).toList();
    }

    public List<MemberSearchResponse> searchUsersToInvite(String projectId, String email) {
        validateCanManageProject(projectId);

        List<UserSearchResponse> users = authenticationClient.searchByEmail(email).getResult();

        if (users.isEmpty()) return List.of();

        List<String> userIds = users.stream().map(UserSearchResponse::getId).toList();

        Map<String, ProfileSearchResponse> profileMap = profileClient.searchByUserIds(userIds)
                .getResult()
                .stream()
                .collect(Collectors.toMap(ProfileSearchResponse::getUserId, p -> p));

        Set<String> existingMemberIds = projectMemberRepository.findUserIdsByProjectId(projectId);

        return users.stream()
                .map(user -> {
                    ProfileSearchResponse profile =
                            profileMap.getOrDefault(user.getId(),
                                    ProfileSearchResponse.builder()
                                            .userId(user.getId())
                                            .build()
                            );

                    return MemberSearchResponse.builder()
                            .userId(user.getId())
                            .email(user.getEmail())
                            .firstName(profile.getFirstName())
                            .lastName(profile.getLastName())
                            .avatar(profile.getAvatar())
                            .alreadyMember(existingMemberIds.contains(user.getId()))
                            .build();
                })
                .toList();
    }

    public ProjectMemberResponse addMemberToProject(String projectId,
                                                    ProjectMemberAddRequest request) {
        validateCanManageProject(projectId);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new AppException(ErrorCode.PROJECT_NOT_FOUND));

        if (projectMemberRepository.existsByProjectIdAndUserId(
                projectId, request.getUserId())) {
            throw new AppException(ErrorCode.USER_ALREADY_IN_PROJECT);
        }

        ProjectMember member = ProjectMember.builder()
                .project(project)
                .userId(request.getUserId())
                .role(request.getRole())
                .build();

        return projectMemberMapper.toProjectMemberResponse(projectMemberRepository.save(member));
    }

    public ProjectMemberResponse updateRoleMemberInProject(String projectId, String userId, RoleMemberUpdateRequest request) {
        validateCanManageProject(projectId);

        ProjectMember member = projectMemberRepository.findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_IN_PROJECT));

        member.setRole(request.getRole());

        return projectMemberMapper.toProjectMemberResponse(projectMemberRepository.save(member));
    }

    public void removeMemberFromProject(String projectId, String userId) {
        if (!projectRepository.existsById(projectId))
            throw new AppException(ErrorCode.PROJECT_NOT_FOUND);

        validateCanManageProject(projectId);

        ProjectMember member = projectMemberRepository
                .findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_IN_PROJECT));

        if (userId.equals(getCurrentUserId()))
            throw new AppException(ErrorCode.CANNOT_REMOVE_YOURSELF);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new AppException(ErrorCode.PROJECT_NOT_FOUND));

        if (userId.equals(project.getCreatedBy()))
            throw new AppException(ErrorCode.CANNOT_REMOVE_PROJECT_OWNER);

        projectMemberRepository.delete(member);
    }

    private String getCurrentUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private boolean isSystemAdmin() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));
    }

    private void validateCanViewProject(String projectId) {
        if (isSystemAdmin()) return;

        if (!projectMemberRepository.existsByProjectIdAndUserId(projectId, getCurrentUserId())) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }
    }

    private void validateCanManageProject(String projectId) {
        if (isSystemAdmin()) return;

        if (!projectMemberRepository.existsByProjectIdAndUserIdAndRole(projectId, getCurrentUserId(), ProjectRole.MANAGER)) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }
    }
}
