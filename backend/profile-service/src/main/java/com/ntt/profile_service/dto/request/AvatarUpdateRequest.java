package com.ntt.profile_service.dto.request;

import jakarta.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import com.ntt.profile_service.validator.FileConstraint;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AvatarUpdateRequest {
    @NotNull(message = "FIELD_REQUIRED")
    @FileConstraint(message = "FILE_INVALID")
    MultipartFile avatar;
}
