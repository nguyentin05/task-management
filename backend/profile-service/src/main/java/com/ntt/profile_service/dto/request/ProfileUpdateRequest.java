package com.ntt.profile_service.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Size;

import com.ntt.profile_service.validator.DobConstraint;
import com.ntt.profile_service.validator.PhoneNumberConstraint;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileUpdateRequest {
    @Size(max = 255, message = "FIELD_SIZE_INVALID")
    String firstName;

    @Size(max = 255, message = "FIELD_SIZE_INVALID")
    String lastName;

    @DobConstraint(min = 10, message = "DOB_INVALID")
    LocalDate dob;

    @PhoneNumberConstraint(message = "PHONE_INVALID")
    String phoneNumber;
}
