package com.leoschulmann.roboquote.quoteservice.validation;

import com.leoschulmann.roboquote.quoteservice.repositories.QuoteRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Service
@RequiredArgsConstructor
public class ExistingQuoteValidator implements ConstraintValidator<ExistingQuote, Integer> {
    private final QuoteRepo quoteRepo;

    @Override
    public boolean isValid(Integer quoteId, ConstraintValidatorContext context) {
        return quoteRepo.existsById(quoteId);
    }
}
