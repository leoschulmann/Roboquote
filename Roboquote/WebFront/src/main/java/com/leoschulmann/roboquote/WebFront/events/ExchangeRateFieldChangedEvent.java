package com.leoschulmann.roboquote.WebFront.events;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.textfield.NumberField;

public class ExchangeRateFieldChangedEvent extends ComponentEvent<NumberField> {
    public ExchangeRateFieldChangedEvent(NumberField conversionRate) {
        super(conversionRate, false);
    }
}
