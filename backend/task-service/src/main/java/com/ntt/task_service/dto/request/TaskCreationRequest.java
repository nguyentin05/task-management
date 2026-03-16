package com.ntt.task_service.dto.request;

import com.ntt.task_service.domain.TaskLabel;
import com.ntt.task_service.validator.TimeConstraint;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@TimeConstraint(startField = "startAt", endField = "dueAt", message = "TIME_INVALID")
public class TaskCreationRequest {
    @NotBlank(message = "FIELD_REQUIRED")
    @Size(max = 255, message = "FIELD_SIZE_INVALID")
    String title;

    String description;

    @NotNull(message = "FIELD_REQUIRED")
    @FutureOrPresent(message = "TIME_IN_PASS")
    Instant startAt;

    @NotNull(message = "FIELD_REQUIRED")
    Instant dueAt;

    @NotNull(message = "FIELD_REQUIRED")
    @Size(min = 0, message = "POSITION_INVALID")
    Double position;

    @NotNull(message = "FIELD_REQUIRED")
    TaskLabel label;
}
