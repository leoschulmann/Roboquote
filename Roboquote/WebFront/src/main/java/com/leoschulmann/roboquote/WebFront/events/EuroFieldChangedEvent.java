package com.leoschulmann.roboquote.WebFront.events;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.textfield.BigDecimalField;

public class EuroFieldChangedEvent extends ComponentEvent<BigDecimalField> {
    public EuroFieldChangedEvent(BigDecimalField euro) {
        super(euro, false);
    }
}
