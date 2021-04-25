package com.leoschulmann.roboquote.WebFront.events;

import com.leoschulmann.roboquote.WebFront.ui.bits.FinishBlock;
import com.vaadin.flow.component.ComponentEvent;

public class CurrencyChangedEvent extends ComponentEvent<FinishBlock> {
    public CurrencyChangedEvent(FinishBlock finishBlock) {
        super(finishBlock, false);
    }
}
