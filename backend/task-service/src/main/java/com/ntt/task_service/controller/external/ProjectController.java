package com.ntt.task_service.controller.external;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ntt.task_service.dto.request.*;
import com.ntt.task_service.dto.response.*;
import com.ntt.task_service.service.ProjectService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProjectController {
    ProjectService projectService;

    @PostMapping
    ApiResponse<ProjectResponse> createProject(@RequestBody @Valid ProjectCreationRequest request) {
        return ApiResponse.<ProjectResponse>builder()
                .result(projectService.createProject(request))
                .build();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<List<ProjectResponse>> getAllProject() {
        return ApiResponse.<List<ProjectResponse>>builder()
                .result(projectService.getAllProject())
                .build();
    }

    @GetMapping("/{projectId}")
    ApiResponse<ProjectResponse> getProject(@PathVariable String projectId) {
        return ApiResponse.<ProjectResponse>builder()
                .result(projectService.getProject(projectId))
                .build();
    }

    @PatchMapping("/{projectId}")
    ApiResponse<ProjectResponse> updateProject(
            @PathVariable String projectId, @RequestBody @Valid ProjectUpdateRequest request) {
        return ApiResponse.<ProjectResponse>builder()
                .result(projectService.updateProject(projectId, request))
                .build();
    }

    @DeleteMapping("/{projectId}")
    ApiResponse<Void> deleteProject(@PathVariable String projectId) {
        projectService.deleteProject(projectId);
        return ApiResponse.<Void>builder().message("Xóa project thành công").build();
    }

    @GetMapping("/{projectId}/statistics")
    ApiResponse<ProjectStatisticsResponse> getProjectStatistics(@PathVariable String projectId) {
        return ApiResponse.<ProjectStatisticsResponse>builder()
                .result(projectService.getProjectStatistics(projectId))
                .build();
    }
}
