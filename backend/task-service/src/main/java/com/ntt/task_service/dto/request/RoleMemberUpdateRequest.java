package com.ntt.task_service.dto.request;

import com.ntt.task_service.domain.ProjectRole;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleMemberUpdateRequest {
    @NotBlank(message = "FIELD_REQUIRED")
    ProjectRole role;
}
