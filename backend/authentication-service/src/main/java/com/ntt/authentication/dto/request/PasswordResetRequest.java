package com.ntt.authentication.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.ntt.authentication.validator.PasswordConstraint;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PasswordResetRequest {
    @NotBlank(message = "FIELD_REQUIRED")
    @PasswordConstraint(message = "PASSWORD_WEAK")
    @Size(max = 255, message = "FIELD_SIZE_INVALID")
    String newPassword;
}
