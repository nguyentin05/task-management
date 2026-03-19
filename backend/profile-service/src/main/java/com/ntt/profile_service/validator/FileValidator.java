package com.ntt.profile_service.validator;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.springframework.web.multipart.MultipartFile;

public class FileValidator implements ConstraintValidator<FileConstraint, MultipartFile> {
    private long maxSize;

    @Override
    public void initialize(FileConstraint constraintAnnotation) {
        this.maxSize = constraintAnnotation.maxSize(); // ✅ đọc từ annotation
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null) return true;
        if (file.isEmpty()) return false;
        if (file.getSize() > maxSize) return false; // dùng maxSize từ annotation

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) return false;

        return checkMagicBytes(file, contentType);
    }

    private static final List<String> ALLOWED_CONTENT_TYPES = List.of("image/jpeg", "image/png", "image/webp");

    private static final Map<String, byte[]> MAGIC_BYTES = Map.of(
            "image/jpeg", new byte[] {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF},
            "image/png", new byte[] {(byte) 0x89, 0x50, 0x4E, 0x47},
            "image/webp", new byte[] {0x52, 0x49, 0x46, 0x46});

    private boolean checkMagicBytes(MultipartFile file, String contentType) {
        try {
            byte[] fileBytes = file.getBytes();
            byte[] magic = MAGIC_BYTES.get(contentType);
            if (magic == null) return false;

            for (int i = 0; i < magic.length; i++) {
                if (fileBytes[i] != magic[i]) return false;
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
