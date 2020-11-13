package com.leoschulmann.roboquote.quoteservice.services;

import com.leoschulmann.roboquote.quoteservice.entities.Quote;
import com.leoschulmann.roboquote.quoteservice.exceptions.EmptyQuoteException;
import com.leoschulmann.roboquote.quoteservice.repositories.QuoteRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class QuoteServiceImpl implements QuoteService {

    @Autowired
    private QuoteRepo quoteRepo;

    @Autowired
    private NameGeneratingService generatingService;

    @Override
    public Quote saveQuote(Quote quote) {
        if (quote.getSections().size() == 0) throw new EmptyQuoteException();
        quote.setCreated(LocalDate.now());
        quote.setNumber(generatingService.generate());
        quote.setValidThru(LocalDate.now().plus(3, ChronoUnit.MONTHS));
        quote.setVersion(1);
        return quoteRepo.save(quote);
    }
}
