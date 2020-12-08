package com.leoschulmann.roboquote.WebFront.components;

import com.leoschulmann.roboquote.quoteservice.entities.Quote;

import java.util.List;

public interface QuoteService {
    List<Quote> findAll();
}
