package com.leoschulmann.roboquote.WebFront.events;

import com.leoschulmann.roboquote.WebFront.ui.Compose;
import com.vaadin.flow.component.ComponentEvent;


public class UniversalSectionChangedEvent extends ComponentEvent<Compose> {
    //todo add payload

    public UniversalSectionChangedEvent(Compose source) {
        super(source, false);
    }
}
