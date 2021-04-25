package com.leoschulmann.roboquote.WebFront.events;

import com.leoschulmann.roboquote.WebFront.ui.bits.RatesPanel;
import com.vaadin.flow.component.ComponentEvent;

public class RatesPanelUpdateClickedEvent extends ComponentEvent<RatesPanel> {
    public RatesPanelUpdateClickedEvent(RatesPanel ratesPanel) {
        super(ratesPanel, false);
    }
}
