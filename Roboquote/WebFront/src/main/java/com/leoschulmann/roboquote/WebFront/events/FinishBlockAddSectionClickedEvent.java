package com.leoschulmann.roboquote.WebFront.events;

import com.leoschulmann.roboquote.WebFront.ui.bits.FinishBlock;
import com.vaadin.flow.component.ComponentEvent;

public class FinishBlockAddSectionClickedEvent extends ComponentEvent<FinishBlock> {
    public FinishBlockAddSectionClickedEvent(FinishBlock finishBlock) {
        super(finishBlock, false);
    }
}
