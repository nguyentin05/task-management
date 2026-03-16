package com.ntt.task_service.service;

import com.ntt.task_service.domain.ProjectRole;
import com.ntt.task_service.exception.AppException;
import com.ntt.task_service.exception.ErrorCode;
import com.ntt.task_service.repository.ProjectMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectAuthorizationService {
    private final ProjectMemberRepository projectMemberRepository;

    public String getCurrentUserId() {
        return SecurityContextHolder.getContext()
                .getAuthentication().getName();
    }

    public boolean isSystemAdmin() {
        return SecurityContextHolder.getContext()
                .getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    public void validateCanView(String projectId) {
        if (isSystemAdmin()) return;
        if (!projectMemberRepository.existsByProjectIdAndUserId(
                projectId, getCurrentUserId()))
            throw new AppException(ErrorCode.ACCESS_DENIED);
    }

    public void validateCanManage(String projectId) {
        if (isSystemAdmin()) return;
        if (!projectMemberRepository.existsByProjectIdAndUserIdAndRole(
                projectId, getCurrentUserId(), ProjectRole.MANAGER))
            throw new AppException(ErrorCode.ACCESS_DENIED);
    }
}
