package com.leoschulmann.roboquote.WebFront.events;

import com.leoschulmann.roboquote.WebFront.ui.bits.SectionGrid;
import com.leoschulmann.roboquote.quoteservice.entities.ItemPosition;
import com.vaadin.flow.component.ComponentEvent;

public class ComposeItemPositionQuantityEvent extends ComponentEvent<SectionGrid> {
    private SectionGrid grid;
    private ItemPosition itemPosition;
    private int qty;

    public ComposeItemPositionQuantityEvent(SectionGrid source, ItemPosition itemPosition, int qty) {
        super(source, false);
        grid = source;
        this.itemPosition = itemPosition;
        this.qty = qty;
    }

    public SectionGrid getGrid() {
        return grid;
    }

    public ItemPosition getItemPosition() {
        return itemPosition;
    }

    public int getQty() {
        return qty;
    }
}
