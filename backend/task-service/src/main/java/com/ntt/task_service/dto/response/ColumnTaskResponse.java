package com.ntt.task_service.dto.response;

import java.time.Instant;

import com.ntt.task_service.domain.TaskLabel;

import lombok.*;
import lombok.experimental.FieldDefaults;

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
    TaskLabel label;
    Instant startAt;
    Instant dueAt;
    Instant completedAt;
}
