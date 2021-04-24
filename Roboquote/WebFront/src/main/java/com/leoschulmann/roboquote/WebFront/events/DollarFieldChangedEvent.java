package com.leoschulmann.roboquote.WebFront.events;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.textfield.BigDecimalField;

public class DollarFieldChangedEvent extends ComponentEvent<BigDecimalField> {
    public DollarFieldChangedEvent(BigDecimalField dollar) {
        super(dollar, false);
    }
}
