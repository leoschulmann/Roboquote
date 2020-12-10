package com.leoschulmann.roboquote.WebFront.ui;

import com.leoschulmann.roboquote.WebFront.components.CurrencyFormatService;
import com.leoschulmann.roboquote.quoteservice.entities.ItemPosition;
import com.leoschulmann.roboquote.quoteservice.entities.Quote;
import com.leoschulmann.roboquote.quoteservice.entities.QuoteSection;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.javamoney.moneta.Money;
import org.javamoney.moneta.function.MonetaryFunctions;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

public class QuoteViewer extends VerticalLayout {
    private CurrencyFormatService currencyService;

    public QuoteViewer(Quote q, CurrencyFormatService currencyService) {
        this.currencyService = currencyService;
        add(new Span("id: " + q.getId() + ";\tQuote #" + q.getNumber() + "-" + q.getVersion() +
                "\t(" + q.getFinalPrice().getCurrency().getCurrencyCode() + ")"));
        add(new Span("Customer: " + q.getCustomer() +
                (q.getCustomerInfo().isBlank() ? "" : " (" + q.getCustomerInfo() + ")")));
        add(new Span("Dealer: " + q.getDealer() +
                (q.getDealerInfo().isBlank() ? "" : " (" + q.getDealerInfo() + ")")));
        add(new Span("Created: " + q.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE) +
                ";\tValid: " + q.getValidThru().format(DateTimeFormatter.ISO_LOCAL_DATE)));
        add(new Span("Payment: " + q.getPaymentTerms() + ";\t" + "Shipping: " + q.getShippingTerms()));
        add(new Span("Warranty: " + q.getWarranty()));
        add(new Span("EUR: " + q.getEurRate().toString() + ";\tUSD: " + q.getUsdRate().toString() +
                "\tJPY: " + q.getJpyRate().toString() + ";\t(+" + q.getConversionRate().toString() + "%)"));

        q.getSections().forEach(sect -> {
            add(new Span(sect.getName()));
            add(placeSection(sect));
            add(new Span("Subtotal " + sect.getName() + " " + currencyService.formatMoney(sect.getTotal())));
            if (sect.getDiscount() != null && sect.getDiscount() != 0) {
                add(new Span("Subtotal " + sect.getName() + " (incl. discount " + sect.getDiscount() + "%) "
                        + currencyService.formatMoney(sect.getTotalDiscounted())));
            }
        });

        MonetaryAmount ma = q.getSections().stream()
                .map(QuoteSection::getTotalDiscounted)
                .reduce(MonetaryFunctions.sum())
                .orElseGet(() -> Money.of(BigDecimal.ZERO, "EUR"));

        MonetaryAmount vat = ma.multiply(q.getVat() / 100.)
                .divide((q.getVat() + 100) / 100.);


        if (!ma.isEqualTo(q.getFinalPrice())) {
            Icon icon = VaadinIcon.WARNING.create();
            icon.setSize("20px");
            icon.setColor("Red");
            add(icon);
        }
        add(new Span("TOTAL " + currencyService.formatMoney(ma)));
        add(new Span("(incl. VAT " + q.getVat() + "%)" + currencyService.formatMoney(vat)));
    }

    private Grid<ItemPosition> placeSection(QuoteSection sect) {
        Grid<ItemPosition> grid = new Grid<>(ItemPosition.class);
        grid.removeAllColumns();
        grid.addColumns("itemId", "name", "partNo", "qty");
        grid.addColumn(item -> currencyService.formatMoney(item.getSellingPrice())).setHeader("Price");
        grid.addColumn(item -> currencyService.formatMoney(item.getSellingSum())).setHeader("Sum");

        grid.setItems(sect.getPositions());
        grid.setHeightByRows(true);
        grid.getColumns().forEach(col -> col.setSortable(false));
        return grid;
    }
}
