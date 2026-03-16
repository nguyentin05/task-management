package com.ntt.task_service.controller.external;

import com.ntt.task_service.dto.request.ProjectMemberAddRequest;
import com.ntt.task_service.dto.request.RoleMemberUpdateRequest;
import com.ntt.task_service.dto.response.ApiResponse;
import com.ntt.task_service.dto.response.MemberSearchResponse;
import com.ntt.task_service.dto.response.ProjectMemberResponse;
import com.ntt.task_service.service.ProjectMemberService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects/{projectId}/members")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProjectMemberController {

    ProjectMemberService projectMemberService;

    @GetMapping
    ApiResponse<List<ProjectMemberResponse>> getMembersInProject(@PathVariable String projectId) {
        return ApiResponse.<List<ProjectMemberResponse>>builder()
                .result(projectMemberService.getMembersInProject(projectId))
                .build();
    }

    @GetMapping("/search")
    ApiResponse<List<MemberSearchResponse>> searchUsersToInvite(@PathVariable String projectId,
                                                                @RequestParam String email) {
        return ApiResponse.<List<MemberSearchResponse>>builder()
                .result(projectMemberService.searchUsersToInvite(projectId, email))
                .build();
    }

    @PostMapping
    ApiResponse<ProjectMemberResponse> addMemberToProject(@PathVariable String projectId,
                                                          @RequestBody @Valid ProjectMemberAddRequest request) {
        return ApiResponse.<ProjectMemberResponse>builder()
                .result(projectMemberService.addMemberToProject(projectId, request))
                .build();
    }

    @PutMapping("/{userId}")
    ApiResponse<ProjectMemberResponse> updateRoleMemberInProject(@PathVariable String projectId,
                                                                 @PathVariable String userId,
                                                                 @RequestBody @Valid RoleMemberUpdateRequest request) {
        return ApiResponse.<ProjectMemberResponse>builder()
                .result(projectMemberService.updateRoleMemberInProject(projectId, userId, request))
                .build();
    }

    @DeleteMapping("/{userId}")
    ApiResponse<Void> removeMemberFromProject(@PathVariable String projectId,
                                              @PathVariable String userId) {
        projectMemberService.removeMemberFromProject(projectId, userId);

        return ApiResponse.<Void>builder()
                .message("Đã xóa thành viên thành công")
                .build();
    }
}
