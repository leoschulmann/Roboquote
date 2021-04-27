package com.leoschulmann.roboquote.WebFront.events;

import com.leoschulmann.roboquote.WebFront.ui.NewQuote;
import com.vaadin.flow.component.ComponentEvent;

public class RefreshCachesEvent extends ComponentEvent<NewQuote> {
    public RefreshCachesEvent(NewQuote newQuote) {
        super(newQuote, false);
    }
}
