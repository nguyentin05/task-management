package com.ntt.task_service.service;

import com.ntt.task_service.domain.Workspace;
import com.ntt.task_service.dto.request.WorkspaceCreationRequest;
import com.ntt.task_service.dto.response.WorkspaceResponse;
import com.ntt.task_service.exception.AppException;
import com.ntt.task_service.exception.ErrorCode;
import com.ntt.task_service.mapper.WorkspaceMapper;
import com.ntt.task_service.repository.WorkspaceRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WorkspaceService {
    WorkspaceRepository workspaceRepository;
    WorkspaceMapper workspaceMapper;

    public WorkspaceResponse getMyWorkspace() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        String userId = authentication.getName();

        var workspace = workspaceRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.WORKSPACE_NOT_FOUND));

        return workspaceMapper.toWorkspaceResponse(workspace);
    }

    public WorkspaceResponse createWorkspace(WorkspaceCreationRequest request) {
        Workspace workspace = workspaceMapper.toWorkspace(request);
        workspace = workspaceRepository.save(workspace);

        return workspaceMapper.toWorkspaceResponse(workspace);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public WorkspaceResponse getWorkspace(String workspaceId) {
        Workspace workspace =
                workspaceRepository.findById(workspaceId).orElseThrow(
                        () -> new AppException(ErrorCode.WORKSPACE_NOT_FOUND));

        return workspaceMapper.toWorkspaceResponse(workspace);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<WorkspaceResponse> getAllWorkspace() {
        var workspaces = workspaceRepository.findAll();

        return workspaces.stream().map(workspaceMapper::toWorkspaceResponse).toList();
    }
}
