package com.leoschulmann.roboquote.WebFront.events;

import com.leoschulmann.roboquote.WebFront.ui.NewQuote;
import com.vaadin.flow.component.ComponentEvent;

public class RedrawGridAndSubtotalsEvent extends ComponentEvent<NewQuote> {
    public RedrawGridAndSubtotalsEvent(NewQuote newQuote) {
        super(newQuote, false);
    }
}
