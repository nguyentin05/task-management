package com.ntt.comment_service.mapper;

import com.ntt.comment_service.domain.Comment;
import com.ntt.comment_service.dto.request.CommentCreationRequest;
import com.ntt.comment_service.dto.request.CommentUpdateRequest;
import com.ntt.comment_service.dto.response.CommentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    Comment toComment(CommentCreationRequest request);

    CommentResponse toCommentResponse(Comment comment);

    void updateComment(@MappingTarget Comment comment, CommentUpdateRequest request);
}
