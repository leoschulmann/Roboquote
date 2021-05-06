package com.leoschulmann.roboquote.WebFront.events;

import com.leoschulmann.roboquote.WebFront.ui.bits.ItemUsageDialog;
import com.vaadin.flow.component.ComponentEvent;
import lombok.Getter;

@Getter
public class OpenQuoteViewerClicked extends ComponentEvent<ItemUsageDialog> {
    private int id;

    public OpenQuoteViewerClicked(ItemUsageDialog itemUsageDialog, int id) {
        super(itemUsageDialog, false);
        this.id = id;
    }
}
