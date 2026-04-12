package com.ntt.comment_service.service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ntt.comment_service.domain.Comment;
import com.ntt.comment_service.dto.request.CommentCreationRequest;
import com.ntt.comment_service.dto.request.CommentUpdateRequest;
import com.ntt.comment_service.dto.response.CommentResponse;
import com.ntt.comment_service.dto.response.PageResponse;
import com.ntt.comment_service.dto.response.ProfileSearchResponse;
import com.ntt.comment_service.dto.response.UserSearchResponse;
import com.ntt.comment_service.exception.AppException;
import com.ntt.comment_service.exception.ErrorCode;
import com.ntt.comment_service.mapper.CommentMapper;
import com.ntt.comment_service.repository.CommentRepository;
import com.ntt.comment_service.repository.httpclient.AuthenticationClient;
import com.ntt.comment_service.repository.httpclient.ProfileClient;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CommentService {
    CommentMapper commentMapper;
    CommentRepository commentRepository;
    CommentAuthorizationService commentAuthorizationService;
    AuthenticationClient authenticationClient;
    ProfileClient profileClient;

    public PageResponse<CommentResponse> getCommentsByTask(String id, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<Comment> pageData = commentRepository.findByTaskId(id, pageable);

        List<String> userIds = pageData.getContent().stream()
                .map(Comment::getUserId)
                .distinct()
                .toList();

        Map<String, ProfileSearchResponse> profileMap = userIds.isEmpty()
                ? Map.of()
                : profileClient.searchByUserIds(userIds).getResult().stream()
                        .collect(Collectors.toMap(ProfileSearchResponse::getUserId, p -> p));

        Map<String, UserSearchResponse> userMap = userIds.isEmpty()
                ? Map.of()
                : authenticationClient.searchByUserIds(userIds).getResult().stream()
                        .collect(Collectors.toMap(UserSearchResponse::getId, u -> u));

        return PageResponse.<CommentResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(pageData.getContent().stream()
                        .map(comment -> {
                            ProfileSearchResponse profile = profileMap.get(comment.getUserId());
                            UserSearchResponse user = userMap.get(comment.getUserId());
                            return CommentResponse.builder()
                                    .id(comment.getId())
                                    .taskId(comment.getTaskId())
                                    .userId(comment.getUserId())
                                    .email(user != null ? user.getEmail() : null)
                                    .firstName(profile != null ? profile.getFirstName() : null)
                                    .lastName(profile != null ? profile.getLastName() : null)
                                    .avatar(profile != null ? profile.getAvatar() : null)
                                    .isEdited(comment.getIsEdited())
                                    .content(comment.getContent())
                                    .parentCommentId(comment.getParentCommentId())
                                    .createdAt(comment.getCreatedAt())
                                    .build();
                        })
                        .toList())
                .build();
    }

    public CommentResponse createComment(String id, CommentCreationRequest request) {
        String userId = commentAuthorizationService.getCurrentUserId();

        Comment comment = commentMapper.toComment(request);
        comment.setTaskId(id);
        comment.setUserId(userId);

        return commentMapper.toCommentResponse(commentRepository.save(comment));
    }

    public CommentResponse updateComment(String commentId, CommentUpdateRequest request) {
        String userId = commentAuthorizationService.getCurrentUserId();

        Comment comment = getCommentOrThrow(commentId);

        if (!comment.getUserId().equals(userId)) throw new AppException(ErrorCode.ACCESS_DENIED);

        commentMapper.updateComment(comment, request);
        comment.setIsEdited(true);
        comment.setUpdatedAt(Instant.now());

        return commentMapper.toCommentResponse(commentRepository.save(comment));
    }

    public void deleteComment(String commentId) {
        String userId = commentAuthorizationService.getCurrentUserId();
        boolean isAdmin = commentAuthorizationService.isAdmin();

        Comment comment = getCommentOrThrow(commentId);

        if (!isAdmin && !comment.getUserId().equals(userId)) throw new AppException(ErrorCode.ACCESS_DENIED);

        commentRepository.delete(comment);
    }

    public void deleteCommentByTask(String taskId) {
        commentRepository.deleteByTaskId(taskId);
    }

    private Comment getCommentOrThrow(String commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));
    }
}
