package com.ntt.task_service.dto.request;

import jakarta.validation.constraints.NotBlank;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskAssignRequest {
    @NotBlank(message = "FIELD_REQUIRED")
    String userId;
}
