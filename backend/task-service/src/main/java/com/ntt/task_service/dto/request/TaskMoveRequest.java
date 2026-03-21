package com.ntt.task_service.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskMoveRequest {
    @NotBlank(message = "FIELD_REQUIRED")
    String columnId;

    @NotNull(message = "FIELD_REQUIRED")
    @DecimalMin(value = "0.0", message = "POSITION_INVALID")
    Double position;
}
