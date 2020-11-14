package com.leoschulmann.roboquote.WebFront.ui;

import com.leoschulmann.roboquote.quoteservice.entities.ItemPosition;
import com.leoschulmann.roboquote.quoteservice.entities.QuoteSection;
import com.vaadin.flow.component.grid.Grid;

public class SectionGrid extends Grid<ItemPosition> {
    private final QuoteSection quoteSection;

    public SectionGrid(String name) {
        super(ItemPosition.class);
        this.quoteSection = new QuoteSection(name);
        setItems(quoteSection.getPositions());
    }

    public void renderItems() {
        setItems(quoteSection.getPositions());
    }

    public QuoteSection getQuoteSection() {
        return quoteSection;
    }

    @Override
    public String toString() {
        return quoteSection.getName();
    }
}
