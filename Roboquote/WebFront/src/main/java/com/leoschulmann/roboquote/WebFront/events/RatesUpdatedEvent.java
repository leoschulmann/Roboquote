package com.leoschulmann.roboquote.WebFront.events;

import com.leoschulmann.roboquote.WebFront.ui.NewQuote;
import com.vaadin.flow.component.ComponentEvent;

public class RatesUpdatedEvent extends ComponentEvent<NewQuote> {
    public RatesUpdatedEvent(NewQuote newQuote) {
        super(newQuote, false);
    }
}
