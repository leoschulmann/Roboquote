package com.leoschulmann.roboquote.WebFront.events;

import com.leoschulmann.roboquote.WebFront.ui.NewQuote;
import com.vaadin.flow.component.ComponentEvent;

public class RecalculateAndRedrawTotalEvent extends ComponentEvent<NewQuote> {
    public RecalculateAndRedrawTotalEvent(NewQuote newQuote) {
        super(newQuote, false);
    }
}
