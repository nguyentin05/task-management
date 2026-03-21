package com.ntt.comment_service.service;

import com.ntt.comment_service.domain.Comment;
import com.ntt.comment_service.dto.request.CommentCreationRequest;
import com.ntt.comment_service.dto.request.CommentUpdateRequest;
import com.ntt.comment_service.dto.response.CommentResponse;
import com.ntt.comment_service.exception.AppException;
import com.ntt.comment_service.exception.ErrorCode;
import com.ntt.comment_service.mapper.CommentMapper;
import com.ntt.comment_service.repository.CommentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CommentService {
    CommentMapper commentMapper;
    CommentRepository commentRepository;

    public List<CommentResponse> getCommentsByTask(String id) {
        return commentRepository.findByTaskId(id).stream().map(commentMapper::toCommentResponse).toList();
    }

    public CommentResponse createComment(String id, CommentCreationRequest request) {
        String userId = getCurrentUserId();

        Comment comment = commentMapper.toComment(request);
        comment.setTaskId(id);
        comment.setUserId(userId);

        return commentMapper.toCommentResponse(commentRepository.save(comment));
    }

    public CommentResponse updateComment(String commentId, CommentUpdateRequest request) {
        String userId = getCurrentUserId();

        Comment comment = getCommentOrThrow(commentId);

        if (!comment.getUserId().equals(userId)) throw new AppException(ErrorCode.ACCESS_DENIED);

        commentMapper.updateComment(comment, request);
        comment.setEdited(true);
        comment.setUpdatedAt(Instant.now());

        return commentMapper.toCommentResponse(commentRepository.save(comment));
    }

    public void deleteComment(String commentId) {
        String userId = getCurrentUserId();

        Comment comment = getCommentOrThrow(commentId);

        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !comment.getUserId().equals(userId))
            throw new AppException(ErrorCode.ACCESS_DENIED);

        commentRepository.delete(comment);
    }

    public void deleteCommentByTask(String taskId) {
        commentRepository.deleteByTaskId(taskId);
    }

    private Comment getCommentOrThrow(String commentId) {
        return commentRepository
                .findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));
    }

    private String getCurrentUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
