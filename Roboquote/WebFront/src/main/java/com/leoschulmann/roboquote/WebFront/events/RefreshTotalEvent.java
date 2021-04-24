package com.leoschulmann.roboquote.WebFront.events;

import com.leoschulmann.roboquote.WebFront.ui.NewQuote;
import com.vaadin.flow.component.ComponentEvent;

public class RefreshTotalEvent extends ComponentEvent<NewQuote> {
    public RefreshTotalEvent(NewQuote newQuote) {
        super(newQuote, false);
    }
}
