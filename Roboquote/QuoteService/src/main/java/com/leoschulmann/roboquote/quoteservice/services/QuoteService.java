package com.leoschulmann.roboquote.quoteservice.services;

import com.leoschulmann.roboquote.quoteservice.entities.Quote;

public interface QuoteService {
    Quote saveQuote(Quote q);

    Quote getQuote(Integer id);
}
