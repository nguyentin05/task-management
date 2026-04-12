package com.ntt.comment_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileSearchResponse {
    String userId;
    String firstName;
    String lastName;
    String avatar;
}
