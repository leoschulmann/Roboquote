package com.leoschulmann.roboquote.WebFront.ui;

import com.leoschulmann.roboquote.WebFront.components.*;
import com.leoschulmann.roboquote.WebFront.ui.bits.GridSizeCombobox;
import com.leoschulmann.roboquote.WebFront.ui.bits.ZoomButtons;
import com.leoschulmann.roboquote.quoteservice.entities.Quote;
import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.javamoney.moneta.Money;
import org.vaadin.klaudeta.PaginatedGrid;
import org.vaadin.olli.FileDownloadWrapper;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.vaadin.flow.component.grid.GridVariant.*;

@Route(value = "quotes", layout = MainLayout.class)
@CssImport(value = "./styles/styles.css", themeFor = "vaadin-grid")
public class QuotesView extends VerticalLayout {
    private final QuoteService quoteService;
    private final CurrencyFormatService currencyFormatService;
    private final DownloadService downloadService;
    private final MoneyMathService moneyMathService;
    private final StringFormattingService stringFormattingService;
    private final HttpRestService httpRestService;
    private final DateTimeFormatter dtf;
    private final PaginatedGrid<Quote> grid;
    private final List<Quote> quotes;
    private final ListDataProvider<Quote> dataProvider;
    private boolean showCancelled = false;
    private ComboBox<String> dealerCombo;
    private ComboBox<String> customersCombo;

    public QuotesView(QuoteService quoteService, CurrencyFormatService currencyFormatService,
                      DownloadService downloadService, MoneyMathService moneyMathService,
                      StringFormattingService stringFormattingService, HttpRestService httpRestService) {
        this.quoteService = quoteService;
        this.currencyFormatService = currencyFormatService;
        this.downloadService = downloadService;
        this.moneyMathService = moneyMathService;
        this.stringFormattingService = stringFormattingService;
        this.httpRestService = httpRestService;
        dtf = DateTimeFormatter.ofPattern("dd MMM yy");
        quotes = getQuotes(showCancelled);
        dataProvider = new ListDataProvider<>(quotes);
        grid = createGrid();
        add(createControlFrame(), grid);
        grid.addItemClickListener(event -> {
            int qId = event.getItem().getId();
            openQuote(httpRestService.getQuoteById(qId));
        });
    }

    private HorizontalLayout createControlFrame() {
        ToggleButton cancelledToggle = createCancelledQuotesToggle();
        customersCombo = new ComboBox<>();
        customersCombo.setItems(getDistinctCustomers());
        customersCombo.setPlaceholder("Customer");
        customersCombo.getElement().setProperty("title", "Customer");
        customersCombo.setClearButtonVisible(true);

        dealerCombo = new ComboBox<>();
        dealerCombo.setItems(getDistinctDealers());
        dealerCombo.setPlaceholder("Dealer");
        dealerCombo.getElement().setProperty("title", "Dealer");
        dealerCombo.setClearButtonVisible(true);


        customersCombo.addValueChangeListener(e -> {
            if (e.getValue() == null) {
                dataProvider.clearFilters();
            } else {
                dealerCombo.clear();
                dataProvider.setFilter(q -> q.getCustomer().equals(e.getValue()));
            }
        });

        dealerCombo.addValueChangeListener(e -> {
            if (e.getValue() == null) {
                dataProvider.clearFilters();
            } else {
                customersCombo.clear();
                dataProvider.setFilter(q -> q.getDealer().equals(e.getValue()));
            }
        });

        ComboBox<String> sizeCombo = new GridSizeCombobox(grid, quotes);

        ZoomButtons zoomButtons = new ZoomButtons(grid);

        HorizontalLayout layout = new HorizontalLayout(cancelledToggle, customersCombo, dealerCombo, sizeCombo,
                zoomButtons.getZoomOut(), zoomButtons.getZoomIn());
        layout.getStyle().set("margin-left", "auto");
        layout.setAlignItems(Alignment.CENTER);
        return layout;
    }

    private List<String> getDistinctDealers() {
        return quotes.stream().map(Quote::getDealer).distinct().sorted().collect(Collectors.toList());
    }

    private List<String> getDistinctCustomers() {
        return quotes.stream().map(Quote::getCustomer).distinct().sorted().collect(Collectors.toList());
    }

    private ToggleButton createCancelledQuotesToggle() {
        ToggleButton toggle = new ToggleButton(showCancelled);
        toggle.getElement().setProperty("title", "Show cancelled quotes");
        toggle.addValueChangeListener(e -> {
            showCancelled = e.getValue();
            updateGrid(grid, showCancelled);
        });
        return toggle;
    }

    private PaginatedGrid<Quote> createGrid() {
        PaginatedGrid<Quote> grid = new PaginatedGrid<>(Quote.class);
        grid.setDataProvider(dataProvider);
        grid.addThemeVariants(LUMO_COMPACT, LUMO_ROW_STRIPES, LUMO_COLUMN_BORDERS);
        grid.removeAllColumns();
        grid.addColumn((ValueProvider<Quote, String>) quote -> quote.getCreatedTimestamp().format(dtf))
                .setHeader("Created").setSortable(true).setKey("created").setAutoWidth(true).setFlexGrow(0)
                .setResizable(true).setComparator((ValueProvider<Quote, LocalDateTime>) Quote::getCreatedTimestamp);
        grid.addColumn(q -> q.getNumber() + "-" + q.getVersion()).setHeader("#").setSortable(true).setKey("serialNum")
                .setAutoWidth(true).setFlexGrow(0).setResizable(true);
        grid.addColumn(Quote::getCustomer).setHeader("Customer").setSortable(true).setResizable(true).setFlexGrow(1);
        grid.addColumn(Quote::getDealer).setHeader("Dealer").setSortable(true).setFlexGrow(1).setResizable(true);
        grid.addColumn(Quote::getComment).setHeader("Comment").setSortable(false).setFlexGrow(0).setResizable(true);
        grid.addColumn(q -> currencyFormatService.formatMoney(q.getFinalPrice() == null ?
                Money.of(BigDecimal.ZERO, "EUR") : q.getFinalPrice()))
                .setHeader("Quote Price")
                .setSortable(true).setComparator(q -> q.getFinalPrice() == null ? 0. : q.getFinalPrice().getNumber().doubleValue())
                .setAutoWidth(true).setFlexGrow(0).setResizable(true);

        grid.setMultiSort(true);
        grid.setClassNameGenerator(quote -> quote.getCancelled() ? "strikethrough" : "");
        grid.setPageSize(15);
        grid.setPaginatorSize(5);
        GridSortOrder<Quote> byCreated = new GridSortOrder<>(grid.getColumnByKey("created"), SortDirection.DESCENDING);
        grid.sort(List.of(byCreated));
        return grid;
    }

    private void updateGrid(PaginatedGrid<Quote> grid, boolean showCancelled) {
        quotes.clear();
        quotes.addAll(getQuotes(showCancelled));
        grid.getDataProvider().refreshAll();
        grid.refreshPaginator();
        customersCombo.setItems(getDistinctCustomers());
        dealerCombo.setItems(getDistinctDealers());
    }

    private List<Quote> getQuotes(boolean getWithCancelled) {
        return getWithCancelled ? httpRestService.findAllQuotes() : httpRestService.findAllUncancelledQuotes();
    }

    private void openQuote(Quote quote) {
        try {
            Dialog qViewerDialog = new Dialog(
                    new VerticalLayout(new QuoteViewer(quote, currencyFormatService, moneyMathService, stringFormattingService)));

            FileDownloadWrapper downloadXlsxWrapper = createDownloadWrapper(quote.getNumber(), quote.getVersion(), quote.getId());
            Button asTemplateBtn = createAsTemplateBtn(quote, qViewerDialog);
            Button appendNewVersionBtn = createNewQuoteVersion(quote.getNumber(), quote.getVersion(), quote, qViewerDialog);
            Button closeBtn = createCloseBtn(qViewerDialog);
            Button addCommentBtn = createCommentButton(quote, qViewerDialog);
            Button cancelBtn = createCancelButton(quote, qViewerDialog);
            FormLayout responsiveLayout = new FormLayout();
            responsiveLayout.setResponsiveSteps(
                    new FormLayout.ResponsiveStep("25em", 1),
                    new FormLayout.ResponsiveStep("32em", 2),
                    new FormLayout.ResponsiveStep("40em", 3));

            responsiveLayout.add(asTemplateBtn, appendNewVersionBtn, downloadXlsxWrapper,
                    addCommentBtn, cancelBtn, closeBtn);
            qViewerDialog.add(responsiveLayout);
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

    private Button createCancelButton(Quote quote, Dialog viewerDialog) {
        boolean cancelAction = !quote.getCancelled();
        Button button = new Button();
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        button.setText(cancelAction ? "Cancel quote" : "Uncancel quote");
        button.addClickListener(c -> {
            httpRestService.setCancelled(quote.getId(), cancelAction);
            updateGrid(grid, showCancelled);
            viewerDialog.close();
        });
        return button;
    }

    private Button createCommentButton(Quote quote, Dialog qViewerDialog) {
        Button button = new Button("Add comment");
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        Dialog dialog = new Dialog();

        TextField textField = new TextField();
        textField.setWidth("30em");
        textField.setValue(Objects.requireNonNullElse(quote.getComment(), ""));
        Button ok = new Button("OK", click -> {
            httpRestService.addComment(quote.getId(), textField.getValue());
            updateGrid(grid, showCancelled);
            qViewerDialog.close();
            dialog.close();
        });
        ok.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
        VerticalLayout vl = new VerticalLayout(textField, ok);
        vl.setAlignItems(Alignment.CENTER);
        dialog.add(vl);
        button.addClickListener(c -> dialog.open());
        return button;
    }

    private FileDownloadWrapper createDownloadWrapper(String serialNumber, Integer version, int id) {
        Button downloadXlsxBtn = new Button("Download " + serialNumber + "-" + version + ".xlsx");
        downloadXlsxBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
        FileDownloadWrapper wrapper = new FileDownloadWrapper(
                new StreamResource(serialNumber + "-" + version + downloadService.getExtension(),
                        () -> new ByteArrayInputStream(downloadService.downloadXlsx(id))));
        downloadXlsxBtn.setWidthFull();
        wrapper.wrapComponent(downloadXlsxBtn);
        return wrapper;
    }

    private Button createAsTemplateBtn(Quote quote, Dialog qViewerDialog) {
        Button btn = new Button("Use as a template");
        btn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btn.addClickListener(click -> getUI().ifPresent(ui -> {
            Quote templatedQuote = quoteService.createNewFromTemplate(quote);
            ui.getSession().setAttribute(Quote.class, templatedQuote);
            qViewerDialog.close();
            ui.navigate(NewQuote.class);
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
            ui.navigate(NewQuote.class);
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
