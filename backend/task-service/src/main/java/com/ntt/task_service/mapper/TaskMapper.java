package com.ntt.task_service.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.ntt.task_service.domain.Task;
import com.ntt.task_service.dto.request.TaskCreationRequest;
import com.ntt.task_service.dto.request.TaskUpdateRequest;
import com.ntt.task_service.dto.response.TaskResponse;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    Task toTask(TaskCreationRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateTask(@MappingTarget Task task, TaskUpdateRequest request);

    TaskResponse toTaskResponse(Task task);
}
