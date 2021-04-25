package com.leoschulmann.roboquote.WebFront.events;

import com.leoschulmann.roboquote.WebFront.ui.bits.SectionGrid;
import com.leoschulmann.roboquote.quoteservice.entities.ItemPosition;
import com.vaadin.flow.component.ComponentEvent;
import lombok.Getter;

@Getter
public class GridChangedQtyClickedEvent extends ComponentEvent<SectionGrid> {
    private final int qty;
    private final ItemPosition itemPosition;

    public GridChangedQtyClickedEvent(SectionGrid sectionGrid, int qty, ItemPosition itemPosition) {
        super(sectionGrid, false);
        this.qty = qty;
        this.itemPosition = itemPosition;
    }
}
