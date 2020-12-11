package com.leoschulmann.roboquote.WebFront.ui;

import com.leoschulmann.roboquote.WebFront.components.CurrencyFormatService;
import com.leoschulmann.roboquote.WebFront.components.DownloadService;
import com.leoschulmann.roboquote.WebFront.components.MoneyMathService;
import com.leoschulmann.roboquote.WebFront.components.QuoteService;
import com.leoschulmann.roboquote.quoteservice.entities.Quote;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.javamoney.moneta.Money;
import org.vaadin.klaudeta.PaginatedGrid;
import org.vaadin.olli.FileDownloadWrapper;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.List;

@Route(value = "quotes", layout = MainLayout.class)
public class QuotesView extends VerticalLayout {
    private PaginatedGrid<Quote> grid;

    private final QuoteService quoteService;
    private final CurrencyFormatService currencyFormatService;
    private final DownloadService downloadService;
    private MoneyMathService moneyMathService;

    public QuotesView(QuoteService quoteService, CurrencyFormatService currencyFormatService,
                      DownloadService downloadService, MoneyMathService moneyMathService) {
        this.quoteService = quoteService;
        this.currencyFormatService = currencyFormatService;
        this.downloadService = downloadService;
        this.moneyMathService = moneyMathService;
        createGrid();
        updateGrid();
        add(grid);
        grid.asSingleSelect().addValueChangeListener(event -> editQuote(event.getValue()));
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

    private void editQuote(Quote value) {
        try {
            Button downloadXlsxBtn = new Button("Download " + value.getNumber() + "-" + value.getVersion() + ".xlsx");
            FileDownloadWrapper downloadXlsxWrapper = new FileDownloadWrapper(
                    new StreamResource(value.getNumber() + "-" + value.getVersion() + ".xlsx",
                            () -> new ByteArrayInputStream(downloadService.downloadXlsx(value.getId()))));
            downloadXlsxWrapper.wrapComponent(downloadXlsxBtn);
            Button closeBtn = new Button("Close");
            Button asTemplateBtn = new Button("Use as a template");
            Button appendNewVersionBtn = new Button("Create new version of " + value.getNumber() + "-" + (value.getVersion()));
            downloadXlsxBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
            closeBtn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
            asTemplateBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_CONTRAST);
            appendNewVersionBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

            Dialog dialog = new Dialog(new VerticalLayout(new QuoteViewer(value, currencyFormatService, moneyMathService),
                    new HorizontalLayout(asTemplateBtn, downloadXlsxWrapper, appendNewVersionBtn, closeBtn)));
            closeBtn.addClickListener(click -> dialog.close());
            asTemplateBtn.addClickListener(click -> new Dialog(new Span("Under construction")).open()); //todo implement
            appendNewVersionBtn.addClickListener(click -> new Dialog(new Span("Under construction")).open());

            dialog.setWidth("80%");
            dialog.open();
        } catch (Exception e) {
            Icon i = VaadinIcon.WARNING.create();
            i.setColor("Red");
            i.setSize("50px");
            VerticalLayout vl = new VerticalLayout(i, new Span("Something went wrong..."));
            vl.setAlignItems(Alignment.CENTER);
            new Dialog(vl).open();
            e.printStackTrace();
        }
    }
}
