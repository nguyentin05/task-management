package com.ntt.task_service.dto.request;

import java.time.Instant;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.ntt.task_service.validator.TimeConstraint;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@TimeConstraint(startField = "startAt", endField = "endAt", message = "TIME_INVALID")
public class ProjectCreationRequest {
    @NotBlank(message = "FIELD_REQUIRED")
    @Size(max = 255, message = "FIELD_SIZE_INVALID")
    String name;

    String description;

    @NotNull(message = "FIELD_REQUIRED")
    @FutureOrPresent(message = "TIME_IN_PAST")
    Instant startAt;

    @NotNull(message = "FIELD_REQUIRED")
    Instant endAt;
}
