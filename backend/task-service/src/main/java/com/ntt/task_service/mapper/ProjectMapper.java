package com.ntt.task_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.ntt.task_service.domain.Project;
import com.ntt.task_service.dto.request.ProjectCreationRequest;
import com.ntt.task_service.dto.request.ProjectUpdateRequest;
import com.ntt.task_service.dto.response.ProjectResponse;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    Project toProject(ProjectCreationRequest request);

    void updateProject(@MappingTarget Project project, ProjectUpdateRequest request);

    ProjectResponse toProjectResponse(Project project);
}
