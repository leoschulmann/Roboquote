package com.leoschulmann.roboquote.WebFront.ui;

import com.leoschulmann.roboquote.WebFront.components.CurrencyFormatService;
import com.leoschulmann.roboquote.WebFront.components.MoneyMathService;
import com.leoschulmann.roboquote.WebFront.components.StringFormattingService;
import com.leoschulmann.roboquote.quoteservice.entities.ItemPosition;
import com.leoschulmann.roboquote.quoteservice.entities.Quote;
import com.leoschulmann.roboquote.quoteservice.entities.QuoteSection;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import javax.money.MonetaryAmount;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.vaadin.flow.component.grid.GridVariant.*;

public class QuoteViewer extends VerticalLayout {
    private CurrencyFormatService currencyService;

    public QuoteViewer(Quote q, CurrencyFormatService currencyService,
                       MoneyMathService moneyMathService,
                       StringFormattingService stringFormatter) {
        this.currencyService = currencyService;
        add(new Span("id: " + q.getId() + ";\tQuote No: " + q.getNumber() + "-" + q.getVersion() +
                "\t(" + q.getFinalPrice().getCurrency().getCurrencyCode() + ") " + (q.getCancelled() ? "CANCELLED" : "")));
        add(new Span("Customer: " + q.getCustomer() +
                (q.getCustomerInfo().isBlank() ? "" : " (" + q.getCustomerInfo() + ")")));
        add(new Span("Dealer: " + q.getDealer() +
                (q.getDealerInfo().isBlank() ? "" : " (" + q.getDealerInfo() + ")")));
        add(new Span("Created: " + q.getCreatedTimestamp().format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm")) +
                ";\tValid: " + q.getValidThru().format(DateTimeFormatter.ISO_LOCAL_DATE)));
        add(new Span("Payment: " + q.getPaymentTerms() + ";\t" + "Shipping: " + q.getShippingTerms()));
        add(new Span("Installation: " + q.getInstallation() + ";\tWarranty: " + q.getWarranty()));
        add(new Span("Comment: " + Objects.requireNonNullElse(q.getComment(), "[]")));
        add(new Span("EUR: " + q.getEurRate().toString() + ";\tUSD: " + q.getUsdRate().toString() +
                ";\tJPY: " + q.getJpyRate().toString() + ";\t(+" + q.getConversionRate().toString() + "%)"));
        add(new Hr());

        q.getSections().forEach(sect -> {
            add(new Span(sect.getName()));
            add(placeSection(sect));
            Span subtotalSpan = new Span(stringFormatter.getSubtotal(sect.getName(), sect.getTotal()));
            Span subtotalDiscountedSpan = new Span(stringFormatter.getSubtotalDisc(sect.getName(), sect.getTotal(), sect.getDiscount()));

            if (sect.getDiscount() != 0) {
                alignRightAndStrikethrough(subtotalSpan);
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

        Span totalSpan = new Span(stringFormatter.getCombined(sum));
        Span discountedTotalSpan = new Span(stringFormatter.getCombinedWithDiscountOrMarkup(sum, q.getDiscount()));
        Span vatSpan = new Span(stringFormatter.getVat(sum, q.getDiscount(), q.getVat()));

        add(totalSpan);
        if (q.getDiscount() != 0) {
            alignRightAndStrikethrough(totalSpan);
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
        grid.addThemeVariants(LUMO_ROW_STRIPES, LUMO_WRAP_CELL_CONTENT, LUMO_COLUMN_BORDERS);
        grid.removeAllColumns();

        grid.addColumn(ItemPosition::getItemId).setHeader("Id").setSortable(false).setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(ItemPosition::getName).setHeader("Name").setSortable(false);
        grid.addColumn(ItemPosition::getPartNo).setHeader("Part No").setSortable(false).setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(ItemPosition::getQty).setHeader("Qty").setSortable(false).setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(ip -> currencyService.formatMoney(ip.getSellingPrice())).setHeader("Price").setSortable(false).setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(ip -> currencyService.formatMoney(ip.getSellingSum())).setHeader("Sum").setSortable(false).setAutoWidth(true).setFlexGrow(0);

        grid.setItems(sect.getPositions());
        grid.setHeightByRows(true);
        return grid;
    }

    private void alignRightAndBolden(Span span) {
        span.getElement().getStyle().set("margin-left", "auto").set("font-weight", "bold");
    }

    private void alignRightAndStrikethrough(Span span) {
        span.getElement().getStyle().set("margin-left", "auto").set("text-decoration", "line-through");
    }

    private void alignRight(Span span) {
        span.getElement().getStyle().set("margin-left", "auto");
    }
}
