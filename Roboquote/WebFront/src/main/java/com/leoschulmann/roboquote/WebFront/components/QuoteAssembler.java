package com.leoschulmann.roboquote.WebFront.components;

import com.leoschulmann.roboquote.quoteservice.entities.Quote;

public interface QuoteAssembler {
    int postNew(Quote quote);
}
