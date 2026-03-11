package com.ntt.profile_service.dto.request;

import com.ntt.profile_service.validator.DobConstraint;
import com.ntt.profile_service.validator.PhoneNumberConstraint;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

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

    @DobConstraint(message = "DOB_INVALID")
    LocalDate dob;

    @PhoneNumberConstraint(message = "PHONE_INVALID")
    String phoneNumber;
}
