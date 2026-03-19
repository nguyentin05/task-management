package com.ntt.profile_service.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {PhoneNumberValidator.class})
public @interface PhoneNumberConstraint {
    String message();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
