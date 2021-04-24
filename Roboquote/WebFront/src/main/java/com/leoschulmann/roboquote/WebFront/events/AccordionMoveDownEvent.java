package com.leoschulmann.roboquote.WebFront.events;

import com.leoschulmann.roboquote.WebFront.ui.bits.SectionButtons;
import com.vaadin.flow.component.ComponentEvent;

public class AccordionMoveDownEvent extends ComponentEvent<SectionButtons> {
    public AccordionMoveDownEvent(SectionButtons sectionButtons) {
        super(sectionButtons, false);
    }
}
