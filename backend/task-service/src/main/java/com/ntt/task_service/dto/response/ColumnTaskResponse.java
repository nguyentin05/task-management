package com.ntt.task_service.dto.response;

import jakarta.persistence.Column;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ColumnTaskResponse {
    String id;
    String title;
    String description;
    long position;
    Instant startAt;
    Instant dueAt;
    Instant completedAt;
}
