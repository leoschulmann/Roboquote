package com.leoschulmann.roboquote.WebFront.events;

import com.leoschulmann.roboquote.itemservice.entities.Item;
import com.vaadin.flow.component.Component;

public class InventoryDeleteItemEvent extends AbstractInventoryEvent {
    public InventoryDeleteItemEvent(Component source, Item item) {
        super(source, item);
    }
}
