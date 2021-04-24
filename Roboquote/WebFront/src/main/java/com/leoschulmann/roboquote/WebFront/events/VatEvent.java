package com.leoschulmann.roboquote.WebFront.events;

import com.leoschulmann.roboquote.WebFront.ui.bits.FinishBlock;
import com.vaadin.flow.component.ComponentEvent;

public class VatEvent extends ComponentEvent<FinishBlock> {
    public VatEvent(FinishBlock finishBlock) {
        super(finishBlock, false);
    }
}
