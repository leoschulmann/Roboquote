package com.leoschulmann.roboquote.WebFront.events;

import com.leoschulmann.roboquote.WebFront.ui.bits.InventoryLookup;
import com.vaadin.flow.component.ComponentEvent;

public class InventoryLookupRefreshButtonEvent extends ComponentEvent<InventoryLookup> {
    public InventoryLookupRefreshButtonEvent(InventoryLookup inventoryLookup) {
        super(inventoryLookup, false);
    }
}
