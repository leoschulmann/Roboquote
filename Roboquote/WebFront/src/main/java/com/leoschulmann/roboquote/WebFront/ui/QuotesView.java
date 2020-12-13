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
    private final MoneyMathService moneyMathService;

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
        grid.addColumns("number", "version", "customer", "dealer", "created");
        grid.addColumn(q -> {

            Money m = q.getFinalPrice();

            return currencyFormatService.formatMoney(m == null ? Money.of(BigDecimal.ZERO, "EUR") : m);
        }).setHeader("Quote Price");
        grid.setMultiSort(true);

        grid.setPageSize(15);
        grid.setPaginatorSize(5);
        GridSortOrder<Quote> byCreated = new GridSortOrder<>(grid.getColumnByKey("created"), SortDirection.DESCENDING);
        GridSortOrder<Quote> byNum = new GridSortOrder<>(grid.getColumnByKey("number"), SortDirection.DESCENDING);
        GridSortOrder<Quote> byVer = new GridSortOrder<>(grid.getColumnByKey("version"), SortDirection.DESCENDING);
        grid.sort(List.of(byCreated, byNum, byVer));
    }

    private void updateGrid() {
        grid.setItems(quoteService.findAll());
    }

    private void editQuote(Quote quote) {
        try {
            Button downloadXlsxBtn = new Button("Download " + quote.getNumber() + "-" + quote.getVersion() + ".xlsx");
            FileDownloadWrapper downloadXlsxWrapper = new FileDownloadWrapper(
                    new StreamResource(quote.getNumber() + "-" + quote.getVersion() + ".xlsx",
                            () -> new ByteArrayInputStream(downloadService.downloadXlsx(quote.getId()))));
            downloadXlsxWrapper.wrapComponent(downloadXlsxBtn);
            Button closeBtn = new Button("Close");
            Button asTemplateBtn = new Button("Use as a template");
            Button appendNewVersionBtn = new Button("Create new version of " + quote.getNumber() + "-" + (quote.getVersion()));
            downloadXlsxBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
            closeBtn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
            asTemplateBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_CONTRAST);
            appendNewVersionBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);


            Dialog dialog = new Dialog(new VerticalLayout(new QuoteViewer(quote, currencyFormatService, moneyMathService),
                    new HorizontalLayout(asTemplateBtn, downloadXlsxWrapper, appendNewVersionBtn, closeBtn)));
            closeBtn.addClickListener(click -> dialog.close());


            asTemplateBtn.addClickListener(click -> getUI().ifPresent(ui -> {
                Quote templatedQuote = quoteService.createNewFromTemplate(quote);

                ui.getSession().setAttribute(Quote.class, templatedQuote);
                dialog.close();
                ui.navigate(Compose.class);
            }));

            appendNewVersionBtn.addClickListener(click -> getUI().ifPresent(ui -> {
                Quote newVerQuote = quoteService.createNewVersion(quote);

                ui.getSession().setAttribute(Quote.class, newVerQuote);
                dialog.close();
                ui.navigate(Compose.class);
            }));

            dialog.setWidth("80%");
            dialog.open();
        } catch (
                Exception e) {
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
