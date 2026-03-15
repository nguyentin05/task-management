package com.ntt.task_service.mapper;

import com.ntt.task_service.domain.Workspace;
import com.ntt.task_service.dto.request.WorkspaceCreationRequest;
import com.ntt.task_service.dto.request.WorkspaceUpdateRequest;
import com.ntt.task_service.dto.response.WorkspaceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface WorkspaceMapper {
    Workspace toWorkspace(WorkspaceCreationRequest request);

    WorkspaceResponse toWorkspaceResponse(Workspace workspace);

    void updateWorkspace(@MappingTarget Workspace workspace, WorkspaceUpdateRequest request);
}
