package com.leoschulmann.roboquote.WebFront.events;

import com.leoschulmann.roboquote.WebFront.ui.InventoryForm;
import com.vaadin.flow.component.ComponentEvent;
import lombok.Getter;

@Getter
public class InvetoryUsageClickedEvent extends ComponentEvent<InventoryForm> {
    private final int itemId;

    public InvetoryUsageClickedEvent(InventoryForm inventoryForm, Integer id) {
        super(inventoryForm, false);
        itemId = id;
    }
}
