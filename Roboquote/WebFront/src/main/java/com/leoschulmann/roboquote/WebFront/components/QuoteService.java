package com.leoschulmann.roboquote.WebFront.components;

import com.leoschulmann.roboquote.quoteservice.entities.Quote;
import com.leoschulmann.roboquote.quoteservice.entities.QuoteSection;

import java.util.List;

public interface QuoteService {
    List<Quote> findAll();

    Quote createNewVersion(Quote quote);

    Quote createNewFromTemplate(Quote quote);

    void addSections(Quote quote, QuoteSection qs);

    int postNew(Quote quote);

    String getFullName(int id);
}
