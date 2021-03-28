package com.leoschulmann.roboquote.itemservice.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ExistingItemValidator.class)

public @interface ExistingItem {
    String message() default "invalid item id";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
