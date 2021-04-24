package com.leoschulmann.roboquote.WebFront.events;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.textfield.BigDecimalField;

public class YenFieldChangedEvent extends ComponentEvent<BigDecimalField> {
    public YenFieldChangedEvent(BigDecimalField yen) {
        super(yen, false);
    }
}
