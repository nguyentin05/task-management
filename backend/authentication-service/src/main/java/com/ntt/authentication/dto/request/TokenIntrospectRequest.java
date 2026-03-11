package com.ntt.authentication.dto.request;

import jakarta.validation.constraints.NotBlank;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TokenIntrospectRequest {
    @NotBlank(message = "FIELD_REQUIRED")
    String token;
}
