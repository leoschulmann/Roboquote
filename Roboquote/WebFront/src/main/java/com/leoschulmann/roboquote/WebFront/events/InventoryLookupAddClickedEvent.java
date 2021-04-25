package com.leoschulmann.roboquote.WebFront.events;

import com.leoschulmann.roboquote.WebFront.ui.bits.InventoryLookup;
import com.leoschulmann.roboquote.WebFront.ui.bits.SectionGrid;
import com.leoschulmann.roboquote.itemservice.entities.Item;
import com.vaadin.flow.component.ComponentEvent;
import lombok.Getter;

@Getter
public class InventoryLookupAddClickedEvent extends ComponentEvent<InventoryLookup> {
    private final Item item;
    private final SectionGrid grid;

    public InventoryLookupAddClickedEvent(InventoryLookup inventoryLookup, Item item, SectionGrid grid) {
        super(inventoryLookup, false);
        this.item = item;
        this.grid = grid;
    }
}
