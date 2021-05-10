package com.leoschulmann.roboquote.WebFront.events;

import com.leoschulmann.roboquote.WebFront.ui.bits.QuoteViewerWrapper;
import com.vaadin.flow.component.ComponentEvent;
import lombok.Getter;

@Getter
public class QuoteViewerCommentClicked extends ComponentEvent<QuoteViewerWrapper> {
    private final int id;
    private final String comment;

    public QuoteViewerCommentClicked(QuoteViewerWrapper quoteViewerWrapper, int id, String comment) {
        super(quoteViewerWrapper, false);
        this.id = id;
        this.comment = comment;
    }
}
