package com.leoschulmann.roboquote.WebFront.events;

import com.leoschulmann.roboquote.itemservice.entities.Item;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;

abstract class AbstractInventoryEvent extends ComponentEvent<Component> {
    private Item eventItem;

    public AbstractInventoryEvent(Component source, Item item) {
        super(source, false);
        this.eventItem = item;
    }

    public Item getEventItem() {
        return eventItem;
    }
}
