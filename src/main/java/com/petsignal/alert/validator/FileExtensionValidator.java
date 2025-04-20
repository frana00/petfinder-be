package com.petsignal.alert.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class FileExtensionValidator implements ConstraintValidator<ValidFileExtension, List<String>> {

  @Override
  public boolean isValid(List<String> filenames, ConstraintValidatorContext context) {

    if (filenames == null || filenames.isEmpty()) return true;

    return filenames.stream().allMatch(f -> f.matches(".*\\.(jpeg|jpg|png)$"));

  }
}
