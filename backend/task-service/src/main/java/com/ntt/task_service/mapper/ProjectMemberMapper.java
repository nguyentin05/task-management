package com.ntt.task_service.mapper;

import org.mapstruct.Mapper;

import com.ntt.task_service.domain.ProjectMember;
import com.ntt.task_service.dto.response.ProjectMemberResponse;

@Mapper(componentModel = "spring")
public interface ProjectMemberMapper {
    ProjectMemberResponse toProjectMemberResponse(ProjectMember projectMember);
}
