package com.leoschulmann.roboquote.WebFront.events;

import com.leoschulmann.roboquote.WebFront.ui.NewQuote;
import com.vaadin.flow.component.ComponentEvent;
import lombok.Getter;

@Getter
public class QuotePersistedEvent extends ComponentEvent<NewQuote> {

    private final String name;
    private final byte[] bytes;

    public QuotePersistedEvent(NewQuote source, String name, byte[] bytes) {
        super(source, false);
        this.name = name;
        this.bytes = bytes;
    }
}
