package com.leoschulmann.roboquote.WebFront.events;

import com.leoschulmann.roboquote.WebFront.ui.bits.QuoteViewerWrapper;
import com.leoschulmann.roboquote.quoteservice.entities.Quote;
import com.vaadin.flow.component.ComponentEvent;
import lombok.Getter;

@Getter
public class QuoteViewerTemplateClicked extends ComponentEvent<QuoteViewerWrapper> {
    private final Quote quote;

    public QuoteViewerTemplateClicked(QuoteViewerWrapper quoteViewerWrapper, Quote quote) {
        super(quoteViewerWrapper, false);
        this.quote = quote;
    }
}
