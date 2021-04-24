package com.leoschulmann.roboquote.WebFront.events;

import com.leoschulmann.roboquote.WebFront.ui.bits.SectionButtons;
import com.vaadin.flow.component.ComponentEvent;

public class AccordionDeleteSectionEvent extends ComponentEvent<SectionButtons> {
    public AccordionDeleteSectionEvent(SectionButtons sectionButtons) {
        super(sectionButtons, false);
    }
}
