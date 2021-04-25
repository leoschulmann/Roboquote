package com.leoschulmann.roboquote.WebFront.events;

import com.leoschulmann.roboquote.WebFront.ui.bits.SectionButtons;
import com.vaadin.flow.component.ComponentEvent;

public class AccordionMoveUpEvent extends ComponentEvent<SectionButtons> {
    public AccordionMoveUpEvent(SectionButtons sectionButtons) {
        super(sectionButtons, false);
    }
}
