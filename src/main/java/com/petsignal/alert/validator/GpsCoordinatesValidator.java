package com.petsignal.alert.validator;

import com.petsignal.alert.dto.AlertRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class GpsCoordinatesValidator implements ConstraintValidator<ValidGpsCoordinates, AlertRequest> {

    @Override
    public void initialize(ValidGpsCoordinates constraintAnnotation) {
    }

    @Override
    public boolean isValid(AlertRequest alertRequest, ConstraintValidatorContext context) {
        Double latitude = alertRequest.getLatitude();
        Double longitude = alertRequest.getLongitude();

        // If both are null, it's valid (no GPS coordinates provided)
        if (latitude == null && longitude == null) {
            return true;
        }

        // If only one is provided, it's invalid
        if (latitude == null || longitude == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Both latitude and longitude must be provided together")
                    .addConstraintViolation();
            return false;
        }

        // Validate latitude range
        if (latitude < -90.0 || latitude > 90.0) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Latitude must be between -90 and 90")
                    .addConstraintViolation();
            return false;
        }

        // Validate longitude range
        if (longitude < -180.0 || longitude > 180.0) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Longitude must be between -180 and 180")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
