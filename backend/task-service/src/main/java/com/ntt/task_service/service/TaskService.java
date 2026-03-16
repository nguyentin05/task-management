package com.ntt.task_service.service;

import com.ntt.task_service.domain.Column;
import com.ntt.task_service.domain.Task;
import com.ntt.task_service.dto.request.TaskAssignRequest;
import com.ntt.task_service.dto.request.TaskCreationRequest;
import com.ntt.task_service.dto.request.TaskMoveRequest;
import com.ntt.task_service.dto.request.TaskUpdateRequest;
import com.ntt.task_service.dto.response.TaskResponse;
import com.ntt.task_service.exception.AppException;
import com.ntt.task_service.exception.ErrorCode;
import com.ntt.task_service.mapper.TaskMapper;
import com.ntt.task_service.repository.ColumnRepository;
import com.ntt.task_service.repository.ProjectMemberRepository;
import com.ntt.task_service.repository.TaskRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TaskService {
    TaskMapper taskMapper;
    TaskRepository taskRepository;
    ProjectAuthorizationService projectAuthorizationService;
    ColumnRepository columnRepository;
    ProjectMemberRepository projectMemberRepository;

    public TaskResponse createTask(String id, TaskCreationRequest request) {
        Column column = columnRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COLUMN_NOT_FOUND));

        projectAuthorizationService.validateCanManage(column.getProjectId());

        Task task = taskMapper.toTask(request);
        task.setColumnId(id);
        task.setCreatedBy(projectAuthorizationService.getCurrentUserId());

        return taskMapper.toTaskResponse(taskRepository.save(task));
    }

    public TaskResponse getTask(String columnId, String taskId) {
        Column column = columnRepository.findById(columnId)
                .orElseThrow(() -> new AppException(ErrorCode.COLUMN_NOT_FOUND));

        projectAuthorizationService.validateCanView(column.getProjectId());

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new AppException(ErrorCode.TASK_NOT_FOUND));

        if (!task.getColumnId().equals(columnId))
            throw new AppException(ErrorCode.TASK_NOT_IN_COLUMN);

        return taskMapper.toTaskResponse(task);
    }

    public TaskResponse updateTask(String id, TaskUpdateRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.TASK_NOT_FOUND));

        Column column = columnRepository.findById(task.getColumnId())
                .orElseThrow(() -> new AppException(ErrorCode.COLUMN_NOT_FOUND));

        projectAuthorizationService.validateCanView(column.getProjectId());

        taskMapper.updateTask(task, request);

        return taskMapper.toTaskResponse(taskRepository.save(task));
    }

    public TaskResponse moveTask(String id, TaskMoveRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.TASK_NOT_FOUND));

        Column column = columnRepository.findById(task.getColumnId())
                .orElseThrow(() -> new AppException(ErrorCode.COLUMN_NOT_FOUND));

        projectAuthorizationService.validateCanView(column.getProjectId());

        Column newColumn = columnRepository.findById(request.getColumnId())
                .orElseThrow(() -> new AppException(ErrorCode.COLUMN_NOT_FOUND));

        if (!newColumn.getProjectId().equals(column.getProjectId()))
            throw new AppException(ErrorCode.COLUMN_NOT_IN_PROJECT);

        task.setColumnId(request.getColumnId());
        task.setPosition(request.getPosition());

        return taskMapper.toTaskResponse(taskRepository.save(task));
    }

    public TaskResponse assignMember(String id, TaskAssignRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.TASK_NOT_FOUND));

        Column column = columnRepository.findById(task.getColumnId())
                .orElseThrow(() -> new AppException(ErrorCode.COLUMN_NOT_FOUND));

        projectAuthorizationService.validateCanManage(column.getProjectId());

        if (!projectMemberRepository.existsByProjectIdAndUserId(column.getProjectId(), request.getUserId()))
            throw new AppException(ErrorCode.USER_NOT_IN_PROJECT);

        task.setAssigneeId(request.getUserId());

        return taskMapper.toTaskResponse(taskRepository.save(task));
    }

    public void unassignMember(String taskId, String userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new AppException(ErrorCode.TASK_NOT_FOUND));

        Column column = columnRepository.findById(task.getColumnId())
                .orElseThrow(() -> new AppException(ErrorCode.COLUMN_NOT_FOUND));

        projectAuthorizationService.validateCanManage(column.getProjectId());

        if (!userId.equals(task.getAssigneeId()))
            throw new AppException(ErrorCode.USER_NOT_ASSIGNED);

        task.setAssigneeId(null);

        taskRepository.save(task);
    }

    public void deleteTask(String taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new AppException(ErrorCode.TASK_NOT_FOUND));

        Column column = columnRepository.findById(task.getColumnId())
                .orElseThrow(() -> new AppException(ErrorCode.COLUMN_NOT_FOUND));

        projectAuthorizationService.validateCanManage(column.getProjectId());

        taskRepository.delete(task);
    }

}
