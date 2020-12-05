package com.leoschulmann.roboquote.WebFront.ui;

import com.leoschulmann.roboquote.WebFront.components.*;
import com.leoschulmann.roboquote.WebFront.events.ComposeDeleteItemPositionEvent;
import com.leoschulmann.roboquote.WebFront.events.ComposeItemPositionQuantityEvent;
import com.leoschulmann.roboquote.WebFront.events.UniversalSectionChangedEvent;
import com.leoschulmann.roboquote.WebFront.pojo.QuoteDetails;
import com.leoschulmann.roboquote.itemservice.entities.Item;
import com.leoschulmann.roboquote.quoteservice.entities.ItemPosition;
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
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.shared.Registration;
import org.javamoney.moneta.Money;
import org.javamoney.moneta.function.MonetaryFunctions;
import org.vaadin.olli.FileDownloadWrapper;

import javax.money.MonetaryAmount;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;


@Route(value = "compose", layout = MainLayout.class)
public class Compose extends VerticalLayout {
    private ItemService itemService;
    private CurrencyFormatService currencyFormatter;
    private InventoryItemToItemPositionConverter converter;
    private QuoteSectionHandler sectionHandler;
    private QuoteAssembler assembler;
    private DownloadService downloadService;
    private CurrencyRatesService currencyRatesService;

    private VerticalLayout gridsBlock;
    private List<SectionGrid> gridList;
    private ComboBox<SectionGrid> avaiableGridsBox;
    private Binder<QuoteDetails> detailsBinder = new Binder<>(QuoteDetails.class);
    private final H4 totalString = new H4();
    private final H4 totalWithDiscountString = new H4();
    private final H5 includingVatValue = new H5();
    private Integer discount = 0;
    private Integer vat = DEFAULT_VAT;
    private Set<HasEnabled> clickableComponents;

    static final String DEFAULT_SECTION_NAME = "New quote section";
    static final Integer DEFAULT_VAT = 20;

    private String currency = "EUR";
    private BigDecimal euroRate = new BigDecimal(100);
    private BigDecimal dollarRate = new BigDecimal(100);
    private BigDecimal yenRate = BigDecimal.ONE;
    private double exchangeConversionFee = 2.;

    public Compose(ItemService itemService,
                   CurrencyFormatService currencyFormatter,
                   InventoryItemToItemPositionConverter converter,
                   QuoteSectionHandler sectionHandler,
                   QuoteAssembler assembler, DownloadService downloadService,
                   CurrencyRatesService currencyRatesService) {

        this.itemService = itemService;
        this.currencyFormatter = currencyFormatter;
        this.converter = converter;
        this.sectionHandler = sectionHandler;
        this.assembler = assembler;
        this.downloadService = downloadService;
        this.currencyRatesService = currencyRatesService;
        this.clickableComponents = new HashSet<>();
        this.gridList = new ArrayList<>();

        add(getInventoryLookupAccordeon());
        gridsBlock = createGridsBlock();
        addNewGrid(DEFAULT_SECTION_NAME);

        add(quoteInfoBlock());
        add(gridsBlock);
        add(createFinishBlock());
    }

    private Accordion quoteInfoBlock() {
        FormLayout columnLayout = new FormLayout();
        columnLayout.setResponsiveSteps(
                new ResponsiveStep("25em", 1),
                new ResponsiveStep("32em", 2),
                new ResponsiveStep("40em", 3));
        TextField customer = new TextField(); //todo lookup in DB
        customer.setPlaceholder("Customer");
        EmailField customerInfo = new EmailField();
        customerInfo.setPlaceholder("Customer info");

        TextField dealer = new TextField();
        dealer.setPlaceholder("Dealer");
        EmailField dealerInfo = new EmailField();
        dealerInfo.setPlaceholder("Dealer info");
        TextField paymentTerms = new TextField();
        paymentTerms.setPlaceholder("Payment Terms");  //todo make selectable from list
        TextField shippingTerms = new TextField();
        shippingTerms.setPlaceholder("Shipping Terms");  //todo make selectable from list
        TextField warranty = new TextField();
        warranty.setPlaceholder("Warranty");    //todo make selectable from list

        DatePicker validThru = new DatePicker();
        validThru.setLabel("Valid through date");
        validThru.setValue(LocalDate.now().plus(3, ChronoUnit.MONTHS));
        addToClickableComponents(validThru, warranty, customer, customerInfo, dealer,
                dealerInfo, paymentTerms, shippingTerms);

        columnLayout.add(customer);
        columnLayout.add(customerInfo, 2);
        columnLayout.add(dealer);
        columnLayout.add(dealerInfo, 2);
        columnLayout.add(paymentTerms, shippingTerms, warranty, validThru);
        columnLayout.add(createRatesBlock(), 3);
        add(columnLayout);

        detailsBinder.bind(customer, QuoteDetails::getCustomer, QuoteDetails::setCustomer);
        detailsBinder.bind(customerInfo, QuoteDetails::getCustomerInfo, QuoteDetails::setCustomerInfo);
        detailsBinder.bind(dealer, QuoteDetails::getDealer, QuoteDetails::setDealer);
        detailsBinder.bind(dealerInfo, QuoteDetails::getDealerInfo, QuoteDetails::setDealerInfo);
        detailsBinder.bind(paymentTerms, QuoteDetails::getPaymentTerms, QuoteDetails::setPaymentTerms);
        detailsBinder.bind(shippingTerms, QuoteDetails::getShippingTerms, QuoteDetails::setShippingTerms);
        detailsBinder.bind(warranty, QuoteDetails::getWarranty, QuoteDetails::setWarranty);
        detailsBinder.bind(validThru, QuoteDetails::getValidThru, QuoteDetails::setValidThru);

        Accordion accordion = new Accordion();
        accordion.setWidthFull();
        accordion.add("Quote details", columnLayout);
        return accordion;
    }

    private HorizontalLayout createRatesBlock() {
        Button update = new Button("Get rates");
        update.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        BigDecimalField euro = new BigDecimalField();
        BigDecimalField dollar = new BigDecimalField();
        BigDecimalField yen = new BigDecimalField();
        euro.setValue(euroRate);
        dollar.setValue(dollarRate);
        yen.setValue(yenRate);
        NumberField conversionRate = new NumberField();
        euro.setPrefixComponent(VaadinIcon.EURO.create());
        euro.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
        dollar.setPrefixComponent(VaadinIcon.DOLLAR.create());
        dollar.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
        yen.setPrefixComponent(new Span("¥"));
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

        detailsBinder.bind(conversionRate, QuoteDetails::getConversionRate, QuoteDetails::setConversionRate);
        detailsBinder.bind(euro, QuoteDetails::getEurRate, QuoteDetails::setEurRate);
        detailsBinder.bind(dollar, QuoteDetails::getUsdRate, QuoteDetails::setUsdRate);
        detailsBinder.bind(yen, QuoteDetails::getJpyRate, QuoteDetails::setJpyRate);

        return new HorizontalLayout(update, conversionRate, euro, dollar, yen);
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
        totalString.setText("TOTAL " + currencyFormatter.formatMoney(am));
        totalWithDiscountString.setText("TOTAL (discounted " + discount + "%) "
                + currencyFormatter.formatMoney(am.multiply((100.0 - discount) / 100)));
        totalWithDiscountString.setVisible(discount > 0);

        includingVatValue.setText("(incl. VAT " + vat + "% " +
                currencyFormatter.formatMoney(
                        am.multiply((100.0 - discount) / 100)
                                .multiply(vat / 100.)
                                .divide((vat + 100) / 100.))
                + ")");
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
        return gridList.stream()
                .map(gr -> gr.getQuoteSection().getTotalDiscounted())
                .reduce(MonetaryFunctions.sum())
                .orElseGet(() -> Money.of(0, currency));
    }

    private VerticalLayout createGridsBlock() {
        return new VerticalLayout();
    }

    private VerticalLayout createFinishBlock() {
        VerticalLayout layout = new VerticalLayout();
        HorizontalLayout controlSublayout = new HorizontalLayout();
        TextField gridNameInputField = new TextField();  //todo add validation (non-empty, non-duplicating, etc)
        Button addNewGridButton = new Button(VaadinIcon.PLUS.create());

        layout.setWidthFull();
        controlSublayout.setWidthFull();
        gridNameInputField.setPlaceholder("Add new section ");
        gridNameInputField.setWidth("50%");
        addNewGridButton.addClickListener(click -> {
            addNewGrid(gridNameInputField.getValue());
            gridNameInputField.clear();
        });

        addToClickableComponents(gridNameInputField, addNewGridButton);
        addNewGridButton.setWidth("10%");

        controlSublayout.add(gridNameInputField, addNewGridButton);
        controlSublayout.setAlignItems(Alignment.END);

        totalWithDiscountString.setVisible(false);
        add(controlSublayout, totalString, totalWithDiscountString, includingVatValue, createFinishControlElements());
        return layout;
    }

    private HorizontalLayout createFinishControlElements() {
        IntegerField discountField = new IntegerField();
        Button showDiscountFieldButton = new Button("%");
        IntegerField vatField = new IntegerField();
        Button showVatFieldButton = new Button(VaadinIcon.PIGGY_BANK_COIN.create());
        Button showCurrencyComboButton = new Button(VaadinIcon.DOLLAR.create());
        ComboBox<String> currencyCombo = new ComboBox<>("Currency", "EUR", "USD", "RUB", "JPY");
        Button postQuote = new Button("Save to DB");
        Button dlButt = new Button("Download .xlsx");
        FileDownloadWrapper wrapper = new FileDownloadWrapper(
                new StreamResource("error", () -> new ByteArrayInputStream(new byte[]{}))
        );

        addToClickableComponents(discountField, vatField, postQuote, currencyCombo);

        discountField.setValue(0);
        discountField.setVisible(false);
        discountField.setHasControls(true);
        discountField.setMin(0);
        discountField.setMax(99);
        discountField.setLabel("Discount, %");
        discountField.addValueChangeListener(c -> {
            discount = c.getValue();
            refreshTotal();
        });
        showDiscountFieldButton.addClickListener(c -> discountField.setVisible(!discountField.isVisible()));

        vatField.setValue(DEFAULT_VAT);
        vatField.setVisible(false);
        vatField.setHasControls(true);
        vatField.setMin(0);
        vatField.setMax(99);
        vatField.setLabel("Vat, %");
        vatField.addValueChangeListener(c -> {
            vat = c.getValue();
            refreshTotal();
        });
        showVatFieldButton.addClickListener(c -> vatField.setVisible(!vatField.isVisible()));

        currencyCombo.setValue(currency);
        currencyCombo.setVisible(false);
        currencyCombo.addValueChangeListener(event -> {
            currency = event.getValue();
            refreshAll();
            fireEvent(new UniversalSectionChangedEvent(this));
        });
        showCurrencyComboButton.addClickListener(c -> currencyCombo.setVisible(!currencyCombo.isVisible()));

        wrapper.wrapComponent(dlButt);
        wrapper.setVisible(false);

        detailsBinder.bind(discountField, QuoteDetails::getDiscount, QuoteDetails::setDiscount);
        detailsBinder.bind(vatField, QuoteDetails::getVat, QuoteDetails::setVat);

        HorizontalLayout layout = new HorizontalLayout(
                discountField, showDiscountFieldButton, vatField, showVatFieldButton,
                currencyCombo, showCurrencyComboButton, postQuote, wrapper);

        layout.setAlignItems(Alignment.END);
        postQuote.addClickListener(click -> {
            int id = postToDbAndGetID();
            byte[] bytes = downloadService.downloadXlsx(id);
            wrapper.setResource(new StreamResource(UUID.nameUUIDFromBytes(bytes).toString() + ".xlsx",
                    () -> new ByteArrayInputStream(bytes)));
            wrapper.setVisible(true);

            disableClickableComponents();
        });
        return layout;
    }

    private int postToDbAndGetID() {
        try {
            QuoteDetails quoteDetails = new QuoteDetails();
            detailsBinder.writeBean(quoteDetails);
            quoteDetails.setFinalPrice(getTotalMoney().multiply((100.0 - discount) / 100));
            List<QuoteSection> sections = gridList.stream().map(SectionGrid::getQuoteSection).collect(Collectors.toList());
            return assembler.assembleAndPostNew(quoteDetails, sections);
        } catch (ValidationException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private void addNewGrid(String name) {
        Accordion accordion = new Accordion();
        accordion.setWidthFull();
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        SectionGrid sg = new SectionGrid(name, currencyFormatter);

        sg.addListener(ComposeDeleteItemPositionEvent.class, this::itemPositionDeleted);
        sg.addListener(ComposeItemPositionQuantityEvent.class, this::itemPositionQuantityChanged);
        addListener(UniversalSectionChangedEvent.class, sg::sectionChangedEvent);

        gridList.add(sg);
        resetAvailableGridsCombobox();

        layout.add(getGridHeaderPanel(accordion, name, sg), sg, sg.getFooter());
        accordion.add(name, layout);
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
            sectionHandler.setSectionName(grid.getQuoteSection(), event.getValue());
            fireEvent(new UniversalSectionChangedEvent(this));
            resetAvailableGridsCombobox();
            acc.getOpenedPanel().ifPresent(panel -> panel.setSummary(new Span(event.getValue())));
        });

        Button editNameBtn = new Button(VaadinIcon.EDIT.create());
        editNameBtn.addClickListener(c -> nameField.setVisible(!nameField.isVisible()));

        IntegerField discountField = new IntegerField();
        discountField.setValue(0);
        discountField.setVisible(false);
        discountField.setHasControls(true);
        discountField.setMin(0);
        discountField.setMax(100);
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
