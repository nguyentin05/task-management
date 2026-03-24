package com.ntt.comment_service.controller.external;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.ntt.comment_service.dto.request.CommentCreationRequest;
import com.ntt.comment_service.dto.request.CommentUpdateRequest;
import com.ntt.comment_service.dto.response.ApiResponse;
import com.ntt.comment_service.dto.response.CommentResponse;
import com.ntt.comment_service.dto.response.PageResponse;
import com.ntt.comment_service.service.CommentService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentController {
    CommentService commentService;

    @GetMapping("/tasks/{taskId}/comments")
    public ApiResponse<PageResponse<CommentResponse>> getCommentsOfTask(
            @PathVariable String taskId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return ApiResponse.<PageResponse<CommentResponse>>builder()
                .result(commentService.getCommentsByTask(taskId, page, size))
                .build();
    }

    @PostMapping("/tasks/{taskId}/comments")
    public ApiResponse<CommentResponse> createComment(
            @PathVariable String taskId, @RequestBody @Valid CommentCreationRequest request) {

        return ApiResponse.<CommentResponse>builder()
                .result(commentService.createComment(taskId, request))
                .build();
    }

    @PutMapping("/comments/{commentId}")
    public ApiResponse<CommentResponse> updateComment(
            @PathVariable String commentId, @RequestBody @Valid CommentUpdateRequest request) {

        return ApiResponse.<CommentResponse>builder()
                .result(commentService.updateComment(commentId, request))
                .build();
    }

    @DeleteMapping("/comments/{commentId}")
    public ApiResponse<Void> deleteComment(@PathVariable String commentId) {
        commentService.deleteComment(commentId);

        return ApiResponse.<Void>builder().message("Xóa comment thành công").build();
    }
}
