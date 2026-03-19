package com.ntt.task_service.service;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ntt.task_service.domain.Project;
import com.ntt.task_service.domain.Workspace;
import com.ntt.task_service.dto.request.WorkspaceCreationRequest;
import com.ntt.task_service.dto.request.WorkspaceUpdateRequest;
import com.ntt.task_service.dto.response.ProjectResponse;
import com.ntt.task_service.dto.response.WorkspaceResponse;
import com.ntt.task_service.exception.AppException;
import com.ntt.task_service.exception.ErrorCode;
import com.ntt.task_service.mapper.ProjectMapper;
import com.ntt.task_service.mapper.WorkspaceMapper;
import com.ntt.task_service.repository.ProjectRepository;
import com.ntt.task_service.repository.WorkspaceRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WorkspaceService {
    WorkspaceRepository workspaceRepository;
    WorkspaceMapper workspaceMapper;
    ProjectRepository projectRepository;
    ProjectMapper projectMapper;

    public WorkspaceResponse create(WorkspaceCreationRequest request) {
        Workspace workspace = workspaceMapper.toWorkspace(request);
        workspace = workspaceRepository.save(workspace);

        return workspaceMapper.toWorkspaceResponse(workspace);
    }

    public WorkspaceResponse getMyWorkspace() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        var workspace = workspaceRepository
                .findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.WORKSPACE_NOT_FOUND));

        return workspaceMapper.toWorkspaceResponse(workspace);
    }

    public List<ProjectResponse> getProjectsInMyWorkspace() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        Workspace workspace = workspaceRepository
                .findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.WORKSPACE_NOT_FOUND));

        return workspace.getProjects().stream()
                .map(projectMapper::toProjectResponse)
                .toList();
    }

    @Transactional
    public WorkspaceResponse updateMyWorkspace(WorkspaceUpdateRequest request) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        var workspace = workspaceRepository
                .findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.WORKSPACE_NOT_FOUND));

        workspaceMapper.updateWorkspace(workspace, request);

        return workspaceMapper.toWorkspaceResponse(workspaceRepository.save(workspace));
    }

    @Transactional
    public void deleteProjectInMyWorkspace(String id) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        Workspace workspace = workspaceRepository
                .findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.WORKSPACE_NOT_FOUND));

        Project project =
                projectRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.PROJECT_NOT_FOUND));

        if (!project.getWorkspaces().contains(workspace)) {
            throw new AppException(ErrorCode.PROJECT_NOT_IN_WORKSPACE);
        }

        if (!project.getCreatedBy().equals(userId)) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        if (project.getWorkspaces().size() == 1) {
            projectRepository.delete(project);
            return;
        }

        project.getWorkspaces().remove(workspace);
        workspace.getProjects().remove(project);

        projectRepository.save(project);
    }

    public List<WorkspaceResponse> getAllWorkspace() {
        var workspaces = workspaceRepository.findAll();

        return workspaces.stream().map(workspaceMapper::toWorkspaceResponse).toList();
    }

    public WorkspaceResponse getWorkspace(String id) {
        Workspace workspace =
                workspaceRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.WORKSPACE_NOT_FOUND));

        return workspaceMapper.toWorkspaceResponse(workspace);
    }

    public List<ProjectResponse> getProjectsInWorkspace(String id) {
        Workspace workspace =
                workspaceRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.WORKSPACE_NOT_FOUND));

        return workspace.getProjects().stream()
                .map(projectMapper::toProjectResponse)
                .toList();
    }

    @Transactional
    public WorkspaceResponse updateWorkspace(String id, WorkspaceUpdateRequest request) {
        var workspace =
                workspaceRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.WORKSPACE_NOT_FOUND));

        workspaceMapper.updateWorkspace(workspace, request);

        return workspaceMapper.toWorkspaceResponse(workspaceRepository.save(workspace));
    }

    @Transactional
    public void deleteProjectInWorkspace(String workspaceId, String projectId) {
        Workspace workspace = workspaceRepository
                .findById(workspaceId)
                .orElseThrow(() -> new AppException(ErrorCode.WORKSPACE_NOT_FOUND));

        Project project =
                projectRepository.findById(projectId).orElseThrow(() -> new AppException(ErrorCode.PROJECT_NOT_FOUND));

        if (!project.getWorkspaces().contains(workspace)) {
            throw new AppException(ErrorCode.PROJECT_NOT_IN_WORKSPACE);
        }

        if (project.getWorkspaces().size() == 1) {
            projectRepository.delete(project);
            return;
        }

        project.getWorkspaces().remove(workspace);
        workspace.getProjects().remove(project);

        projectRepository.save(project);
    }
}
