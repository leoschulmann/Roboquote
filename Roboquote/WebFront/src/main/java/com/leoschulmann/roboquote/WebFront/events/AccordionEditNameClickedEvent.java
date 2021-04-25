package com.leoschulmann.roboquote.WebFront.events;

import com.leoschulmann.roboquote.WebFront.ui.bits.SectionButtons;
import com.vaadin.flow.component.ComponentEvent;

public class AccordionEditNameClickedEvent extends ComponentEvent<SectionButtons> {
    public AccordionEditNameClickedEvent(SectionButtons sectionButtons) {
        super(sectionButtons, false);
    }
}
