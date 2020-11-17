package com.leoschulmann.roboquote.WebFront.components;

import com.leoschulmann.roboquote.WebFront.pojo.QuoteDetails;
import com.leoschulmann.roboquote.quoteservice.entities.QuoteSection;

import java.util.List;

public interface QuoteAssembler {
    void assemble(QuoteDetails details, List<QuoteSection> sections);
}
