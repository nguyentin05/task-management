package com.ntt.task_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MemberSearchResponse {
    String userId;
    String email;
    String firstName;
    String lastName;
    String avatar;
    boolean alreadyMember;
}
