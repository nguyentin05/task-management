package com.ntt.comment_service.controller.external;

import java.util.List;

import com.ntt.comment_service.dto.response.ApiResponse;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ntt.comment_service.dto.request.CommentCreationRequest;
import com.ntt.comment_service.dto.request.CommentUpdateRequest;
import com.ntt.comment_service.dto.response.CommentResponse;
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
    public ApiResponse<List<CommentResponse>> getCommentsOfTask(@PathVariable String taskId) {

        return ApiResponse.<List<CommentResponse>>builder()
                .result(commentService.getCommentsByTask(taskId))
                .build();
    }

    @PostMapping("/tasks/{taskId}/comments")
    public ApiResponse<CommentResponse> createComment(@PathVariable String taskId,
                                                      @RequestBody @Valid CommentCreationRequest request) {

        return ApiResponse.<CommentResponse>builder()
                .result(commentService.createComment(taskId, request))
                .build();
    }

    @PutMapping("/comments/{commentId}")
    public ApiResponse<CommentResponse> updateComment(@PathVariable String commentId,
                                                      @RequestBody @Valid CommentUpdateRequest request) {

        return ApiResponse.<CommentResponse>builder()
                .result(commentService.updateComment(commentId, request))
                .build();
    }

    @DeleteMapping("/comments/{commentId}")
    public ApiResponse<Void> deleteComment(@PathVariable String commentId) {
        commentService.deleteComment(commentId);

        return ApiResponse.<Void>builder()
                .message("Xóa comment thành công")
                .build();
    }
}