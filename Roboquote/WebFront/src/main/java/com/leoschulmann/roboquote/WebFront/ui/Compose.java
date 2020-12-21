package com.leoschulmann.roboquote.WebFront.ui;

import com.leoschulmann.roboquote.WebFront.components.*;
import com.leoschulmann.roboquote.WebFront.events.ComposeDeleteItemPositionEvent;
import com.leoschulmann.roboquote.WebFront.events.ComposeItemPositionQuantityEvent;
import com.leoschulmann.roboquote.WebFront.events.UniversalSectionChangedEvent;
import com.leoschulmann.roboquote.itemservice.entities.Item;
import com.leoschulmann.roboquote.quoteservice.entities.ItemPosition;
import com.leoschulmann.roboquote.quoteservice.entities.Quote;
import com.leoschulmann.roboquote.quoteservice.entities.QuoteSection;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.shared.Registration;
import org.javamoney.moneta.Money;
import org.vaadin.olli.FileDownloadWrapper;

import javax.money.MonetaryAmount;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;


@Route(value = "compose", layout = MainLayout.class)
public class Compose extends VerticalLayout implements AfterNavigationObserver {
    private ItemService itemService;
    private CurrencyFormatService currencyFormatter;
    private InventoryItemToItemPositionConverter converter;
    private QuoteSectionHandler sectionHandler;
    private DownloadService downloadService;
    private CurrencyRatesService currencyRatesService;
    private QuoteService quoteService;
    private StringFormattingService stringFormattingService;
    private MoneyMathService moneyMathService;

    private VerticalLayout gridsBlock;
    private List<SectionGrid> gridList;
    private ComboBox<SectionGrid> avaiableGridsBox;
    private Binder<Quote> quoteBinder = new Binder<>(Quote.class);
    private Quote quote;
    private final Span totalString;
    private final Span totalWithDiscountString;
    private final Span includingVatValue;
    private Integer discount = 0;
    private Integer vat = 20;
    private Set<HasEnabled> clickableComponents;

    private String currency = "EUR";
    private BigDecimal euroRate;
    private BigDecimal dollarRate;
    private BigDecimal yenRate;
    private double exchangeConversionFee;

    public Compose(ItemService itemService,
                   CurrencyFormatService currencyFormatter,
                   InventoryItemToItemPositionConverter converter,
                   QuoteSectionHandler sectionHandler,
                   DownloadService downloadService,
                   CurrencyRatesService currencyRatesService,
                   QuoteService quoteService,
                   StringFormattingService stringFormattingService,
                   MoneyMathService moneyMathService) {

        this.itemService = itemService;
        this.currencyFormatter = currencyFormatter;
        this.converter = converter;
        this.sectionHandler = sectionHandler;
        this.downloadService = downloadService;
        this.currencyRatesService = currencyRatesService;
        this.quoteService = quoteService;
        this.stringFormattingService = stringFormattingService;
        this.moneyMathService = moneyMathService;
        this.clickableComponents = new HashSet<>();
        this.gridList = new ArrayList<>();
        totalString = new Span();
        totalString.getElement().getStyle().set("margin-left", "auto");
        totalWithDiscountString = new Span();
        totalWithDiscountString.getElement().getStyle().set("margin-left", "auto").set("font-weight", "bold");
        includingVatValue = new Span();
        includingVatValue.getElement().getStyle().set("margin-left", "auto");
        add(getInventoryLookupAccordeon());
        gridsBlock = createGridsBlock();

        add(createQuoteInfoBlock());
        add(gridsBlock);
        add(createFinishBlock());
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {

        getUI().ifPresent(ui -> {
            quote = Objects.requireNonNullElseGet(ui.getSession().getAttribute(Quote.class),
                    () -> {
                        //empty quote with default rates, vat, discount and one empty quote section
                        Quote q = new Quote(0, 20, BigDecimal.valueOf(100), BigDecimal.valueOf(100),
                                BigDecimal.ONE, BigDecimal.valueOf(2));
                        q.addSections(new QuoteSection("New quote section"));
                        return q;
                    });

            quote.setValidThru(LocalDate.now().plus(3, ChronoUnit.MONTHS));

            ui.getSession().setAttribute(Quote.class, null); //delete session payload if any
            quoteBinder.readBean(quote);
            quote.getSections().forEach(this::addNewGrid);
        });
    }

    private Accordion createQuoteInfoBlock() {
        FormLayout columnLayout = new FormLayout();
        columnLayout.setResponsiveSteps(
                new ResponsiveStep("25em", 1),
                new ResponsiveStep("32em", 2),
                new ResponsiveStep("40em", 3));
        TextField customer = new TextField("Customer"); //todo lookup in DB
        TextField customerInfo = new TextField("Customer info");
        TextField dealer = new TextField("Dealer");
        TextField dealerInfo = new TextField("Dealer info");
        TextField paymentTerms = new TextField("Payment Terms");
        TextField shippingTerms = new TextField("Shipping Terms");
        TextField warranty = new TextField("Warranty");
        TextField installation = new TextField("Installation");

        DatePicker validThru = new DatePicker("Valid through date");
        validThru.setValue(LocalDate.now().plus(3, ChronoUnit.MONTHS));
        addToClickableComponents(validThru, warranty, customer, customerInfo, dealer,
                dealerInfo, paymentTerms, shippingTerms, installation);

        columnLayout.add(customer);
        columnLayout.add(customerInfo, 2);
        columnLayout.add(dealer);
        columnLayout.add(dealerInfo, 2);
        columnLayout.add(paymentTerms, shippingTerms, warranty, installation, validThru);
        columnLayout.add(createRatesBlock(), 3);
        add(columnLayout);

        quoteBinder.forField(customer).asRequired().bind(Quote::getCustomer, Quote::setCustomer);
        quoteBinder.bind(customerInfo, Quote::getCustomerInfo, Quote::setCustomerInfo);
        quoteBinder.bind(dealer, Quote::getDealer, Quote::setDealer);
        quoteBinder.bind(dealerInfo, Quote::getDealerInfo, Quote::setDealerInfo);
        quoteBinder.bind(paymentTerms, Quote::getPaymentTerms, Quote::setPaymentTerms);
        quoteBinder.bind(shippingTerms, Quote::getShippingTerms, Quote::setShippingTerms);
        quoteBinder.bind(warranty, Quote::getWarranty, Quote::setWarranty);
        quoteBinder.bind(installation, Quote::getInstallation, Quote::setInstallation);
        quoteBinder.forField(validThru).asRequired().bind(Quote::getValidThru, Quote::setValidThru);

        Accordion accordion = new Accordion();
        accordion.setWidthFull();
        accordion.add("Quote details", columnLayout);
        return accordion;
    }

    private HorizontalLayout createRatesBlock() {
        Button update = new Button("Get rates");
        update.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        BigDecimalField euro = new BigDecimalField("₽/€");
        BigDecimalField dollar = new BigDecimalField("₽/$");
        BigDecimalField yen = new BigDecimalField("¥/₽");
        euro.setValue(euroRate);
        dollar.setValue(dollarRate);
        yen.setValue(yenRate);
        NumberField conversionRate = new NumberField("Conversion rate");
        euro.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
        dollar.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
        yen.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);

        addToClickableComponents(update, euro, dollar, yen, conversionRate);

        conversionRate.setSuffixComponent(new Span("%"));
        conversionRate.setValue(exchangeConversionFee);

        conversionRate.setMin(-99.);
        conversionRate.setMax(99.);
        conversionRate.setHasControls(true);
        conversionRate.setStep(0.5);

        update.addClickListener(event -> {
            euro.setValue(currencyRatesService.getRubEurRate());
            dollar.setValue(currencyRatesService.getRubUSDRate());
            yen.setValue(currencyRatesService.getRubJPYRate());
        });

        euro.addValueChangeListener(e -> {
            euroRate = e.getValue();
            refreshAll();
            fireEvent(new UniversalSectionChangedEvent(this));
        });
        dollar.addValueChangeListener(e -> {
            dollarRate = e.getValue();
            refreshAll();
            fireEvent(new UniversalSectionChangedEvent(this));
        });
        yen.addValueChangeListener(e -> {
            yenRate = e.getValue();
            refreshAll();
            fireEvent(new UniversalSectionChangedEvent(this));
        });
        conversionRate.addValueChangeListener(e -> {
            exchangeConversionFee = e.getValue();
            refreshAll();
            fireEvent(new UniversalSectionChangedEvent(this));
        });

        quoteBinder.forField(euro).asRequired().bind(Quote::getEurRate, Quote::setEurRate);
        quoteBinder.forField(dollar).asRequired().bind(Quote::getUsdRate, Quote::setUsdRate);
        quoteBinder.forField(yen).asRequired().bind(Quote::getJpyRate, Quote::setJpyRate);
        quoteBinder.forField(conversionRate).asRequired().bind(quote -> quote.getConversionRate().doubleValue(),
                (quote1, conversionRate1) -> quote1.setConversionRate(BigDecimal.valueOf(conversionRate1)));

        HorizontalLayout hl = new HorizontalLayout(update, conversionRate, euro, dollar, yen);
        hl.setAlignItems(Alignment.END);
        return hl;
    }

    private Accordion getInventoryLookupAccordeon() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        ComboBox<Item> searchBox = getItemComboBox();
        avaiableGridsBox = new ComboBox<>();
        resetAvailableGridsCombobox();
        Button addToGridBtn = new Button(VaadinIcon.PLUS.create());
        addToGridBtn.addClickListener(click -> {
            if (searchBox.getValue() != null && avaiableGridsBox.getValue() != null) {
                ItemPosition ip = converter.convert(searchBox.getValue());
                QuoteSection qs = avaiableGridsBox.getValue().getQuoteSection();
                sectionHandler.putToSection(qs, ip);
                refreshSectionSubtotal(currency, qs);
                fireEvent(new UniversalSectionChangedEvent(this));
                refreshTotal();
            }
        });
        addToClickableComponents(addToGridBtn);
        searchBox.setWidthFull();
        searchBox.setPlaceholder("Inventory lookup");
        avaiableGridsBox.setWidthFull();
        avaiableGridsBox.setPlaceholder("Quote section");
        addToGridBtn.setWidth("15%");
        layout.add(searchBox, avaiableGridsBox, addToGridBtn);
        Accordion accordion = new Accordion();
        accordion.setWidthFull();
        accordion.add("Inventory lookup", layout);
        return accordion;
    }

    private void refreshTotal() {
        MonetaryAmount am = getTotalMoney();
        totalString.setText(stringFormattingService.getCombined(am));
        totalWithDiscountString.setText(stringFormattingService.getCombinedWithDiscountOrMarkup(am, discount));
        includingVatValue.setText(stringFormattingService.getVat(am, discount, vat));

        totalWithDiscountString.setVisible(discount != 0);

        if (discount == 0) {
            totalString.getElement().getStyle().set("font-weight", "bold").remove("text-decoration");
        } else {
            totalString.getElement().getStyle().set("text-decoration", "line-through").remove("font-weight");
        }
    }

    private void refreshSectionSubtotal(String currency, QuoteSection qs) {
        sectionHandler.updateSubtotalToCurrency(qs, currency,
                euroRate, dollarRate, yenRate, exchangeConversionFee);
    }

    private void refreshAll() {
        gridList.stream().map(SectionGrid::getQuoteSection)
                .forEach(qs -> refreshSectionSubtotal(currency, qs));
        refreshTotal();
    }

    private MonetaryAmount getTotalMoney() {

        return moneyMathService.getSum(gridList.stream()
                .map(grid -> grid.getQuoteSection().getTotalDiscounted())
                .collect(Collectors.toList()));
    }

    private VerticalLayout createGridsBlock() {
        return new VerticalLayout();
    }

    private VerticalLayout createFinishBlock() {
        IntegerField discountField = new IntegerField();
        IntegerField vatField = new IntegerField();
        ComboBox<String> currencyCombo = new ComboBox<>("Currency", "EUR", "USD", "RUB", "JPY");
        Button addNewSectionBtn = new Button("Add new section");
        Button postQuote = new Button("Save to DB");
        Button dlButt = new Button("Download .xlsx");
        FileDownloadWrapper wrapper = new FileDownloadWrapper(
                new StreamResource("error", () -> new ByteArrayInputStream(new byte[]{}))
        );

        addToClickableComponents(discountField, vatField, postQuote, currencyCombo, addNewSectionBtn);

        discountField.setValue(0);
        discountField.setHasControls(true);
        discountField.setMin(-99);
        discountField.setMax(99);
        discountField.setLabel("Discount, %");
        discountField.addValueChangeListener(c -> {
            if (c.getValue() < 0) discountField.setLabel("Premium, %");
            else discountField.setLabel("Discount, %");
            discount = c.getValue();
            refreshTotal();
        });

        vatField.setValue(20);
        vatField.setHasControls(true);
        vatField.setMin(0);
        vatField.setMax(99);
        vatField.setLabel("Vat, %");
        vatField.addValueChangeListener(c -> {
            vat = c.getValue();
            refreshTotal();
        });

        currencyCombo.setValue(currency);
        currencyCombo.addValueChangeListener(event -> {
            currency = event.getValue();
            refreshAll();
            fireEvent(new UniversalSectionChangedEvent(this));
        });

        wrapper.wrapComponent(dlButt);
        wrapper.setVisible(false);
        quoteBinder.forField(discountField).asRequired().bind(Quote::getDiscount, Quote::setDiscount);
        quoteBinder.forField(vatField).asRequired().bind(Quote::getVat, Quote::setVat);

        HorizontalLayout buttons = new HorizontalLayout(discountField, vatField, currencyCombo, addNewSectionBtn,
                postQuote, wrapper);
        buttons.setAlignItems(Alignment.BASELINE);

        postQuote.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
        postQuote.addClickListener(click -> {
            if (quoteBinder.validate().isOk() && gridsNotEmpty()) {
                int id = postToDbAndGetID();
                byte[] bytes = downloadService.downloadXlsx(id);
                wrapper.setResource(new StreamResource(UUID.nameUUIDFromBytes(bytes).toString() + ".xlsx",
                        () -> new ByteArrayInputStream(bytes)));
                wrapper.setVisible(true);

                disableClickableComponents();
            } else {
                showValidationErrorDialog();
            }
        });

        addNewSectionBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addNewSectionBtn.addClickListener(c -> {
            TextField tf = new TextField();
            Button addBtn = new Button("Add");
            addBtn.setEnabled(false);
            tf.setWidth("32em");
            tf.addValueChangeListener(event -> addBtn.setEnabled(!event.getValue().isBlank()));
            addBtn.setWidthFull();
            addBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            Dialog dialog = new Dialog(new VerticalLayout(tf, addBtn));
            addBtn.addClickListener(click -> {
                QuoteSection qs = new QuoteSection(tf.getValue().trim());
                quoteService.addSections(quote, qs);
                addNewGrid(qs);
                tf.clear();
                dialog.close();
            });
            dialog.open();
        });
        return new VerticalLayout(totalString, totalWithDiscountString, includingVatValue, buttons);
    }

    private void showValidationErrorDialog() {
        Icon i = VaadinIcon.WARNING.create();
        i.setColor("Red");
        i.setSize("50px");
        VerticalLayout vl = new VerticalLayout(i);
        if (!quoteBinder.validate().isOk()) vl.add(new Span("Please fill marked fields"));
        if (!gridsNotEmpty()) vl.add(new Span("Some sections are empty"));
        vl.setAlignItems(Alignment.CENTER);
        new Dialog(vl).open();
    }

    private boolean gridsNotEmpty() {
        return gridList.stream().noneMatch(s -> s.getQuoteSection().getPositions().size() == 0);
    }

    private int postToDbAndGetID() {
        try {
            quoteBinder.writeBean(quote);
            quote.setFinalPrice((Money) getTotalMoney().multiply((100.0 - discount) / 100));
            return quoteService.postNew(quote);
        } catch (ValidationException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private void addNewGrid(QuoteSection quoteSection) {
        Accordion accordion = new Accordion();
        accordion.setWidthFull();
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        SectionGrid sg = new SectionGrid(quoteSection, currencyFormatter, stringFormattingService);

        sg.addListener(ComposeDeleteItemPositionEvent.class, this::itemPositionDeleted);
        sg.addListener(ComposeItemPositionQuantityEvent.class, this::itemPositionQuantityChanged);
        addListener(UniversalSectionChangedEvent.class, sg::sectionChangedEvent);

        gridList.add(sg);
        resetAvailableGridsCombobox();

        layout.add(getGridHeaderPanel(accordion, quoteSection.getName(), sg), sg, sg.getFooter());
        accordion.add(quoteSection.getName(), layout);
        gridsBlock.add(accordion);
        refreshSectionSubtotal(currency, sg.getQuoteSection());
        refreshTotal();
        fireEvent(new UniversalSectionChangedEvent(this));
    }

    private HorizontalLayout getGridHeaderPanel(Accordion acc, String name, SectionGrid grid) {
        HorizontalLayout head = new HorizontalLayout();
        head.setWidthFull();
        TextField nameField = new TextField();
        nameField.setWidth("50%");
        nameField.setValue(name);
        nameField.setVisible(false);

        nameField.addValueChangeListener(event -> {
            if (!event.getValue().isBlank()) {
                sectionHandler.setSectionName(grid.getQuoteSection(), event.getValue().trim());
                fireEvent(new UniversalSectionChangedEvent(this));
                resetAvailableGridsCombobox();
                acc.getOpenedPanel().ifPresent(panel -> panel.setSummary(new Span(event.getValue().trim())));
            }
        });

        Button editNameBtn = new Button(VaadinIcon.EDIT.create());
        editNameBtn.addClickListener(c -> nameField.setVisible(!nameField.isVisible()));

        IntegerField discountField = new IntegerField();
        discountField.setValue(0);
        discountField.setVisible(false);
        discountField.setHasControls(true);
        discountField.setMin(-99);
        discountField.setMax(99);
        discountField.addValueChangeListener(c -> {
            sectionHandler.setSectionDiscount(grid.getQuoteSection(), c.getValue());
            refreshSectionSubtotal(currency, grid.getQuoteSection());
            refreshTotal();
            fireEvent(new UniversalSectionChangedEvent(this));
        });


        Button discountBtn = new Button("%");
        discountBtn.addClickListener(c -> discountField.setVisible(!discountField.isVisible()));

        Button deleteBtn = new Button(VaadinIcon.TRASH.create());
        deleteBtn.addClickListener(c -> {
            gridsBlock.remove(acc);
            gridList.remove(grid);
            resetAvailableGridsCombobox();
            refreshTotal();
        });
        addToClickableComponents(deleteBtn, discountField, nameField);

        head.add(nameField, editNameBtn, discountField, discountBtn, deleteBtn);

        return head;
    }

    private ComboBox<Item> getItemComboBox() {
        ComboBox<Item> filteringComboBox = new ComboBox<>();
        filteringComboBox.addClassName("compose-querybox");
        List<Item> elementsList = itemService.findAll(); //todo make 'prepare elements' mechanism to reduce DB load

        ComboBox.ItemFilter<Item> filter = (ComboBox.ItemFilter<Item>)
                (element, filterString) -> element.getNameRus().toLowerCase().contains(filterString.toLowerCase()) ||
                        element.getNameEng().toLowerCase().contains(filterString.toLowerCase()) ||
                        element.getPartno().toLowerCase().contains(filterString.toLowerCase());

        filteringComboBox.setItems(filter, elementsList);
        filteringComboBox.setItemLabelGenerator((ItemLabelGenerator<Item>) item -> item.getPartno() + " "
                + item.getNameRus().substring(0, Math.min(item.getNameRus().length(), 35)) + " | "
                + item.getNameEng().substring(0, Math.min(item.getNameEng().length(), 35))
        );

        filteringComboBox.setClearButtonVisible(true);
        return filteringComboBox;
    }

    private void addToClickableComponents(HasEnabled... components) {
        clickableComponents.addAll(Arrays.asList(components));
    }

    private void disableClickableComponents() {
        clickableComponents.stream().filter(Objects::nonNull).forEach(c -> c.setEnabled(false));
        gridList.forEach(SectionGrid::disableClickables);
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

    private void itemPositionDeleted(ComposeDeleteItemPositionEvent event) {
        sectionHandler.deletePosition(event.getGrid().getQuoteSection(), event.getItemPosition());
        refreshSectionSubtotal(currency, event.getGrid().getQuoteSection());
        refreshTotal();
    }

    private void itemPositionQuantityChanged(ComposeItemPositionQuantityEvent ev) {
        sectionHandler.setQty(ev.getGrid().getQuoteSection(), ev.getItemPosition(), ev.getQty());
        refreshSectionSubtotal(currency, ev.getGrid().getQuoteSection());
        refreshTotal();
    }

    private void resetAvailableGridsCombobox() {
        avaiableGridsBox.setItems(gridList);
    }
}
