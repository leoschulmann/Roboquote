package com.leoschulmann.roboquote.quoteservice.services;

import com.leoschulmann.roboquote.quoteservice.entities.Quote;
import com.leoschulmann.roboquote.quoteservice.exceptions.EmptyQuoteException;
import com.leoschulmann.roboquote.quoteservice.exceptions.NoQuoteFoundException;
import com.leoschulmann.roboquote.quoteservice.repositories.QuoteRepo;
import org.springframework.stereotype.Service;

@Service
public class QuoteServiceImpl implements QuoteService {

    private final QuoteRepo quoteRepo;

    public QuoteServiceImpl(QuoteRepo quoteRepo) {
        this.quoteRepo = quoteRepo;
    }

    @Override
    public Quote saveQuote(Quote quote) {
        if (quote.getSections().size() == 0) throw new EmptyQuoteException();
        return quoteRepo.save(quote);
    }

    @Override
    public Quote getQuote(Integer id) {
        return quoteRepo.findDistinctById(id).orElseThrow(() -> new NoQuoteFoundException(id));
//        return quoteRepo.findById(id).orElseThrow(() -> new NoQuoteFoundException(id)); //produces duplicates :(
    }
}
