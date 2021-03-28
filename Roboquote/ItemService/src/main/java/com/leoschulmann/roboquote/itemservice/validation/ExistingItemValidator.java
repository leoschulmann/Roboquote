package com.leoschulmann.roboquote.itemservice.validation;

import com.leoschulmann.roboquote.itemservice.repositories.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ExistingItemValidator implements ConstraintValidator<ExistingItem, Integer> {
    @Autowired
    ItemRepository itemRepository;

    @Override
    public boolean isValid(Integer id, ConstraintValidatorContext context) {
        return itemRepository.existsById(id);
    }
}
