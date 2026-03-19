package com.ntt.task_service.validator;

import java.lang.annotation.*;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = TimeValidator.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TimeConstraint {
    String message();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String startField() default "startAt";

    String endField() default "endAt";
}
