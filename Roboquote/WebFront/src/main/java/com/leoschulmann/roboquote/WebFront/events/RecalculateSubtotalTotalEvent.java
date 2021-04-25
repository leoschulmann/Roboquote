package com.leoschulmann.roboquote.WebFront.events;

import com.leoschulmann.roboquote.WebFront.ui.NewQuote;
import com.vaadin.flow.component.ComponentEvent;

public class RecalculateSubtotalTotalEvent extends ComponentEvent<NewQuote> {
    public RecalculateSubtotalTotalEvent(NewQuote newQuote) {
        super(newQuote, false);
    }
}
