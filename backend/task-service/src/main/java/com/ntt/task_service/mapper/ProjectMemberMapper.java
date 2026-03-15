package com.ntt.task_service.mapper;

import com.ntt.task_service.domain.ProjectMember;
import com.ntt.task_service.dto.response.ProjectMemberResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProjectMemberMapper {
    ProjectMemberResponse toProjectMemberResponse(ProjectMember projectMember);
}
