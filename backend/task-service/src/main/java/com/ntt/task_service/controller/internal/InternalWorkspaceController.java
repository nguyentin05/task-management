package com.ntt.task_service.controller.internal;

import com.ntt.task_service.dto.request.WorkspaceCreationRequest;
import com.ntt.task_service.dto.response.ApiResponse;
import com.ntt.task_service.dto.response.WorkspaceResponse;
import com.ntt.task_service.service.WorkspaceService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InternalWorkspaceController {
    WorkspaceService workspaceService;

    @PostMapping("/workspace")
    ApiResponse<WorkspaceResponse> createProfile(@RequestBody WorkspaceCreationRequest request) {
        return ApiResponse.<WorkspaceResponse>builder()
                .result(workspaceService.createWorkspace(request))
                .build();
    }
}
