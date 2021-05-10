package com.leoschulmann.roboquote.WebFront.events;

import com.leoschulmann.roboquote.WebFront.ui.bits.QuoteViewerWrapper;
import com.vaadin.flow.component.ComponentEvent;
import lombok.Getter;

@Getter
public class QuoteViewerCancelClicked extends ComponentEvent<QuoteViewerWrapper> {
    private final int id;
    private final boolean cancelAction;

    public QuoteViewerCancelClicked(QuoteViewerWrapper quoteViewerWrapper, int id, boolean cancelAction) {
        super(quoteViewerWrapper, false);
        this.id = id;
        this.cancelAction = cancelAction;
    }
}
