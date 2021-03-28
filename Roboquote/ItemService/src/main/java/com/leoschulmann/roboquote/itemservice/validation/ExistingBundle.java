package com.leoschulmann.roboquote.itemservice.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ExistingBundleValidator.class)
public @interface ExistingBundle {
    String message() default "invalid bundle id";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
