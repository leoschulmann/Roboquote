package com.leoschulmann.roboquote.WebFront.ui;

import com.leoschulmann.roboquote.WebFront.components.*;
import com.leoschulmann.roboquote.WebFront.pojo.QuoteDetails;
import com.leoschulmann.roboquote.itemservice.entities.Item;
import com.leoschulmann.roboquote.quoteservice.entities.ItemPosition;
import com.leoschulmann.roboquote.quoteservice.entities.QuoteSection;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.javamoney.moneta.Money;
import org.javamoney.moneta.function.MonetaryFunctions;
import org.vaadin.olli.FileDownloadWrapper;

import javax.money.MonetaryAmount;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Route(value = "compose", layout = MainLayout.class)
public class Compose extends VerticalLayout {
    private ItemService itemService;
    private CurrencyFormatService currencyFormatter;
    private InventoryItemToItemPositionConverter converter;
    private QuoteSectionHandler sectionHandler;
    private QuoteAssembler assembler;
    private DownloadService downloadService;

    private VerticalLayout gridsBlock;
    private List<SectionGrid> gridList;
    private ComboBox<SectionGrid> avaiableGridsBox;
    private Binder<QuoteDetails> detailsBinder = new Binder<>(QuoteDetails.class);
    private final H4 totalString = new H4();
    private final H4 totalWithDiscountString = new H4();
    private Integer discount = 0;


    static final String DEFAULT_SECTION_NAME = "New quote section";

    public Compose(ItemService itemService,
                   CurrencyFormatService currencyFormatter,
                   InventoryItemToItemPositionConverter converter,
                   QuoteSectionHandler sectionHandler,
                   QuoteAssembler assembler, DownloadService downloadService) {

        this.itemService = itemService;
        this.currencyFormatter = currencyFormatter;
        this.converter = converter;
        this.sectionHandler = sectionHandler;
        this.assembler = assembler;
        this.downloadService = downloadService;

        gridList = new ArrayList<>();
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
        validThru.setValue(LocalDate.now().plus(3, ChronoUnit.MONTHS));

        columnLayout.add(customer);
        columnLayout.add(customerInfo, 2);
        columnLayout.add(dealer);
        columnLayout.add(dealerInfo, 2);
        columnLayout.add(paymentTerms, shippingTerms, warranty, validThru);
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

    private Accordion getInventoryLookupAccordeon() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        ComboBox<Item> searchBox = getItemComboBox();
        avaiableGridsBox = new ComboBox<>();
        avaiableGridsBox.setItems(gridList);
        Button addToGridBtn = new Button(VaadinIcon.PLUS.create());
        addToGridBtn.addClickListener(click -> {
            if (searchBox.getValue() != null && avaiableGridsBox.getValue() != null) {
                ItemPosition ip = converter.convert(searchBox.getValue());
                sectionHandler.putToSection(avaiableGridsBox.getValue().getQuoteSection(), ip);
                avaiableGridsBox.getValue().renderItems();
                avaiableGridsBox.getValue().refreshTotals();
                refreshTotal();
            }
        });
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
        totalString.setText("TOTAL " + currencyFormatter.formatMoney(getTotalMoney()));
        totalWithDiscountString.setText("TOTAL (discounted " + discount + "%) "
                + currencyFormatter.formatMoney(getTotalMoney().multiply((100.0 - discount) / 100)));
        totalWithDiscountString.setVisible(discount > 0);
    }

    private MonetaryAmount getTotalMoney() {
        return gridList.stream()
                .map(gr -> gr.getQuoteSection().getTotalDiscounted())
                .reduce(MonetaryFunctions.sum())
                .orElseGet(() -> Money.of(0, "EUR"));
    }

    private VerticalLayout createGridsBlock() {
        VerticalLayout layout = new VerticalLayout();
        return layout;
    }

    private VerticalLayout createFinishBlock() {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        HorizontalLayout controlSublayout = new HorizontalLayout();
        controlSublayout.setWidthFull();
        TextField gridNameInputField = new TextField();  //todo add validation (non-empty, non-duplicating, etc)
        gridNameInputField.setPlaceholder("Add new section ");
        Button addNewGridButton = new Button(VaadinIcon.PLUS.create());
        addNewGridButton.addClickListener(click -> {
            addNewGrid(gridNameInputField.getValue());
            gridNameInputField.clear();
        });
        gridNameInputField.setWidth("50%");
        addNewGridButton.setWidth("10%");
        controlSublayout.add(gridNameInputField, addNewGridButton);
        controlSublayout.setAlignItems(Alignment.END);

        totalWithDiscountString.setVisible(false);
        add(controlSublayout, totalString, totalWithDiscountString, createFinishingControls());
        return layout;
    }

    private HorizontalLayout createFinishingControls() {
        Button postQuote = new Button("Save to DB");

        IntegerField discountField = new IntegerField();
        discountField.setValue(0);
        discountField.setVisible(false);
        discountField.setHasControls(true);
        discountField.setMin(0);
        discountField.setMax(99);
        discountField.addValueChangeListener(c -> {
            discount = c.getValue();
            refreshTotal();
        });

        detailsBinder.bind(discountField, QuoteDetails::getDiscount, QuoteDetails::setDiscount);

        Button showDiscountFieldButton = new Button("%");
        Button dlButt = new Button("Download .xlsx");
        FileDownloadWrapper wrapper = new FileDownloadWrapper(
                new StreamResource("error", () -> new ByteArrayInputStream(new byte[]{}))
        );
        wrapper.wrapComponent(dlButt);
        wrapper.setVisible(false);

        showDiscountFieldButton.addClickListener(c -> discountField.setVisible(!discountField.isVisible()));
        HorizontalLayout layout = new HorizontalLayout(discountField, showDiscountFieldButton, postQuote, wrapper);

        QuoteDetails qd = new QuoteDetails();
        postQuote.addClickListener(click -> {
            try {
                detailsBinder.writeBean(qd);
                List<QuoteSection> sections = gridList.stream().map(SectionGrid::getQuoteSection).collect(Collectors.toList());
                int id = assembler.assembleAndPostNew(qd, sections);
                byte[] bytes = downloadService.downloadXlsx(id);
                wrapper.setResource(new StreamResource(UUID.nameUUIDFromBytes(bytes).toString() + ".xlsx",
                        () -> new ByteArrayInputStream(bytes)));
                wrapper.setVisible(true);

            } catch (ValidationException e) {
                e.printStackTrace();
            }
        });
        return layout;
    }

    private void addNewGrid(String name) {
        Accordion accordion = new Accordion();
        accordion.setWidthFull();
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        SectionGrid sg = new SectionGrid(name, currencyFormatter);
        gridList.add(sg);
        avaiableGridsBox.setItems(gridList);

        layout.add(getGridHeaderPanel(accordion, sg), sg, sg.getFooter());
        accordion.add(name, layout);
        gridsBlock.add(accordion);
        sg.refreshTotals();
    }

    private HorizontalLayout getGridHeaderPanel(Accordion acc, SectionGrid grid) {
        HorizontalLayout head = new HorizontalLayout();
        head.setWidthFull();
        TextField nameField = new TextField();
        nameField.setWidth("50%");
        nameField.setValue(grid.getName());
        nameField.setVisible(false);

        nameField.addValueChangeListener(event -> {
            grid.setName(event.getValue());
            grid.refreshTotals();
            avaiableGridsBox.setItems(gridList);
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
            grid.getQuoteSection().setDiscount(c.getValue());
            grid.refreshTotals();
            refreshTotal();
        });


        Button discountBtn = new Button("%");
        discountBtn.addClickListener(c -> discountField.setVisible(!discountField.isVisible()));

        Button currencyBtn = new Button(VaadinIcon.DOLLAR.create()); //todo implement

        Button deleteBtn = new Button(VaadinIcon.TRASH.create());
        deleteBtn.addClickListener(c -> {
            gridsBlock.remove(acc);
            gridList.remove(grid);
            avaiableGridsBox.setItems(gridList); //todo refactor as method
            refreshTotal();
        });

        head.add(nameField, editNameBtn, discountField, discountBtn, currencyBtn, deleteBtn);

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
}
