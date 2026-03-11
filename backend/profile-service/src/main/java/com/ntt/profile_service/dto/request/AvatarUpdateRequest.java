package com.ntt.profile_service.dto.request;

import com.ntt.profile_service.validator.FileConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AvatarUpdateRequest {
    @NotNull(message = "FIELD_REQUIRED")
    @FileConstraint(message = "INVALID_FILE")
    MultipartFile avatar;
}
