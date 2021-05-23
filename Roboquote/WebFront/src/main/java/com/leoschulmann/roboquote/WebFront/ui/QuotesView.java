package com.leoschulmann.roboquote.WebFront.ui;

import com.leoschulmann.roboquote.WebFront.components.*;
import com.leoschulmann.roboquote.WebFront.events.QuoteViewerCancelClicked;
import com.leoschulmann.roboquote.WebFront.events.QuoteViewerCommentClicked;
import com.leoschulmann.roboquote.WebFront.events.QuoteViewerNewVersionClicked;
import com.leoschulmann.roboquote.WebFront.events.QuoteViewerTemplateClicked;
import com.leoschulmann.roboquote.WebFront.ui.bits.ErrorDialog;
import com.leoschulmann.roboquote.WebFront.ui.bits.GridSizeCombobox;
import com.leoschulmann.roboquote.WebFront.ui.bits.QuoteViewerWrapper;
import com.leoschulmann.roboquote.WebFront.ui.bits.ZoomButtons;
import com.leoschulmann.roboquote.quoteservice.entities.Quote;
import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.Route;
import org.javamoney.moneta.Money;
import org.vaadin.klaudeta.PaginatedGrid;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.vaadin.flow.component.grid.GridVariant.*;

@Route(value = "quotes", layout = MainLayout.class)
@CssImport(value = "./styles/styles.css", themeFor = "vaadin-grid")
public class QuotesView extends VerticalLayout {
    private final QuoteService quoteService;
    private final CurrencyFormatService currencyFormatService;
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
                      MoneyMathService moneyMathService, StringFormattingService stringFormattingService,
                      HttpRestService httpRestService) {
        this.quoteService = quoteService;
        this.currencyFormatService = currencyFormatService;
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
            try {
                openQuote(httpRestService.getQuoteById(qId));
            } catch (ServerCommunicationException e) {
                new ErrorDialog(e.getMessage()).open();
            }
        });
    }

    private HorizontalLayout createControlFrame() {
        ToggleButton cancelledToggle = createCancelledQuotesToggle();

        Button newQuoteBtn = new Button("Create new quote");
        newQuoteBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newQuoteBtn.addClickListener(c -> newQuoteBtn.getUI().ifPresent(ui -> ui.navigate("new")));

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

        HorizontalLayout layout = new HorizontalLayout(cancelledToggle, newQuoteBtn, customersCombo, dealerCombo, sizeCombo,
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
        try {
            return httpRestService.findAllQuotes(getWithCancelled);
        } catch (ServerCommunicationException e) {
            new ErrorDialog(e.getMessage()).open();
            return Collections.emptyList();
        }
    }

    private void openQuote(Quote quote) {
        try {
            QuoteViewer qv = new QuoteViewer(quote, currencyFormatService, moneyMathService, stringFormattingService);
            QuoteViewerWrapper qViewerDialog = new QuoteViewerWrapper(qv, quote, httpRestService);
            qViewerDialog.addListener(QuoteViewerCancelClicked.class, e -> handleCancelClick(e.getId(), e.isCancelAction()));
            qViewerDialog.addListener(QuoteViewerCommentClicked.class, e -> handleCommentClick(e.getId(), e.getComment()));
            qViewerDialog.addListener(QuoteViewerTemplateClicked.class, e -> handleTemplateClick(e.getQuote()));
            qViewerDialog.addListener(QuoteViewerNewVersionClicked.class, e -> handleNewVersionClick(e.getQuote()));
            qViewerDialog.setWidth("80%");
            qViewerDialog.open();
        } catch (Exception e) {
            new ErrorDialog("Something went wrong...").open();
            e.printStackTrace();
        }
    }

    private void handleNewVersionClick(Quote quote) {
        getUI().ifPresent(ui -> {
            Quote newVerQuote = quoteService.createNewVersion(quote);
            ui.getSession().setAttribute(Quote.class, newVerQuote);
            ui.navigate(NewQuote.class);
        });
    }

    private void handleTemplateClick(Quote quote) {
        getUI().ifPresent(ui -> {
            Quote templatedQuote = quoteService.createNewFromTemplate(quote);
            ui.getSession().setAttribute(Quote.class, templatedQuote);
            ui.navigate(NewQuote.class);
        });
    }

    private void handleCancelClick(int id, boolean cancelAction) {
        try {
            httpRestService.setCancelled(id, cancelAction);
        } catch (ServerCommunicationException e) {
            new ErrorDialog(e.getMessage()).open();
        }
        updateGrid(grid, showCancelled);
    }

    private void handleCommentClick(int id, String comment) {
        try {
            httpRestService.addComment(id, comment);
        } catch (ServerCommunicationException e) {
            new ErrorDialog(e.getMessage()).open();
        }
        updateGrid(grid, showCancelled);
    }

}
