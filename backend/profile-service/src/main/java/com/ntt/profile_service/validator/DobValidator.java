package com.ntt.profile_service.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DobValidator implements ConstraintValidator<DobConstraint, LocalDate> {
    private int minAge;

    @Override
    public void initialize(DobConstraint constraintAnnotation) {
        this.minAge = constraintAnnotation.min();
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) return true;

        if (value.isAfter(LocalDate.now())) return false;

        long years = ChronoUnit.YEARS.between(value, LocalDate.now());
        return years >= minAge;
    }
}
