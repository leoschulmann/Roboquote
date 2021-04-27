package com.leoschulmann.roboquote.WebFront.events;

import com.leoschulmann.roboquote.WebFront.ui.bits.InventoryLookup;
import com.vaadin.flow.component.ComponentEvent;

public class RefreshButtonEvent extends ComponentEvent<InventoryLookup> {
    public RefreshButtonEvent(InventoryLookup inventoryLookup) {
        super(inventoryLookup, false);
    }
}
