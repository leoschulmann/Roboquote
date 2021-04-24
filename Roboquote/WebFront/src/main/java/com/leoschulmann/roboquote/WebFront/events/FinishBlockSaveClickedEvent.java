package com.leoschulmann.roboquote.WebFront.events;

import com.leoschulmann.roboquote.WebFront.ui.bits.FinishBlock;
import com.vaadin.flow.component.ComponentEvent;

public class FinishBlockSaveClickedEvent extends ComponentEvent<FinishBlock> {
    public FinishBlockSaveClickedEvent(FinishBlock finishBlock) {
        super(finishBlock, false);
    }
}
