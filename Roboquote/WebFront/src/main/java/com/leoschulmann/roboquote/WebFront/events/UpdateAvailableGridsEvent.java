package com.leoschulmann.roboquote.WebFront.events;

import com.leoschulmann.roboquote.WebFront.ui.NewQuote;
import com.vaadin.flow.component.ComponentEvent;

public class UpdateAvailableGridsEvent extends ComponentEvent<NewQuote> {
    public UpdateAvailableGridsEvent(NewQuote newQuote) {
        super(newQuote, false);
    }
}
