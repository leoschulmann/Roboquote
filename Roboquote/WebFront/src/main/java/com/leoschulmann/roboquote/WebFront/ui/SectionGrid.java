package com.leoschulmann.roboquote.WebFront.ui;

import com.leoschulmann.roboquote.WebFront.components.CurrencyFormatService;
import com.leoschulmann.roboquote.WebFront.components.QuoteSectionHandler;
import com.leoschulmann.roboquote.quoteservice.entities.ItemPosition;
import com.leoschulmann.roboquote.quoteservice.entities.QuoteSection;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.IntegerField;

public class SectionGrid extends Grid<ItemPosition> {
    private final QuoteSection quoteSection;
    static private CurrencyFormatService currencyFormatter;
    private Footer footer;
    QuoteSectionHandler sectionHandler;
    Compose composePage;

    public SectionGrid(String name, CurrencyFormatService currencyFormatter, QuoteSectionHandler sectionHandler, Compose compose) {
        super(ItemPosition.class);
        this.currencyFormatter = currencyFormatter;
        this.sectionHandler = sectionHandler;
        composePage = compose;
        this.quoteSection = new QuoteSection(name);
        setItems(quoteSection.getPositions());

        removeAllColumns();
        addColumn("name").setHeader("Item name").setAutoWidth(true);
        addComponentColumn(this::getQuantityField).setKey("qty").setHeader("Quantity");
        addColumn("partNo").setHeader("Part No");
        addColumn(ip -> currencyFormatter.formatMoney(ip.getSellingPrice())).setHeader("Price").setKey("price");
        addColumn(ip -> currencyFormatter.formatMoney(ip.getSellingSum()))
                .setKey("total")
                .setHeader("Sum");
//                .setFooter("Subtotal:  " + currencyFormatter.formatMoney(quoteSection.getTotal()));
        addComponentColumn(this::createDeleteButton).setKey("delete");
        setHeightByRows(true);
        getColumnByKey("name").setAutoWidth(true);
        getColumnByKey("total").setAutoWidth(true);
        getColumnByKey("price").setAutoWidth(true);
        getColumnByKey("qty").setAutoWidth(true);
        getColumnByKey("partNo").setAutoWidth(true);
        getColumnByKey("delete").setAutoWidth(true);
        recalculateColumnWidths();
        footer = new Footer(this, currencyFormatter);
    }

    private Component getQuantityField(ItemPosition itemPosition) {
        IntegerField field = new IntegerField();
        field.setMax(99);
        field.setMin(1);
        field.setHasControls(true);
        field.setValue(itemPosition.getQty());
        field.addValueChangeListener(event -> {
            sectionHandler.setQty(quoteSection, itemPosition, field.getValue());
            renderItems();
            refreshSubtotals();
            composePage.refreshTotal();
        });
        return field;
    }

    private Component createDeleteButton(ItemPosition itemPosition) {
        Button b = new Button(VaadinIcon.CLOSE_SMALL.create());

        b.addClickListener(c -> {
            sectionHandler.deletePosition(quoteSection, itemPosition);
            renderItems();
            refreshSubtotals();
            composePage.refreshTotal();
        });

        return b;
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

    public void refreshSubtotals() {
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
