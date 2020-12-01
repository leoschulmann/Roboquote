package com.leoschulmann.roboquote.WebFront.events;

import com.leoschulmann.roboquote.WebFront.ui.InventoryForm;
import com.vaadin.flow.component.ComponentEvent;

public class InventoryFormCloseEvent extends ComponentEvent<InventoryForm> {

    public InventoryFormCloseEvent(InventoryForm source, boolean fromClient) {
        super(source, fromClient);
    }
}
