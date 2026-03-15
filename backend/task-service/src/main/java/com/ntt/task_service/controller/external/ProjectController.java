package com.ntt.task_service.controller.external;

import com.ntt.task_service.dto.request.ProjectCreationRequest;
import com.ntt.task_service.dto.request.ProjectMemberAddRequest;
import com.ntt.task_service.dto.request.ProjectUpdateRequest;
import com.ntt.task_service.dto.request.RoleMemberUpdateRequest;
import com.ntt.task_service.dto.response.*;
import com.ntt.task_service.service.ProjectService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    ApiResponse<ProjectResponse> updateProject(@PathVariable String projectId,
                                               @RequestBody @Valid ProjectUpdateRequest request) {
        return ApiResponse.<ProjectResponse>builder()
                .result(projectService.updateProject(projectId, request))
                .build();
    }

    @DeleteMapping("/{projectId}")
    ApiResponse<Void> deleteProject(@PathVariable String projectId) {
        projectService.deleteProject(projectId);
        return ApiResponse.<Void>builder()
                .message("Xóa project thành công")
                .build();
    }

    @GetMapping("/{projectId}/members")
    ApiResponse<List<ProjectMemberResponse>> getMembersInProject(@PathVariable String projectId) {
        return ApiResponse.<List<ProjectMemberResponse>>builder()
                .result(projectService.getMembersInProject(projectId))
                .build();
    }

    @GetMapping("/{projectId}/members/search")
    ApiResponse<List<MemberSearchResponse>> searchUsersToInvite(@PathVariable String projectId,
                                                                @RequestParam String email) {
        return ApiResponse.<List<MemberSearchResponse>>builder()
                .result(projectService.searchUsersToInvite(projectId, email))
                .build();
    }

    @PostMapping("/{projectId}/members")
    ApiResponse<ProjectMemberResponse> addMemberToProject(@PathVariable String projectId,
                                                          @RequestBody @Valid ProjectMemberAddRequest request) {
        return ApiResponse.<ProjectMemberResponse>builder()
                .result(projectService.addMemberToProject(projectId, request))
                .build();
    }

    @PutMapping("/{projectId}/members/{userId}")
    ApiResponse<ProjectMemberResponse> updateRoleMemberInProject(@PathVariable String projectId,
                                                                 @PathVariable String userId,
                                                                 @RequestBody @Valid RoleMemberUpdateRequest request) {
        return ApiResponse.<ProjectMemberResponse>builder()
                .result(projectService.updateRoleMemberInProject(projectId, userId, request))
                .build();
    }

    @DeleteMapping("/{projectId}/members/{userId}")
    ApiResponse<Void> removeMemberFromProject(@PathVariable String projectId,
                                              @PathVariable String userId) {
        projectService.removeMemberFromProject(projectId, userId);

        return ApiResponse.<Void>builder()
                .message("Đã xóa thành viên thành công")
                .build();
    }

//    @PostMapping("/{projectId}/columns")
//    ApiResponse<ColumnResponse> createColumnInProject(@PathVariable String projectId,
//                                                      @RequestBody @Valid ColumnCreationRequest request) {
//        return ApiResponse.<ColumnResponse>builder()
//                .result(projectService.createColumnInProject(projectId, request))
//                .build();
//    }
//
//    @GetMapping("/{projectId}/columns")
//    ApiResponse<List<ColumnResponse>> getAllColumnInProject(@PathVariable String projectId) {
//        return ApiResponse.<List<ColumnResponse>>builder()
//                .result(projectService.getAllColumnInProject(projectId))
//                .build();
//    }
//
//    @PatchMapping("/{projectId}/columns/{columnId}")
//    ApiResponse<ColumnResponse> updateColumnInProject(@PathVariable String projectId,
//                                                      @PathVariable String columnId,
//                                                      @RequestBody @Valid ColumnUpdateRequest request) {
//        return ApiResponse.<ColumnResponse>builder()
//                .result(projectService.updateColumnInProject(projectId, columnId, request))
//                .build();
//    }
//
//    @DeleteMapping("/{projectId}/columns/{columnId}")
//    ApiResponse<Void> daleteColumnInProject(@PathVariable String projectId,
//                                            @PathVariable String columnId) {
//
//        projectService.daleteColumnInProject(projectId, columnId);
//        return ApiResponse.<Void>builder()
//                .message("Xóa cột thành công")
//                .build();
//    }
}
