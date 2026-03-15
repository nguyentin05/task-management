package com.ntt.task_service.mapper;

import com.ntt.task_service.domain.Project;
import com.ntt.task_service.domain.Workspace;
import com.ntt.task_service.dto.request.ProjectCreationRequest;
import com.ntt.task_service.dto.request.ProjectUpdateRequest;
import com.ntt.task_service.dto.request.WorkspaceCreationRequest;
import com.ntt.task_service.dto.request.WorkspaceUpdateRequest;
import com.ntt.task_service.dto.response.ProjectResponse;
import com.ntt.task_service.dto.response.WorkspaceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    Project toProject(ProjectCreationRequest request);

    void updateProject(@MappingTarget Project project, ProjectUpdateRequest request);

    ProjectResponse toProjectResponse(Project project);
}
