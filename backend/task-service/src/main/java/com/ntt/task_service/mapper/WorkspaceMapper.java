package com.ntt.task_service.mapper;

import com.ntt.task_service.domain.Workspace;
import com.ntt.task_service.dto.request.WorkspaceCreationRequest;
import com.ntt.task_service.dto.response.WorkspaceResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WorkspaceMapper {
    Workspace toWorkspace(WorkspaceCreationRequest request);

    WorkspaceResponse toWorkspaceResponse(Workspace workspace);
}
