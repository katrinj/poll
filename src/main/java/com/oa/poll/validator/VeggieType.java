package com.oa.poll.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = VeggieTypeValidator.class)
public @interface VeggieType {
    String message() default "Invalid veggie key found in the list";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
