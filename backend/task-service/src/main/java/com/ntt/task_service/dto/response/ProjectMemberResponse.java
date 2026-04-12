package com.ntt.task_service.dto.response;

import com.ntt.task_service.domain.ProjectRole;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProjectMemberResponse {
    String userId;
    String email;
    ProjectRole role;
}
