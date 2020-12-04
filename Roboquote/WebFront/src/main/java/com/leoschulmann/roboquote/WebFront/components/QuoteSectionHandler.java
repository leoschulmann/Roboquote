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

    void setCurrency(QuoteSection qs, String value, BigDecimal euroValue, BigDecimal dollarValue, BigDecimal yenValue, Double conversionRateValue);

    //todo make some fancy sorting implementations (like more expensive goes first etc)
}
