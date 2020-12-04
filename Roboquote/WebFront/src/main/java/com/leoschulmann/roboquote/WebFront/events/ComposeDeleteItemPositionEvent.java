package com.leoschulmann.roboquote.WebFront.events;

import com.leoschulmann.roboquote.WebFront.ui.SectionGrid;
import com.leoschulmann.roboquote.quoteservice.entities.ItemPosition;
import com.vaadin.flow.component.ComponentEvent;

public class ComposeDeleteItemPositionEvent extends ComponentEvent<SectionGrid> {
    private ItemPosition itemPosition;
    private SectionGrid grid;

    public ComposeDeleteItemPositionEvent(SectionGrid grid, ItemPosition itemPosition) {
        super(grid, false);
        this.itemPosition = itemPosition;
        this.grid = grid;
    }

    public ItemPosition getItemPosition() {
        return itemPosition;
    }

    public SectionGrid getGrid() {
        return grid;
    }
}
