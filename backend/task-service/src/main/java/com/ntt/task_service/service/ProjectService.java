package com.ntt.task_service.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ntt.task_service.domain.*;
import com.ntt.task_service.dto.request.*;
import com.ntt.task_service.dto.response.*;
import com.ntt.task_service.exception.AppException;
import com.ntt.task_service.exception.ErrorCode;
import com.ntt.task_service.mapper.ProjectMapper;
import com.ntt.task_service.repository.*;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProjectService {
    ProjectRepository projectRepository;
    WorkspaceRepository workspaceRepository;
    ProjectMapper projectMapper;
    ProjectMemberRepository projectMemberRepository;
    ProjectAuthorizationService projectAuthorizationService;
    ColumnRepository columnRepository;
    TaskRepository taskRepository;

    @Transactional
    public ProjectResponse createProject(ProjectCreationRequest request) {
        String userId = projectAuthorizationService.getCurrentUserId();

        Workspace workspace = workspaceRepository
                .findByUserId(userId)
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

        Column toDo = Column.builder()
                .projectId(project.getId())
                .name("To Do")
                .position(1.0)
                .build();

        Column inProgress = Column.builder()
                .projectId(project.getId())
                .name("In Progress")
                .position(2.0)
                .build();

        Column done = Column.builder()
                .projectId(project.getId())
                .name("Done")
                .isDoneColumn(true)
                .position(3.0)
                .build();

        columnRepository.saveAll(List.of(toDo, inProgress, done));

        return projectMapper.toProjectResponse(project);
    }

    public PageResponse<ProjectResponse> getAllProject(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<Project> pageData = projectRepository.findAll(pageable);

        return PageResponse.<ProjectResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(pageData.getContent().stream()
                        .map(projectMapper::toProjectResponse)
                        .toList())
                .build();
    }

    public ProjectResponse getProject(String id) {
        Project project = getProjectOrThrow(id);

        projectAuthorizationService.validateCanView(id);

        return projectMapper.toProjectResponse(project);
    }

    public ProjectResponse updateProject(String id, ProjectUpdateRequest request) {
        Project project = getProjectOrThrow(id);

        projectAuthorizationService.validateCanManage(id);

        projectMapper.updateProject(project, request);
        project = projectRepository.save(project);

        return projectMapper.toProjectResponse(project);
    }

    @Transactional
    public void deleteProject(String id) {
        Project project = getProjectOrThrow(id);
        projectAuthorizationService.validateCanManage(id);

        projectRepository.delete(project);
    }

    public ProjectStatisticsResponse getProjectStatistics(String id) {
        Project project = getProjectOrThrow(id);
        projectAuthorizationService.validateCanView(id);

        List<String> columnIds = columnRepository.findColumnIdsByProjectId(id);
        long totalTasks = taskRepository.countByColumnIdIn(columnIds);

        long completedTasks = columnRepository.findDoneColumnIdByProjectId(id)
                .map(taskRepository::countByColumnId)
                .orElse(0L);

        double completionRate = totalTasks == 0 ? 0 : Math.round((double) completedTasks / totalTasks * 1000) / 10.0;
        long totalMembers = projectMemberRepository.countByProjectId(id);

        return ProjectStatisticsResponse.builder()
                .projectId(id)
                .projectName(project.getName())
                .totalTasks((int) totalTasks)
                .completedTasks((int) completedTasks)
                .completionRate(completionRate)
                .totalMembers((int) totalMembers)
                .totalColumns(columnIds.size())
                .build();
    }

    private Project getProjectOrThrow(String projectId) {
        return projectRepository.findById(projectId).orElseThrow(() -> new AppException(ErrorCode.PROJECT_NOT_FOUND));
    }
}
