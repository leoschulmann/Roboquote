package com.leoschulmann.roboquote.WebFront.events;

import com.leoschulmann.roboquote.WebFront.ui.NewQuote;
import com.vaadin.flow.component.ComponentEvent;

public class DisableClickableComponents extends ComponentEvent<NewQuote> {
    public DisableClickableComponents(NewQuote newQuote) {
        super(newQuote, false);
    }
}
