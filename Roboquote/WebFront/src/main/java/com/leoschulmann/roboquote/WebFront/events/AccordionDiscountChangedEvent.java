package com.leoschulmann.roboquote.WebFront.events;

import com.leoschulmann.roboquote.WebFront.ui.bits.SectionButtons;
import com.vaadin.flow.component.ComponentEvent;

public class AccordionDiscountChangedEvent extends ComponentEvent<SectionButtons> {
    public AccordionDiscountChangedEvent(SectionButtons sectionButtons) {
        super(sectionButtons, false);
    }
}
