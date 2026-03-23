package com.ntt.task_service.controller.external;

import com.ntt.task_service.dto.response.PageResponse;
import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ntt.task_service.dto.request.WorkspaceUpdateRequest;
import com.ntt.task_service.dto.response.ApiResponse;
import com.ntt.task_service.dto.response.ProjectResponse;
import com.ntt.task_service.dto.response.WorkspaceResponse;
import com.ntt.task_service.service.WorkspaceService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/workspaces")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WorkspaceController {
    WorkspaceService workspaceService;

    @GetMapping("/me")
    ApiResponse<WorkspaceResponse> getMyWorkspace() {
        return ApiResponse.<WorkspaceResponse>builder()
                .result(workspaceService.getMyWorkspace())
                .build();
    }

    @GetMapping("/me/projects")
    ApiResponse<PageResponse<ProjectResponse>> getProjectsInMyWorkspace(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "20") int size
    ) {
        return ApiResponse.<PageResponse<ProjectResponse>>builder()
                .result(workspaceService.getProjectsInMyWorkspace(page, size))
                .build();
    }

    @PatchMapping("/me")
    ApiResponse<WorkspaceResponse> updateMyWorkspace(@RequestBody @Valid WorkspaceUpdateRequest request) {
        return ApiResponse.<WorkspaceResponse>builder()
                .result(workspaceService.updateMyWorkspace(request))
                .build();
    }

    @DeleteMapping("/me/projects/{projectId}")
    ApiResponse<Void> deleteProjectInMyWorkspace(@PathVariable String projectId) {
        workspaceService.deleteProjectInMyWorkspace(projectId);
        return ApiResponse.<Void>builder()
                .message("Xóa dự án khỏi không gian làm việc thành công")
                .build();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<PageResponse<WorkspaceResponse>> getAllWorkspace(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size
    ) {
        return ApiResponse.<PageResponse<WorkspaceResponse>>builder()
                .result(workspaceService.getAllWorkspace(page, size))
                .build();
    }

    @GetMapping("/{workspaceId}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<WorkspaceResponse> getWorkspace(@PathVariable String workspaceId) {
        return ApiResponse.<WorkspaceResponse>builder()
                .result(workspaceService.getWorkspace(workspaceId))
                .build();
    }

    @GetMapping("/{workspaceId}/projects")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<PageResponse<ProjectResponse>> getProjectsInWorkspace(
            @PathVariable String workspaceId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size
    ) {
        return ApiResponse.<PageResponse<ProjectResponse>>builder()
                .result(workspaceService.getProjectsInWorkspace(workspaceId, page, size))
                .build();
    }

    @PatchMapping("/{workspaceId}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<WorkspaceResponse> updateWorkspace(
            @PathVariable String workspaceId, @RequestBody @Valid WorkspaceUpdateRequest request) {
        return ApiResponse.<WorkspaceResponse>builder()
                .result(workspaceService.updateWorkspace(workspaceId, request))
                .build();
    }

    @DeleteMapping("/{workspaceId}/projects/{projectId}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<Void> deleteProjectInWorkspace(@PathVariable String workspaceId,
                                               @PathVariable String projectId) {
        workspaceService.deleteProjectInWorkspace(workspaceId, projectId);
        return ApiResponse.<Void>builder()
                .message("Xóa dự án khỏi không gian làm việc thành công")
                .build();
    }
}
