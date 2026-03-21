package com.ntt.comment_service.dto.response;

import java.time.Instant;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentResponse {
    String id;
    String taskId;
    String userId;
    boolean isEdited;
    String content;
    String parentCommentId;
    Instant createdAt;
}