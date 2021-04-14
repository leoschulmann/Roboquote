package com.leoschulmann.roboquote.itemservice.validation;

import com.leoschulmann.roboquote.itemservice.repositories.BundleRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ExistingBundleValidator implements ConstraintValidator<ExistingBundle, Integer> {

    @Autowired
    BundleRepository bundleRepository;

    @Override
    public boolean isValid(Integer id, ConstraintValidatorContext constraintValidatorContext) {
        return bundleRepository.existsById(id);
    }

}
