package com.ntt.task_service.controller.external;

import com.ntt.task_service.dto.request.TaskAssignRequest;
import com.ntt.task_service.dto.request.TaskCreationRequest;
import com.ntt.task_service.dto.request.TaskMoveRequest;
import com.ntt.task_service.dto.request.TaskUpdateRequest;
import com.ntt.task_service.dto.response.ApiResponse;
import com.ntt.task_service.dto.response.TaskResponse;
import com.ntt.task_service.service.TaskService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TaskController {
    TaskService taskService;

    @PostMapping("/columns/{columnId}/tasks")
    ApiResponse<TaskResponse> createTask(@PathVariable String columnId,
                                         @RequestBody @Valid TaskCreationRequest request) {
        return ApiResponse.<TaskResponse>builder()
                .result(taskService.createTask(columnId, request))
                .build();
    }

    @GetMapping("/columns/{columnId}/tasks/{taskId}")
    ApiResponse<TaskResponse> getTask(@PathVariable String columnId,
                                      @PathVariable String taskId) {
        return ApiResponse.<TaskResponse>builder()
                .result(taskService.getTask(columnId, taskId))
                .build();
    }

    @PatchMapping("/tasks/{taskId}")
    ApiResponse<TaskResponse> updateTask(@PathVariable String taskId,
                                         @RequestBody @Valid TaskUpdateRequest request) {
        return ApiResponse.<TaskResponse>builder()
                .result(taskService.updateTask(taskId, request))
                .build();
    }

    @PutMapping("/tasks/{taskId}/move")
    ApiResponse<TaskResponse> moveTask(@PathVariable String taskId,
                                       @RequestBody @Valid TaskMoveRequest request) {
        return ApiResponse.<TaskResponse>builder()
                .result(taskService.moveTask(taskId, request))
                .build();
    }

    @PostMapping("/tasks/{taskId}/assignees")
    ApiResponse<TaskResponse> assignMember(@PathVariable String taskId,
                                           @RequestBody @Valid TaskAssignRequest request) {
        return ApiResponse.<TaskResponse>builder()
                .result(taskService.assignMember(taskId, request))
                .build();
    }

    @DeleteMapping("/tasks/{taskId}/assignees/{userId}")
    ApiResponse<Void> unassignMember(@PathVariable String taskId,
                                     @PathVariable String userId) {
        taskService.unassignMember(taskId, userId);
        return ApiResponse.<Void>builder()
                .message("Đã xóa thành viên khỏi task")
                .build();
    }

    @DeleteMapping("/tasks/{taskId}")
    ApiResponse<Void> deleteTask(@PathVariable String taskId) {
        taskService.deleteTask(taskId);
        return ApiResponse.<Void>builder()
                .message("Xóa task thành công")
                .build();
    }
}
