package com.leoschulmann.roboquote.WebFront.ui.bits;

import com.leoschulmann.roboquote.WebFront.components.CurrencyFormatService;
import com.leoschulmann.roboquote.WebFront.events.OpenQuoteViewerClicked;
import com.leoschulmann.roboquote.quoteservice.entities.Quote;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.shared.Registration;
import org.javamoney.moneta.Money;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ItemUsageDialog extends Dialog implements AfterNavigationObserver {
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd MMM yy");

    public ItemUsageDialog(List<Quote> quotes, CurrencyFormatService currencyFormatService) {
        Grid<Quote> grid = new Grid<>(Quote.class);
        grid.setItems(quotes);
        grid.removeAllColumns();
        grid.addColumn(quote -> quote.getCreatedTimestamp().format(DTF))
                .setHeader("Created").setSortable(true).setKey("created").setAutoWidth(true).setFlexGrow(0)
                .setResizable(true).setComparator(Quote::getCreatedTimestamp);

        grid.addColumn(q -> q.getNumber() + "-" + q.getVersion()).setHeader("#").setSortable(true).setKey("serialNum")
                .setAutoWidth(true).setFlexGrow(0).setResizable(true);

        grid.addColumn(quote -> {
            String dealer = quote.getDealer();
            if (dealer == null || dealer.isBlank()) {
                dealer = "direct";
            }
            return quote.getCustomer() + "/" + dealer + "/";
        }).setHeader("Customer/Dealer");

        grid.addColumn(Quote::getComment).setHeader("Comment").setFlexGrow(0).setResizable(true);
        grid.addColumn(q -> currencyFormatService.formatMoney(q.getFinalPrice() == null ?
                Money.of(BigDecimal.ZERO, "EUR") : q.getFinalPrice()))
                .setHeader("Quote Price")
                .setAutoWidth(true).setFlexGrow(0).setResizable(true);

        grid.setClassNameGenerator(quote -> quote.getCancelled() ? "strikethrough" : "");
        add(grid);
        GridSortOrder<Quote> byCreated = new GridSortOrder<>(grid.getColumnByKey("created"), SortDirection.DESCENDING);
        grid.sort(List.of(byCreated));

        grid.addItemClickListener(event -> fireEvent(new OpenQuoteViewerClicked(this, event.getItem().getId())));
    }

    public <T extends ComponentEvent<?>> Registration addListener
            (Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        this.close();
    }
}
