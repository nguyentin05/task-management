package com.ntt.task_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskResponse {
    String id;
    String columnId;
    String title;
    String description;
    Double position;
    Instant startAt;
    Instant dueAt;
    String assigneeId;
    String label;
    String createdBy;
    Instant createdAt;
    Instant updatedAt;
    Instant completedAt;
}
