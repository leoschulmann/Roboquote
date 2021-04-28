package com.leoschulmann.roboquote.WebFront.events;

import com.leoschulmann.roboquote.WebFront.ui.bits.SectionGrid;
import com.vaadin.flow.component.ComponentEvent;

public class OverridePriceClicked extends ComponentEvent<SectionGrid> {
    public OverridePriceClicked(SectionGrid grid) {
        super(grid, false);
    }
}
