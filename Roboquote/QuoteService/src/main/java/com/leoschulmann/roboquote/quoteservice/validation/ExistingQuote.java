package com.leoschulmann.roboquote.quoteservice.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ExistingQuoteValidator.class)
public @interface ExistingQuote {
    String message() default "invalid quote id";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
