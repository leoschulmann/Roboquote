package com.leoschulmann.roboquote.WebFront.events;

import com.leoschulmann.roboquote.WebFront.ui.bits.SectionGrid;
import com.vaadin.flow.component.ComponentEvent;

public class GridChangedEvent extends ComponentEvent<SectionGrid> {
    public GridChangedEvent(SectionGrid sectionGrid) {
        super(sectionGrid, false);
    }
}
