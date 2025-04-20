package com.petsignal.alert.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = FileExtensionValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFileExtension {
  String message() default "Filenames must end with .jpeg, .jpg, or .png";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
