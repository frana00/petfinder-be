package com.petsignal.alert.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = GpsCoordinatesValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidGpsCoordinates {
    String message() default "Invalid GPS coordinates";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
