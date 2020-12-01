package com.leoschulmann.roboquote.WebFront.events;

import com.leoschulmann.roboquote.itemservice.entities.Item;
import com.vaadin.flow.component.Component;

public class InventoryUpdateItemEvent extends AbstractInventoryEvent {
    public InventoryUpdateItemEvent(Component source, Item item) {
        super(source, item);
    }
}
