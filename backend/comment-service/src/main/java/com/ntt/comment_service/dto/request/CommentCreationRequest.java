package com.ntt.comment_service.dto.request;

import jakarta.validation.constraints.NotBlank;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentCreationRequest {

    @NotBlank(message = "FIELD_REQUIRED")
    String content;

    String parentCommentId;
}
