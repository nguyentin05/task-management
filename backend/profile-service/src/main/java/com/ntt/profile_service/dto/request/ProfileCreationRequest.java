package com.ntt.profile_service.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileCreationRequest {
    String userId;
    String firstName;
    String lastName;
}
