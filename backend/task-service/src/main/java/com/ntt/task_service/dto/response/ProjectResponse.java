package com.ntt.task_service.dto.response;

import java.time.Instant;

import lombok.*;
import lombok.experimental.FieldDefaults;

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
