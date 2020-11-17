package com.leoschulmann.roboquote.WebFront.ui;

import com.leoschulmann.roboquote.WebFront.components.CurrencyFormatService;
import com.leoschulmann.roboquote.quoteservice.entities.ItemPosition;
import com.leoschulmann.roboquote.quoteservice.entities.QuoteSection;
import com.vaadin.flow.component.grid.Grid;
import org.javamoney.moneta.Money;
import org.javamoney.moneta.function.MonetaryFunctions;

import javax.money.MonetaryAmount;

public class SectionGrid extends Grid<ItemPosition> {
    private final QuoteSection quoteSection;
    private CurrencyFormatService currencyFormatter;


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
                .setHeader("Sum")
                .setFooter("Subtotal:  "+ currencyFormatter.formatMoney(getTotal()));
        setHeightByRows(true);
        getColumnByKey("total").setAutoWidth(true);
        recalculateColumnWidths();
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

    public Money getTotal() {  //todo do smth with mismatching currencies within one table
        return (Money) quoteSection.getPositions().stream()
                .map(ip -> (MonetaryAmount) (ip.getSellingSum()))
                .reduce(MonetaryFunctions.sum())
                .orElseGet(() -> Money.of(0, "EUR"));
    }


    public void refreshTotals() {
        getColumnByKey("total").setFooter("Subtotal: " + currencyFormatter.formatMoney(getTotal()));
    }
}
