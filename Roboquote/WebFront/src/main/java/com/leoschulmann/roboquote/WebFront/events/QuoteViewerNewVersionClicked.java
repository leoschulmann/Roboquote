package com.leoschulmann.roboquote.WebFront.events;

import com.leoschulmann.roboquote.WebFront.ui.bits.QuoteViewerWrapper;
import com.leoschulmann.roboquote.quoteservice.entities.Quote;
import com.vaadin.flow.component.ComponentEvent;
import lombok.Getter;

@Getter
public class QuoteViewerNewVersionClicked extends ComponentEvent<QuoteViewerWrapper> {
    private final Quote quote;

    public QuoteViewerNewVersionClicked(QuoteViewerWrapper quoteViewerWrapper, Quote quote) {
        super(quoteViewerWrapper, false);
        this.quote = quote;
    }
}
