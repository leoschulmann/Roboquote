package com.leoschulmann.roboquote.WebFront.components;

import com.leoschulmann.roboquote.quoteservice.entities.ItemPosition;
import com.leoschulmann.roboquote.quoteservice.entities.QuoteSection;

public interface QuoteSectionHandler {

    void putToSection(QuoteSection section, ItemPosition ip);

    //todo make some fancy sorting implementations (like more expensive goes first etc)
}
