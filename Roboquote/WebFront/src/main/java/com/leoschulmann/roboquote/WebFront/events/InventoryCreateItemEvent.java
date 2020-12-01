package com.leoschulmann.roboquote.WebFront.events;

import com.leoschulmann.roboquote.itemservice.entities.Item;
import com.vaadin.flow.component.Component;

public class InventoryCreateItemEvent extends AbstractInventoryEvent{
    public InventoryCreateItemEvent(Component source, Item item) {
        super(source, item);
    }
}
