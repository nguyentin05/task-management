package com.ntt.profile_service.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = FileValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FileConstraint {
    String message();

    long maxSize() default 5 * 1024 * 1024;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
