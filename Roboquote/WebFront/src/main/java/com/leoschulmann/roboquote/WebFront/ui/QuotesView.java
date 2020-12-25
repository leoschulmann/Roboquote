package com.leoschulmann.roboquote.WebFront.ui;

import com.leoschulmann.roboquote.WebFront.components.*;
import com.leoschulmann.roboquote.quoteservice.entities.Quote;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
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

import static com.vaadin.flow.component.grid.GridVariant.*;

@Route(value = "quotes", layout = MainLayout.class)
public class QuotesView extends VerticalLayout {
    private final QuoteService quoteService;
    private final CurrencyFormatService currencyFormatService;
    private final DownloadService downloadService;
    private final MoneyMathService moneyMathService;
    private final StringFormattingService stringFormattingService;

    public QuotesView(QuoteService quoteService, CurrencyFormatService currencyFormatService,
                      DownloadService downloadService, MoneyMathService moneyMathService,
                      StringFormattingService stringFormattingService) {
        this.quoteService = quoteService;
        this.currencyFormatService = currencyFormatService;
        this.downloadService = downloadService;
        this.moneyMathService = moneyMathService;
        this.stringFormattingService = stringFormattingService;
        Grid<Quote> grid = createGrid();
        updateGrid(grid);
        add(grid);
        grid.addItemClickListener(event -> editQuote(event.getItem()));
    }

    private Grid<Quote> createGrid() {
        PaginatedGrid<Quote> grid = new PaginatedGrid<>(Quote.class);
        grid.addThemeVariants(LUMO_ROW_STRIPES, LUMO_WRAP_CELL_CONTENT, LUMO_COLUMN_BORDERS);
        grid.removeAllColumns();
        grid.addColumn(Quote::getCreated).setHeader("Created").setSortable(true).setKey("created").setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(q -> q.getNumber() + "-" + q.getVersion()).setHeader("#").setSortable(true).setKey("serialNum").setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(Quote::getCustomer).setHeader("Customer").setSortable(true).setResizable(true).setFlexGrow(1);
        grid.addColumn(Quote::getDealer).setHeader("Dealer").setSortable(true).setFlexGrow(1);
        grid.addColumn(q -> currencyFormatService.formatMoney(q.getFinalPrice() == null ?
                Money.of(BigDecimal.ZERO, "EUR") : q.getFinalPrice()))
                .setHeader("Quote Price")
                .setSortable(true).setComparator(q -> q.getFinalPrice() == null ? 0. : q.getFinalPrice().getNumber().doubleValue())
                .setAutoWidth(true).setFlexGrow(0);

        grid.setMultiSort(true);

        grid.setPageSize(15);
        grid.setPaginatorSize(5);
        GridSortOrder<Quote> byCreated = new GridSortOrder<>(grid.getColumnByKey("created"), SortDirection.DESCENDING);
        GridSortOrder<Quote> bySerial = new GridSortOrder<>(grid.getColumnByKey("serialNum"), SortDirection.DESCENDING);
        grid.sort(List.of(byCreated, bySerial));
        return grid;
    }

    private void updateGrid(Grid<Quote> grid) {
        grid.setItems(quoteService.findAll());
    }

    private void editQuote(Quote quote) {
        try {
            Dialog qViewerDialog = new Dialog(
                    new VerticalLayout(new QuoteViewer(quote, currencyFormatService, moneyMathService, stringFormattingService)));

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
                new StreamResource(serialNumber + "-" + version + downloadService.getExtension(),
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
