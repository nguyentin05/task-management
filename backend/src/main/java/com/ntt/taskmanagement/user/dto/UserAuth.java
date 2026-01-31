package com.ntt.taskmanagement.user.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAuth {
    String email;
    String password;
    String role;
    boolean isActive;
}
