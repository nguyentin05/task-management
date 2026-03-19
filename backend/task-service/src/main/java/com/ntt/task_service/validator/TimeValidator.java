package com.ntt.task_service.validator;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanWrapperImpl;

public class TimeValidator implements ConstraintValidator<TimeConstraint, Object> {

    private String startField;
    private String endField;
    private String message;

    @Override
    public void initialize(TimeConstraint constraintAnnotation) {
        this.startField = constraintAnnotation.startField();
        this.endField = constraintAnnotation.endField();
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;

        BeanWrapperImpl wrapper = new BeanWrapperImpl(value);
        Instant startAt = (Instant) wrapper.getPropertyValue(startField);
        Instant endAt = (Instant) wrapper.getPropertyValue(endField);

        if (startAt == null || endAt == null) {
            return true;
        }

        boolean isValid = endAt.isAfter(startAt.plus(1, ChronoUnit.DAYS));

        if (!isValid) {
            context.disableDefaultConstraintViolation();

            context.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode(endField)
                    .addConstraintViolation();
        }

        return isValid;
    }
}
