package com.leoschulmann.roboquote.WebFront.ui;

import com.leoschulmann.roboquote.WebFront.components.CurrencyFormatService;
import com.leoschulmann.roboquote.WebFront.components.QuoteService;
import com.leoschulmann.roboquote.quoteservice.entities.Quote;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.router.Route;
import org.javamoney.moneta.Money;
import org.vaadin.klaudeta.PaginatedGrid;

import java.math.BigDecimal;
import java.util.List;

@Route(value = "quotes", layout = MainLayout.class)
public class QuotesView extends VerticalLayout {
    PaginatedGrid<Quote> grid;

    private final QuoteService quoteService;
    private final CurrencyFormatService currencyFormatService;

    public QuotesView(QuoteService quoteService, CurrencyFormatService currencyFormatService) {
        this.quoteService = quoteService;
        this.currencyFormatService = currencyFormatService;
        createGrid();
        updateGrid();
        add(grid);
    }

    private void createGrid() {
        grid = new PaginatedGrid<>(Quote.class);
        setSizeFull();
        grid.removeAllColumns();
        grid.addColumns("number", "customer", "dealer", "created");
        grid.addColumn(q -> {

            Money m = q.getFinalPrice();

            return currencyFormatService.formatMoney(m == null ? Money.of(BigDecimal.ZERO, "EUR") : m);
        }).setHeader("Quote Price");

        grid.setPageSize(15);
        grid.setPaginatorSize(5);
        GridSortOrder<Quote> gridSortOrder = new GridSortOrder<>(grid.getColumnByKey("created"), SortDirection.DESCENDING);
        GridSortOrder<Quote> gridSortOrder1 = new GridSortOrder<>(grid.getColumnByKey("number"), SortDirection.DESCENDING);
        grid.sort(List.of(gridSortOrder, gridSortOrder1));
    }

    private void updateGrid() {
        grid.setItems(quoteService.findAll());
    }
}
