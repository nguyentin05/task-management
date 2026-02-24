package com.ntt.authentication.dto.request;

import com.ntt.authentication.validator.StrongPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterRequest {
    @NotBlank(message = "FIELD_REQUIRED")
    @Email(message = "EMAIL_INVALID")
    String email;

    @NotBlank(message = "FIELD_REQUIRED")
    @StrongPassword(message = "PASSWORD_WEAK")
    String password;

    @NotBlank(message = "FIELD_REQUIRED")
    String firstName;

    @NotBlank(message = "FIELD_REQUIRED")
    String lastName;

    LocalDate dob;
}
