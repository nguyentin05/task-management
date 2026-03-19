package com.ntt.task_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.ntt.task_service.domain.Task;
import com.ntt.task_service.dto.request.TaskCreationRequest;
import com.ntt.task_service.dto.request.TaskUpdateRequest;
import com.ntt.task_service.dto.response.TaskResponse;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    Task toTask(TaskCreationRequest request);

    void updateTask(@MappingTarget Task task, TaskUpdateRequest request);

    TaskResponse toTaskResponse(Task task);
}
