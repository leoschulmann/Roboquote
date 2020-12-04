package com.leoschulmann.roboquote.WebFront.events;

import com.leoschulmann.roboquote.WebFront.ui.Compose;
import com.leoschulmann.roboquote.quoteservice.entities.ItemPosition;
import com.vaadin.flow.component.ComponentEvent;


public class ComposeSectionGridAddNewItemEvent extends ComponentEvent<Compose> {
    public ComposeSectionGridAddNewItemEvent(Compose source, ItemPosition itemPosition) {
        super(source, false);
    }
}
