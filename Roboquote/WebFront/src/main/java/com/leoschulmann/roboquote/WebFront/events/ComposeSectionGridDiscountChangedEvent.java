package com.leoschulmann.roboquote.WebFront.events;

import com.leoschulmann.roboquote.WebFront.ui.Compose;
import com.vaadin.flow.component.ComponentEvent;

public class ComposeSectionGridDiscountChangedEvent extends ComponentEvent<Compose> {
    private Integer value;

    public ComposeSectionGridDiscountChangedEvent(Compose compose, Integer value) {
        super(compose, false);
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
