package com.ntt.authentication.dto.request;

import java.util.Set;

import jakarta.validation.constraints.NotEmpty;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleUpdateRequest {
    @NotEmpty(message = "FIELD_REQUIRED")
    Set<String> roles;
}
