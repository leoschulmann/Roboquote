package com.leoschulmann.roboquote.WebFront.ui;

import com.leoschulmann.roboquote.WebFront.components.CurrencyFormatService;
import com.leoschulmann.roboquote.WebFront.components.MoneyMathService;
import com.leoschulmann.roboquote.quoteservice.entities.ItemPosition;
import com.leoschulmann.roboquote.quoteservice.entities.Quote;
import com.leoschulmann.roboquote.quoteservice.entities.QuoteSection;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import javax.money.MonetaryAmount;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class QuoteViewer extends VerticalLayout {
    private CurrencyFormatService currencyService;

    public QuoteViewer(Quote q, CurrencyFormatService currencyService, MoneyMathService moneyMathService) {
        this.currencyService = currencyService;
        add(new Span("id: " + q.getId() + ";\tQuote No: " + q.getNumber() + "-" + q.getVersion() +
                "\t(" + q.getFinalPrice().getCurrency().getCurrencyCode() + ")"));
        add(new Span("Customer: " + q.getCustomer() +
                (q.getCustomerInfo().isBlank() ? "" : " (" + q.getCustomerInfo() + ")")));
        add(new Span("Dealer: " + q.getDealer() +
                (q.getDealerInfo().isBlank() ? "" : " (" + q.getDealerInfo() + ")")));
        add(new Span("Created: " + q.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE) +
                ";\tValid: " + q.getValidThru().format(DateTimeFormatter.ISO_LOCAL_DATE)));
        add(new Span("Payment: " + q.getPaymentTerms() + ";\t" + "Shipping: " + q.getShippingTerms()));
        add(new Span("Installation: " + q.getInstallation() + ";\tWarranty: " + q.getWarranty()));
        add(new Span("EUR: " + q.getEurRate().toString() + ";\tUSD: " + q.getUsdRate().toString() +
                ";\tJPY: " + q.getJpyRate().toString() + ";\t(+" + q.getConversionRate().toString() + "%)"));
        add(new Hr());

        q.getSections().forEach(sect -> {
            add(new Span(sect.getName()));
            add(placeSection(sect));
            Span subtotalSpan = new Span("Subtotal " + sect.getName() + " " + currencyService.formatMoney(sect.getTotal()));
            Span subtotalDiscountedSpan = new Span("Subtotal " + sect.getName() + " (incl. discount " + sect.getDiscount() + "%) "
                    + currencyService.formatMoney(sect.getTotalDiscounted()));

            if (sect.getDiscount() != 0) {
                alignRight(subtotalSpan);
                alignRightAndBolden(subtotalDiscountedSpan);
                add(subtotalSpan, subtotalDiscountedSpan);
            } else {
                alignRightAndBolden(subtotalSpan);
                add(subtotalSpan);
            }

            add(new Hr());
        });

        MonetaryAmount sum = moneyMathService.getSum(q.getSections().stream().
                map(QuoteSection::getTotalDiscounted).collect(Collectors.toList()));
        MonetaryAmount discounted = moneyMathService.calculateDiscountedPrice(sum, q.getDiscount());
        MonetaryAmount vat = moneyMathService.calculateIncludedTax(discounted, q.getVat());

        Span totalSpan = new Span("TOTAL: " + currencyService.formatMoney(sum));
        Span discountedTotalSpan = new Span("TOTAL (" +
                (q.getDiscount() < 0 ? "with premium +" : "with discount -") +
                q.getDiscount() + "%): " + currencyService.formatMoney(discounted));
        Span vatSpan = new Span("(incl. VAT " + q.getVat() + "%: " + currencyService.formatMoney(vat) + ")");

        add(totalSpan);
        if (q.getDiscount() != 0) {
            alignRight(totalSpan);
            alignRight(vatSpan);
            alignRightAndBolden(discountedTotalSpan);
            add(totalSpan, discountedTotalSpan, vatSpan);
        } else {
            alignRightAndBolden(totalSpan);
            alignRight(vatSpan);
            add(totalSpan, vatSpan);
        }
    }

    private Grid<ItemPosition> placeSection(QuoteSection sect) {
        Grid<ItemPosition> grid = new Grid<>(ItemPosition.class);
        grid.removeAllColumns();
        grid.addColumn("itemId");
        grid.addColumn("name");
        grid.addColumn("partNo");
        grid.addColumn("qty");
        grid.addColumn(item -> currencyService.formatMoney(item.getSellingPrice())).setHeader("Price");
        grid.addColumn(item -> currencyService.formatMoney(item.getSellingSum())).setHeader("Sum");
        //todo fix word wrap
        grid.setItems(sect.getPositions());
        grid.setHeightByRows(true);
        grid.getColumns().forEach(col -> col.setSortable(false));
        return grid;
    }

    private void alignRightAndBolden(Span span) {
        span.getElement().getStyle().set("margin-left", "auto").set("font-weight", "bold");
    }

    private void alignRight(Span span) {
        span.getElement().getStyle().set("margin-left", "auto");
    }
}
