package com.ntt.task_service.controller.external;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.ntt.task_service.dto.request.ColumnCreationRequest;
import com.ntt.task_service.dto.request.ColumnUpdateRequest;
import com.ntt.task_service.dto.response.ApiResponse;
import com.ntt.task_service.dto.response.ColumnResponse;
import com.ntt.task_service.dto.response.PageResponse;
import com.ntt.task_service.service.ColumnService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/projects/{projectId}/columns")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ColumnController {
    ColumnService columnService;

    @PostMapping
    ApiResponse<ColumnResponse> createColumnInProject(
            @PathVariable String projectId, @RequestBody @Valid ColumnCreationRequest request) {
        return ApiResponse.<ColumnResponse>builder()
                .result(columnService.createColumnInProject(projectId, request))
                .build();
    }

    @GetMapping
    ApiResponse<PageResponse<ColumnResponse>> getAllColumnInProject(
            @PathVariable String projectId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return ApiResponse.<PageResponse<ColumnResponse>>builder()
                .result(columnService.getAllColumnInProject(projectId, page, size))
                .build();
    }

    @PatchMapping("/{columnId}")
    ApiResponse<ColumnResponse> updateColumnInProject(
            @PathVariable String projectId,
            @PathVariable String columnId,
            @RequestBody @Valid ColumnUpdateRequest request) {
        return ApiResponse.<ColumnResponse>builder()
                .result(columnService.updateColumnInProject(projectId, columnId, request))
                .build();
    }

    @DeleteMapping("/{columnId}")
    ApiResponse<Void> deleteColumnInProject(@PathVariable String projectId, @PathVariable String columnId) {
        columnService.deleteColumnInProject(projectId, columnId);
        return ApiResponse.<Void>builder().message("Xóa cột thành công").build();
    }
}
