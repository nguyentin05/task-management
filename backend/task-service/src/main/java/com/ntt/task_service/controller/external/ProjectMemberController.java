package com.ntt.task_service.controller.external;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.ntt.task_service.dto.request.ProjectMemberAddRequest;
import com.ntt.task_service.dto.request.RoleMemberUpdateRequest;
import com.ntt.task_service.dto.response.ApiResponse;
import com.ntt.task_service.dto.response.MemberSearchResponse;
import com.ntt.task_service.dto.response.PageResponse;
import com.ntt.task_service.dto.response.ProjectMemberResponse;
import com.ntt.task_service.service.ProjectMemberService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/projects/{projectId}/members")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProjectMemberController {

    ProjectMemberService projectMemberService;

    @GetMapping
    ApiResponse<PageResponse<ProjectMemberResponse>> getMembersInProject(
            @PathVariable String projectId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "20") int size) {
        return ApiResponse.<PageResponse<ProjectMemberResponse>>builder()
                .result(projectMemberService.getMembersInProject(projectId, page, size))
                .build();
    }

    @GetMapping("/search")
    ApiResponse<List<MemberSearchResponse>> searchUsersToInvite(
            @PathVariable String projectId, @RequestParam String email) {
        return ApiResponse.<List<MemberSearchResponse>>builder()
                .result(projectMemberService.searchUsersToInvite(projectId, email))
                .build();
    }

    @PostMapping
    ApiResponse<ProjectMemberResponse> addMemberToProject(
            @PathVariable String projectId, @RequestBody @Valid ProjectMemberAddRequest request) {
        return ApiResponse.<ProjectMemberResponse>builder()
                .result(projectMemberService.addMemberToProject(projectId, request))
                .build();
    }

    @PutMapping("/{userId}")
    ApiResponse<ProjectMemberResponse> updateRoleMemberInProject(
            @PathVariable String projectId,
            @PathVariable String userId,
            @RequestBody @Valid RoleMemberUpdateRequest request) {
        return ApiResponse.<ProjectMemberResponse>builder()
                .result(projectMemberService.updateRoleMemberInProject(projectId, userId, request))
                .build();
    }

    @DeleteMapping("/{userId}")
    ApiResponse<Void> removeMemberFromProject(@PathVariable String projectId, @PathVariable String userId) {
        projectMemberService.removeMemberFromProject(projectId, userId);

        return ApiResponse.<Void>builder()
                .message("Đã xóa thành viên thành công")
                .build();
    }
}
