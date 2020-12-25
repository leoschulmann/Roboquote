package com.leoschulmann.roboquote.quoteservice.services;

import com.leoschulmann.roboquote.quoteservice.entities.Quote;

import java.util.List;

public interface QuoteService {
    Quote saveQuote(Quote q);

    Quote getQuote(Integer id);

    List<Quote> findAll();

    String getQuoteFullName(int id);
}
