package com.ntt.task_service.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.ntt.task_service.dto.response.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ntt.task_service.domain.Project;
import com.ntt.task_service.domain.ProjectMember;
import com.ntt.task_service.dto.request.ProjectMemberAddRequest;
import com.ntt.task_service.dto.request.RoleMemberUpdateRequest;
import com.ntt.task_service.exception.AppException;
import com.ntt.task_service.exception.ErrorCode;
import com.ntt.task_service.mapper.ProjectMemberMapper;
import com.ntt.task_service.repository.ProjectMemberRepository;
import com.ntt.task_service.repository.ProjectRepository;
import com.ntt.task_service.repository.httpclient.AuthenticationClient;
import com.ntt.task_service.repository.httpclient.ProfileClient;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProjectMemberService {
    ProjectMemberRepository projectMemberRepository;
    ProjectMemberMapper projectMemberMapper;
    AuthenticationClient authenticationClient;
    ProfileClient profileClient;
    ProjectRepository projectRepository;
    ProjectAuthorizationService projectAuthorizationService;

    public PageResponse<ProjectMemberResponse> getMembersInProject(String id, int page, int size) {
        getProjectOrThrow(id);

        projectAuthorizationService.validateCanView(id);

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        var pageData = projectMemberRepository.findByProjectId(id, pageable);

        return PageResponse.<ProjectMemberResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(pageData.getContent().stream()
                        .map(projectMemberMapper::toProjectMemberResponse)
                        .toList())
                .build();
    }

    public List<MemberSearchResponse> searchUsersToInvite(String id, String email) {
        getProjectOrThrow(id);

        projectAuthorizationService.validateCanManage(id);

        List<UserSearchResponse> users =
                authenticationClient.searchByEmail(email).getResult();

        if (users.isEmpty()) return List.of();

        List<String> userIds = users.stream().map(UserSearchResponse::getId).toList();

        Map<String, ProfileSearchResponse> profileMap = profileClient.searchByUserIds(userIds).getResult().stream()
                .collect(Collectors.toMap(ProfileSearchResponse::getUserId, p -> p));

        Set<String> existingMemberIds = projectMemberRepository.findUserIdsByProjectId(id);

        return users.stream()
                .map(user -> {
                    ProfileSearchResponse profile = profileMap.getOrDefault(
                            user.getId(),
                            ProfileSearchResponse.builder().userId(user.getId()).build());

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

    public ProjectMemberResponse addMemberToProject(String id, ProjectMemberAddRequest request) {
        Project project = getProjectOrThrow(id);

        projectAuthorizationService.validateCanManage(id);

        if (projectMemberRepository.existsByProjectIdAndUserId(id, request.getUserId())) {
            throw new AppException(ErrorCode.USER_ALREADY_IN_PROJECT);
        }

        ProjectMember member = ProjectMember.builder()
                .project(project)
                .userId(request.getUserId())
                .role(request.getRole())
                .build();

        return projectMemberMapper.toProjectMemberResponse(projectMemberRepository.save(member));
    }

    public ProjectMemberResponse updateRoleMemberInProject(
            String projectId, String userId, RoleMemberUpdateRequest request) {
        getProjectOrThrow(projectId);

        projectAuthorizationService.validateCanManage(projectId);

        ProjectMember member = projectMemberRepository
                .findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_IN_PROJECT));

        member.setRole(request.getRole());

        return projectMemberMapper.toProjectMemberResponse(projectMemberRepository.save(member));
    }

    public void removeMemberFromProject(String id, String userId) {
        Project project = getProjectOrThrow(id);

        projectAuthorizationService.validateCanManage(id);

        ProjectMember member = projectMemberRepository
                .findByProjectIdAndUserId(id, userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_IN_PROJECT));

        if (userId.equals(projectAuthorizationService.getCurrentUserId()))
            throw new AppException(ErrorCode.CANNOT_REMOVE_YOURSELF);

        if (userId.equals(project.getCreatedBy())) throw new AppException(ErrorCode.CANNOT_REMOVE_PROJECT_OWNER);

        projectMemberRepository.delete(member);
    }

    private Project getProjectOrThrow(String projectId) {
        return projectRepository.findById(projectId).orElseThrow(() -> new AppException(ErrorCode.PROJECT_NOT_FOUND));
    }
}
