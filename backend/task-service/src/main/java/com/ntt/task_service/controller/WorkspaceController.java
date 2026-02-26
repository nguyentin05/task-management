package com.ntt.task_service.controller;

import com.ntt.task_service.dto.response.ApiResponse;
import com.ntt.task_service.dto.response.WorkspaceResponse;
import com.ntt.task_service.service.WorkspaceService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workspace")
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

    @GetMapping("/{workspaceId}")
    ApiResponse<WorkspaceResponse> getWorkspace(@PathVariable String workspaceId) {
        return ApiResponse.<WorkspaceResponse>builder()
                .result(workspaceService.getWorkspace(workspaceId))
                .build();
    }

    @GetMapping
    ApiResponse<List<WorkspaceResponse>> getAllWorkspace() {
        return ApiResponse.<List<WorkspaceResponse>>builder()
                .result(workspaceService.getAllWorkspace())
                .build();
    }
}
