package com.leoschulmann.roboquote.WebFront.events;

import com.leoschulmann.roboquote.WebFront.ui.Compose;
import com.leoschulmann.roboquote.quoteservice.entities.Quote;
import com.leoschulmann.roboquote.quoteservice.entities.QuoteSection;
import com.vaadin.flow.component.ComponentEvent;
import lombok.Getter;

@Getter
public class ComposeSectionMovedEvent extends ComponentEvent<Compose> {
    private int fromIndex;
    private int toIndex;
    private Quote quote;
    private QuoteSection quoteSection;

    public ComposeSectionMovedEvent(Compose compose, int fromIndex, int toIndex, Quote quote, QuoteSection quoteSection) {
        super(compose, false);
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
        this.quote = quote;
        this.quoteSection = quoteSection;
    }
}
