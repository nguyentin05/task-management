package com.ntt.taskmanagement.auth.dto.request;

import com.ntt.taskmanagement.auth.internal.validator.StrongPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    @NotBlank(message = "FIELD_REQUIRED")
    @Email(message = "EMAIL_INVALID")
    private String email;

    @NotBlank(message = "FIELD_REQUIRED")
    @StrongPassword(message = "PASSWORD_WEAK")
    private String password;

    @NotBlank(message = "FIELD_REQUIRED")
    private String firstName;

    @NotBlank(message = "FIELD_REQUIRED")
    private String lastName;
}
