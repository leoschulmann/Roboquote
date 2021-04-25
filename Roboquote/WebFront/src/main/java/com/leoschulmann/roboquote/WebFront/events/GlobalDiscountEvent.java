package com.leoschulmann.roboquote.WebFront.events;

import com.leoschulmann.roboquote.WebFront.ui.bits.FinishBlock;
import com.vaadin.flow.component.ComponentEvent;

public class GlobalDiscountEvent extends ComponentEvent<FinishBlock> {
    public GlobalDiscountEvent(FinishBlock finishBlock) {
        super(finishBlock, false);
    }
}
