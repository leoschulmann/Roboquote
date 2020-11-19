package com.leoschulmann.roboquote.WebFront.ui;

import com.leoschulmann.roboquote.WebFront.components.CurrencyFormatService;
import com.leoschulmann.roboquote.quoteservice.entities.ItemPosition;
import com.leoschulmann.roboquote.quoteservice.entities.QuoteSection;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;

public class SectionGrid extends Grid<ItemPosition> {
    private final QuoteSection quoteSection;
    static private CurrencyFormatService currencyFormatter;
    private Footer footer;


    public SectionGrid(String name, CurrencyFormatService currencyFormatter) {
        super(ItemPosition.class);
        this.currencyFormatter = currencyFormatter;
        this.quoteSection = new QuoteSection(name);
        setItems(quoteSection.getPositions());

        removeAllColumns();
        addColumn("name").setHeader("Item name").setAutoWidth(true);
        addColumn("qty").setHeader("Quantity");
        addColumn("partNo").setHeader("Part No");
        addColumn(ip -> currencyFormatter.formatMoney(ip.getSellingPrice())).setHeader("Price");
        addColumn(ip -> currencyFormatter.formatMoney(ip.getSellingSum()))
                .setKey("total")
                .setHeader("Sum");
//                .setFooter("Subtotal:  " + currencyFormatter.formatMoney(quoteSection.getTotal()));
        setHeightByRows(true);
        getColumnByKey("total").setAutoWidth(true);
        recalculateColumnWidths();
        footer = new Footer(this, currencyFormatter);
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

    public String getName() {
        return quoteSection.getName();
    }

    public void setName(String name) {
        quoteSection.setName(name);
    }

    public Footer getFooter() {
        return footer;
    }

    public void refreshTotals() {
//        getColumnByKey("total").setFooter("Subtotal: " + currencyFormatter.formatMoney(quoteSection.getTotal()));
        footer.update();
    }
}

class Footer extends Div {
    private Paragraph total;
    private Paragraph totalDiscounted;
    private CurrencyFormatService currencyFormatter;

    public Footer(SectionGrid grid, CurrencyFormatService currencyFormatter) {
        this.currencyFormatter = currencyFormatter;
        this.grid = grid;
        total = new Paragraph();
        totalDiscounted = new Paragraph();
        add(total, totalDiscounted);
    }

    private SectionGrid grid;

    public void update() {
        total.setText("Subotal " + grid.getName() + " " + currencyFormatter.formatMoney(grid.getQuoteSection().getTotal()));
        totalDiscounted.setText(
                grid.getQuoteSection().getDiscount() <= 0 ? "" :
                        "Subtotal " + grid.getName() + " (disc. " + grid.getQuoteSection().getDiscount() + "%) " +
                                currencyFormatter.formatMoney(grid.getQuoteSection().getTotalDiscounted()));
    }
}
