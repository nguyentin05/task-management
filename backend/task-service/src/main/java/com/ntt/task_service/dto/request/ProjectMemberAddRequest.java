package com.ntt.task_service.dto.request;

import com.ntt.task_service.domain.ProjectRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProjectMemberAddRequest {
    @NotBlank(message = "FIELD_REQUIRED")
    String userId;

    @NotNull(message = "FIELD_REQUIRED")
    ProjectRole role;
}
