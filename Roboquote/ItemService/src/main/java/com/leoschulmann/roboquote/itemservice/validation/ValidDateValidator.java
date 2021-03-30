package com.leoschulmann.roboquote.itemservice.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ValidDateValidator implements ConstraintValidator<ValidDate, String> {
    private DateTimeFormatter dateTimeFormatter;

    @Override
    public void initialize(ValidDate constraintAnnotation) {
        dateTimeFormatter = DateTimeFormatter.ofPattern(constraintAnnotation.format());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            dateTimeFormatter.parse(value);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
