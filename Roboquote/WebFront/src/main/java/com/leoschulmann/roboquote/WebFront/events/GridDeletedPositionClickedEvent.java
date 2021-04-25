package com.leoschulmann.roboquote.WebFront.events;

import com.leoschulmann.roboquote.WebFront.ui.bits.SectionGrid;
import com.leoschulmann.roboquote.quoteservice.entities.ItemPosition;
import com.vaadin.flow.component.ComponentEvent;
import lombok.Getter;

@Getter
public class GridDeletedPositionClickedEvent extends ComponentEvent<SectionGrid> {
    private ItemPosition itemPosition;

    public GridDeletedPositionClickedEvent(SectionGrid sectionGrid, ItemPosition itemPosition) {
        super(sectionGrid, false);
        this.itemPosition = itemPosition;
    }
}
