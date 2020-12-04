package com.leoschulmann.roboquote.WebFront.events;

import com.leoschulmann.roboquote.WebFront.ui.Compose;
import com.vaadin.flow.component.ComponentEvent;


public class ComposeQuoteCurrencyChangedEvent extends ComponentEvent<Compose> {
    private String currency;

    public ComposeQuoteCurrencyChangedEvent(Compose source, String currency) {
        super(source, false);
        this.currency = currency;
    }

    public String getCurrency() {
        return currency;
    }
}
