package com.leoschulmann.roboquote.itemservice.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class AnyValueValidator implements ConstraintValidator<AnyValue, String> {
    private String[] matchingValues;

    @Override
    public void initialize(AnyValue constraintAnnotation) {
        matchingValues = constraintAnnotation.values();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return Arrays.asList(matchingValues).contains(value);
    }
}
