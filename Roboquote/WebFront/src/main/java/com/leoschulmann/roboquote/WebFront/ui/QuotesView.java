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
        grid.addItemClickListener(event -> editQuote(event.getItem()));
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
            Dialog qViewerDialog = new Dialog(
                    new VerticalLayout(new QuoteViewer(quote, currencyFormatService, moneyMathService)));

            FileDownloadWrapper downloadXlsxWrapper = createDownloadWrapper(quote.getNumber(), quote.getVersion(), quote.getId());
            Button asTemplateBtn = createAsTemplateBtn(quote, qViewerDialog);
            Button appendNewVersionBtn = createNewQuoteVersion(quote.getNumber(), quote.getVersion(), quote, qViewerDialog);
            Button closeBtn = createCloseBtn(qViewerDialog);
            HorizontalLayout btnPanel = new HorizontalLayout(asTemplateBtn, downloadXlsxWrapper, appendNewVersionBtn, closeBtn);
            btnPanel.setJustifyContentMode(JustifyContentMode.CENTER);
            btnPanel.setAlignItems(Alignment.CENTER);
            qViewerDialog.add(btnPanel);
            qViewerDialog.setWidth("80%");
            qViewerDialog.open();
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

    private FileDownloadWrapper createDownloadWrapper(String serialNumber, Integer version, int id) {
        Button downloadXlsxBtn = new Button("Download " + serialNumber + "-" + version + ".xlsx");
        downloadXlsxBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
        FileDownloadWrapper wrapper = new FileDownloadWrapper(
                new StreamResource(serialNumber + "-" + version + ".xlsx",
                        () -> new ByteArrayInputStream(downloadService.downloadXlsx(id))));
        wrapper.wrapComponent(downloadXlsxBtn);
        return wrapper;
    }

    private Button createAsTemplateBtn(Quote quote, Dialog qViewerDialog) {
        Button btn = new Button("Use as a template");
        btn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_CONTRAST);
        btn.addClickListener(click -> getUI().ifPresent(ui -> {
            Quote templatedQuote = quoteService.createNewFromTemplate(quote);
            ui.getSession().setAttribute(Quote.class, templatedQuote);
            qViewerDialog.close();
            ui.navigate(Compose.class);
        }));
        return btn;
    }

    private Button createNewQuoteVersion(String serialNumber, Integer version, Quote quote, Dialog qViewerDialog) {
        Button btn = new Button("Create new version of " + serialNumber + "-" + version);
        btn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btn.addClickListener(click -> getUI().ifPresent(ui -> {
            Quote newVerQuote = quoteService.createNewVersion(quote);
            ui.getSession().setAttribute(Quote.class, newVerQuote);
            qViewerDialog.close();
            ui.navigate(Compose.class);
        }));
        return btn;
    }

    private Button createCloseBtn(Dialog qViewerDialog) {
        Button btn = new Button("Close");
        btn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
        btn.addClickListener(click -> qViewerDialog.close());
        return btn;
    }
}
