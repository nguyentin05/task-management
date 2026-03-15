package com.ntt.task_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProjectResponse {
    String id;
    String name;
    String description;
    String createdBy;
    Instant startAt;
    Instant endAt;
    Instant createdAt;
    Instant updatedAt;
}
