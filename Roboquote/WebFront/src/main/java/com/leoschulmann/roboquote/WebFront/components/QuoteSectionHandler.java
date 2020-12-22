package com.leoschulmann.roboquote.WebFront.components;

import com.leoschulmann.roboquote.quoteservice.entities.ItemPosition;
import com.leoschulmann.roboquote.quoteservice.entities.QuoteSection;

import java.math.BigDecimal;

public interface QuoteSectionHandler {

    void putToSection(QuoteSection section, ItemPosition ip);

    void deletePosition(QuoteSection quoteSection, ItemPosition itemPosition);

    void setQty(QuoteSection quoteSection, ItemPosition itemPosition, Integer value);

    void setSectionDiscount(QuoteSection quoteSection, Integer value);

    void setSectionName(QuoteSection quoteSection, String value);

    void updateSubtotalToCurrency(QuoteSection qs, String currency,
                                  BigDecimal euroRate, BigDecimal dollarRate, BigDecimal yenRate, Double conv);

    //todo make some fancy sorting implementations (like more expensive goes first etc)
}
