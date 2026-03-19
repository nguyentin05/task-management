package com.ntt.task_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.ntt.task_service.domain.Column;
import com.ntt.task_service.dto.request.ColumnCreationRequest;
import com.ntt.task_service.dto.request.ColumnUpdateRequest;
import com.ntt.task_service.dto.response.ColumnResponse;

@Mapper(componentModel = "spring")
public interface ColumnMapper {
    Column toColumn(ColumnCreationRequest request);

    void updateColumn(@MappingTarget Column column, ColumnUpdateRequest request);

    @Mapping(target = "columnTaskResponses", source = "tasks")
    ColumnResponse toColumnResponse(Column column);
}
