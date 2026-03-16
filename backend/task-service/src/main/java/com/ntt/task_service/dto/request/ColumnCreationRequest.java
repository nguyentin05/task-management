package com.ntt.task_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ColumnCreationRequest {
    @NotBlank(message = "FIELD_REQUIRED")
    @Size(max = 255, message = "FIELD_SIZE_INVALID")
    String name;

    @NotNull(message = "FIELD_REQUIRED")
    @Size(min = 0, message = "POSITION_INVALID")
    Double position;
}
