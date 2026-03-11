package com.ntt.profile_service.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumberConstraint, String> {
    private static final String PHONE_REGEX = "^(0[35789]\\d{8}|\\+84[35789]\\d{8})$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return true;
        }

        return value.matches(PHONE_REGEX);
    }
}
