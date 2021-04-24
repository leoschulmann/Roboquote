package com.leoschulmann.roboquote.WebFront.events;

import com.leoschulmann.roboquote.WebFront.ui.NewQuote;
import com.vaadin.flow.component.ComponentEvent;

public class UpdateGridEvent extends ComponentEvent<NewQuote> {
    public UpdateGridEvent(NewQuote newQuote) {
        super(newQuote, false);
    }
}
