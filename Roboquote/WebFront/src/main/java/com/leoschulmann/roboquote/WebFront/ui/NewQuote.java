package com.leoschulmann.roboquote.WebFront.ui;

import com.leoschulmann.roboquote.WebFront.components.*;
import com.leoschulmann.roboquote.WebFront.events.*;
import com.leoschulmann.roboquote.WebFront.ui.bits.*;
import com.leoschulmann.roboquote.itemservice.entities.Bundle;
import com.leoschulmann.roboquote.quoteservice.entities.ItemPosition;
import com.leoschulmann.roboquote.quoteservice.entities.Quote;
import com.leoschulmann.roboquote.quoteservice.entities.QuoteSection;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;
import org.javamoney.moneta.Money;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Route(value = "new", layout = MainLayout.class)
public class NewQuote extends VerticalLayout implements AfterNavigationObserver {

    private final CurrencyFormatService currencyFormatter;
    private final CurrencyRatesService currencyRatesService;
    private final QuoteSectionHandler sectionHandler;
    private final DownloadService downloadService;
    private final QuoteService quoteService;
    private final StringFormattingService stringFormattingService;
    private final MoneyMathService moneyMathService;
    private final ItemCachingService cachingService;
    private final HttpRestService httpRestService;
    private final ConverterService converterService;
    private final GridsBlock gridsBlock;
    private final Binder<Quote> quoteBinder = new Binder<>(Quote.class);
    private Integer discount = 0;
    private Integer vat = 20;

    private String currency = "EUR";
    private BigDecimal euroRate;
    private BigDecimal dollarRate;
    private BigDecimal yenRate;
    private double exchangeConversionFee;

    public NewQuote(CurrencyFormatService currencyFormatter, CurrencyRatesService currencyRatesService, QuoteSectionHandler sectionHandler, DownloadService downloadService, QuoteService quoteService, StringFormattingService stringFormattingService, MoneyMathService moneyMathService, ItemCachingService cachingService, HttpRestService httpRestService, ConverterService converterService) {
        this.currencyFormatter = currencyFormatter;
        this.currencyRatesService = currencyRatesService;
        this.sectionHandler = sectionHandler;
        this.downloadService = downloadService;
        this.quoteService = quoteService;
        this.stringFormattingService = stringFormattingService;
        this.moneyMathService = moneyMathService;
        this.cachingService = cachingService;
        this.httpRestService = httpRestService;
        this.converterService = converterService;

        gridsBlock = new GridsBlock();

        add(createInventoryLookup());
        add(createQuoteInfoBlock());
        add(gridsBlock);
        add(createFinishBlock());
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        getUI().ifPresent(ui -> {
            Quote quote = Objects.requireNonNullElseGet(ui.getSession().getAttribute(Quote.class),
                    () -> {
                        //empty quote with default rates, vat, discount and one empty quote section
                        Quote q = new Quote(0, 20, BigDecimal.valueOf(100), BigDecimal.valueOf(100),
                                BigDecimal.ONE, BigDecimal.valueOf(2));
                        q.addSections(new QuoteSection("New quote section"));
                        return q;
                    });

            ui.getSession().setAttribute(Quote.class, null); //delete session payload if any
            quoteBinder.readBean(quote);
            quote.getSections().forEach(this::addNewGrid);
            resetAvailableGridsCombobox();
        });
    }

    private InventoryLookup createInventoryLookup() {
        InventoryLookup lookup = new InventoryLookup(gridsBlock.getGridsAsList(), cachingService.getItemsFromCache());
        lookup.addListener(InventoryLookupAddClickedEvent.class, e -> {
            ItemPosition ip = converterService.convertItemToItemPosition(e.getItem());
            QuoteSection qs = e.getGrid().getQuoteSection();
            sectionHandler.putToSection(qs, ip);
            refreshSectionSubtotal(currency, qs);
            fireEvent(new UpdateGridEvent(this)); //todo
            refreshTotal();
        });

        lookup.addListener(InventoryLookupRefreshButtonEvent.class, e -> {
            System.err.println(e.getClass().getSimpleName());

            cachingService.updateCache();
            lookup.setItems(cachingService.getItemsFromCache());
        });

        addListener(UpdateAvailableGridsEvent.class, e -> {
            System.err.println(e.getClass().getSimpleName());

            lookup.updateGrids(gridsBlock.getGridsAsList());
        });

        return lookup;
    }

    //todo move to bits package as class
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
        TextField comment = new TextField("Comment");

        DatePicker validThru = new DatePicker("Valid through date");
        validThru.setValue(LocalDate.now().plus(3, ChronoUnit.MONTHS));

        //todo add to clickables
//        addToClickableComponents(validThru, warranty, customer, customerInfo, dealer,
//                dealerInfo, paymentTerms, shippingTerms, installation);
//
        columnLayout.add(customer);
        columnLayout.add(customerInfo, 2);
        columnLayout.add(dealer);
        columnLayout.add(dealerInfo, 2);
        columnLayout.add(paymentTerms, shippingTerms, warranty, installation, validThru, comment);
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
        quoteBinder.bind(comment, Quote::getComment, Quote::setComment);
        quoteBinder.forField(validThru).asRequired().bind(Quote::getValidThru, Quote::setValidThru);

        Accordion accordion = new Accordion();
        accordion.setWidthFull();
        accordion.add("Quote details", columnLayout);
        return accordion;
    }

    private RatesPanel createRatesBlock() {
        RatesPanel ratesPanel = new RatesPanel();

        addListener(RatesUpdatedEvent.class, e -> {
            ratesPanel.updateUsd(e);
            ratesPanel.updateEur(e);
            ratesPanel.updateJpy(e);
        });

        addListener(DisableClickableComponents.class, ratesPanel::disable);

        ratesPanel.addListener(RatesPanelUpdateClickedEvent.class, e -> {
            euroRate = currencyRatesService.getRubEurRate();
            dollarRate = currencyRatesService.getRubUSDRate();
            yenRate = currencyRatesService.getRubJPYRate();
            fireEvent(new RatesUpdatedEvent(this));
        });

        ratesPanel.addListener(EuroFieldChangedEvent.class, e -> euroRate = e.getSource().getValue());
        ratesPanel.addListener(DollarFieldChangedEvent.class, e -> dollarRate = e.getSource().getValue());
        ratesPanel.addListener(YenFieldChangedEvent.class, e -> yenRate = e.getSource().getValue());
        ratesPanel.addListener(ExchangeRateFieldChangedEvent.class, e -> exchangeConversionFee = e.getSource().getValue());
        //todo update totals?

        fireEvent(new RatesUpdatedEvent(this));

        quoteBinder.forField(ratesPanel.getConversionRate()).asRequired().bind(quote -> quote.getConversionRate().doubleValue(),
                (quote1, conversionRate1) -> quote1.setConversionRate(BigDecimal.valueOf(conversionRate1)));
        quoteBinder.forField(ratesPanel.getEuro()).asRequired().bind(Quote::getEurRate, Quote::setEurRate);
        quoteBinder.forField(ratesPanel.getDollar()).asRequired().bind(Quote::getUsdRate, Quote::setUsdRate);
        quoteBinder.forField(ratesPanel.getYen()).asRequired().bind(Quote::getJpyRate, Quote::setJpyRate);

        return ratesPanel;
    }

    private FinishBlock createFinishBlock() {
        FinishBlock finishBlock = new FinishBlock(currency, discount, vat);

        addListener(GlobalDiscountEvent.class, event -> {
            System.err.println(event.getClass().getSimpleName());

            discount = event.getSource().getDiscountField().getValue();

            if (discount < 0) event.getSource().getDiscountField().setLabel("Markup, %");
            else event.getSource().getDiscountField().setLabel("Discount, %");
//            refreshTotal();//todo ??
        });

        addListener(VatEvent.class, e -> {
            System.err.println(e.getClass().getSimpleName());

            vat = e.getSource().getVatField().getValue();
            e.getSource().getIncludingVatValue().setText(stringFormattingService.getVat(getTotalMoney(), discount, vat));
//            refreshTotal();//todo ??
        });

        addListener(CurrencyChangedEvent.class, e -> {
            System.err.println(e.getClass().getSimpleName());

            currency = e.getSource().getCurrencyCombo().getValue();
            MonetaryAmount ma = getTotalMoney();
            e.getSource().getTotalString().setText(stringFormattingService.getCombined(ma));
            e.getSource().getTotalWithDiscountString().setText(stringFormattingService.getCombinedWithDiscountOrMarkup(ma, discount));
//            refreshAll(); //todo ???
//            fireEvent(new UniversalSectionChangedEvent(this));
        });

        addListener(FinishBlockAddSectionClickedEvent.class, e -> {
            System.err.println(e.getClass().getSimpleName());

            showNewSectionDialog();});

        addListener(FinishBlockSaveClickedEvent.class, e -> {
            System.err.println(e.getClass().getSimpleName());

            if (quoteBinder.validate().isOk() && gridsNotEmpty()) {
                int id = postToDbAndGetID();  //persisting starts here
                fireEvent(new QuotePersistedEvent(this,
                        httpRestService.getFullName(id) + downloadService.getExtension(),
                        downloadService.downloadXlsx(id)));
                fireEvent(new DisableClickableComponents(this));
            } else {
                List<String> msg = new ArrayList<>();
                if (quoteBinder.validate().hasErrors()) msg.add("Please fill marked fields");
                if (!gridsNotEmpty()) msg.add("Some sections are empty");
                String[] arr = new String[msg.size()];

                new ErrorDialog(msg.toArray(arr)).open();
            }
        });

        addListener(RefreshTotalEvent.class, e -> {
            MonetaryAmount am = getTotalMoney();
            finishBlock.getTotalString().setText(stringFormattingService.getCombined(am));
            finishBlock.getTotalWithDiscountString().setText(stringFormattingService.getCombinedWithDiscountOrMarkup(am, discount));
            finishBlock.getIncludingVatValue().setText(stringFormattingService.getVat(am, discount, vat));

            finishBlock.getTotalWithDiscountString().setVisible(discount != 0);

            if (discount == 0) {
                finishBlock.getTotalString().getElement().getStyle().set("font-weight", "bold").remove("text-decoration");
            } else {
                finishBlock.getTotalString().getElement().getStyle().set("text-decoration", "line-through").remove("font-weight");
            }
        });

        quoteBinder.forField(finishBlock.getDiscountField()).asRequired().bind(Quote::getDiscount, Quote::setDiscount);
        quoteBinder.forField(finishBlock.getVatField()).asRequired().bind(Quote::getVat, Quote::setVat);

        return finishBlock;
    }

    private void refreshTotal() {
        fireEvent(new RefreshTotalEvent(this));
    }

    private void refreshSectionSubtotal(String currency, QuoteSection qs) {
        sectionHandler.updateSubtotalToCurrency(qs, currency,
                euroRate, dollarRate, yenRate, exchangeConversionFee);
    }

//    private void refreshAll() {
//        gridList.stream().map(SectionGrid::getQuoteSection)
//                .forEach(qs -> refreshSectionSubtotal(currency, qs));
//        refreshTotal();
//    }

    private MonetaryAmount getTotalMoney() {
        return moneyMathService.getSum(gridsBlock.getGridsAsList().stream()
                .map(grid -> grid.getQuoteSection().getTotalDiscounted())
                .collect(Collectors.toList()));
    }

    private void showNewSectionDialog() {
        VerticalLayout addEmpty;
        VerticalLayout addBundle;

        TextField tf = new TextField();
        Button addEmptySecBtn = new Button("Add empty section");
        tf.setWidth("32em");
        tf.setClearButtonVisible(true);
        addEmptySecBtn.setEnabled(false);
        addEmptySecBtn.setWidth("32em");
        addEmptySecBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        tf.addValueChangeListener(event -> addEmptySecBtn.setEnabled(!event.getValue().isBlank()));
        addEmpty = new VerticalLayout(tf, addEmptySecBtn);

        ComboBox<Bundle> bundles = new ComboBox<>();
        bundles.setItems(httpRestService.getAllBundlesNamesAndIds());
        bundles.setAllowCustomValue(false);
        bundles.setClearButtonVisible(true);
        bundles.setWidth("32em");
        Button addBundleBtn = new Button("Add bundle");
        addBundleBtn.setEnabled(false);
        addBundleBtn.setWidth("32em");
        addBundleBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
        bundles.addValueChangeListener(event -> addBundleBtn.setEnabled(!(event.getValue() == null)));
        addBundle = new VerticalLayout(bundles, addBundleBtn);

        FormLayout layout = new FormLayout(addEmpty, addBundle);
        layout.setResponsiveSteps(new ResponsiveStep("1em", 1),
                new ResponsiveStep("65em", 2));
        Dialog dialog = new Dialog(layout);

        addEmptySecBtn.addClickListener(click -> {
            QuoteSection qs = new QuoteSection(tf.getValue().trim());
            addNewGrid(qs);
            dialog.close();
        });

        addBundleBtn.addClickListener(bang -> {
            int id = bundles.getValue().getId();
            Bundle bundle = httpRestService.getBundleById(id);
            QuoteSection qs = new QuoteSection(bundle.getNameRus()); //todo i8n
            addNewGrid(qs);
            bundle.getPositions().stream()
                    .map(converterService::convertBundledPositionToItemPosition)
                    .forEach(ip -> sectionHandler.putToSection(qs, ip));

            refreshSectionSubtotal(currency, qs);
//            fireEvent(new UniversalSectionChangedEvent(this));
            refreshTotal();

            dialog.close();
        });
        dialog.open();
    }

    private boolean gridsNotEmpty() {
        return gridsBlock.getGridsAsList().stream().noneMatch(s -> s.getQuoteSection().getPositions().size() == 0);
    }

    private int postToDbAndGetID() {
        try {
            Quote quote = new Quote();
            quoteBinder.writeBean(quote);
            //todo write all sections
            quote.setFinalPrice((Money) getTotalMoney().multiply((100.0 - discount) / 100));
            return httpRestService.postNew(quote);
        } catch (ValidationException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private void addNewGrid(QuoteSection quoteSection) {
        SectionAccordion sectionAccordion = new SectionAccordion(quoteSection, stringFormattingService);

        sectionAccordion.getControl().addListener(AccordionEditNameClickedEvent.class, e -> {
            System.err.println(e.getClass().getSimpleName());

            TextField tf = new TextField();
            tf.setValue(quoteSection.getName());
            Button addBtn = new Button("Set");
            addBtn.setEnabled(false);
            tf.setWidth("32em");
            tf.addValueChangeListener(event -> addBtn.setEnabled(!event.getValue().isBlank()));
            addBtn.setWidthFull();
            addBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            Dialog dialog = new Dialog(new VerticalLayout(tf, addBtn));
            addBtn.addClickListener(click -> {
                        quoteSection.setName(tf.getValue().trim());
                        sectionAccordion.refreshName();
//                                fireEvent(new UniversalSectionChangedEvent(this));
                        resetAvailableGridsCombobox();
                        dialog.close();
                    }
            );
            dialog.open();
        });

        sectionAccordion.getControl().addListener(AccordionDiscountChangedEvent.class, e -> {
            System.err.println(e.getClass().getSimpleName());

            quoteSection.setDiscount(e.getSource().getDiscountField().getValue());
            refreshSectionSubtotal(currency, quoteSection);
            refreshTotal(); //todo check
//            fireEvent(new UniversalSectionChangedEvent(this));
        });

        sectionAccordion.getControl().addListener(AccordionDeleteSectionEvent.class, e -> {
            System.err.println(e.getClass().getSimpleName());

            gridsBlock.removeGrid(sectionAccordion);
            resetAvailableGridsCombobox();
            refreshTotal();
        });

        sectionAccordion.getControl().addListener(AccordionMoveUpEvent.class, e -> {
            System.err.println(e.getClass().getSimpleName());

            int fromIndex = gridsBlock.indexOf(sectionAccordion);
            int toIndex = fromIndex - 1;
            if (toIndex >= 0) {
                gridsBlock.moveAccordion(sectionAccordion, toIndex);
            } else new ErrorDialog("Can't move up").open();
            // resetAvailableGridsCombobox();
        });

        sectionAccordion.getControl().addListener(AccordionMoveDownEvent.class, e -> {
            System.err.println(e.getClass().getSimpleName());

            int fromIndex = gridsBlock.indexOf(sectionAccordion);
            int toIndex = fromIndex + 1;
            if (toIndex < gridsBlock.getComponentCount()) {
                gridsBlock.moveAccordion(sectionAccordion, toIndex);
            } else new ErrorDialog("Can't move down").open();
            //          resetAvailableGridsCombobox();
        });

        sectionAccordion.getGrid().addListener(GridChangedEvent.class, e -> {
            System.err.println(e.getClass().getSimpleName());

            refreshSectionSubtotal(currency, quoteSection);
        });

        addListener(UpdateGridEvent.class, event -> sectionAccordion.getGrid().update(event));

        gridsBlock.add(sectionAccordion);
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

    private void resetAvailableGridsCombobox() {
        fireEvent(new UpdateAvailableGridsEvent(this));
    }

    private void onComponentEvent(RatesPanelUpdateClickedEvent event) {
        euroRate = currencyRatesService.getRubEurRate();
        dollarRate = currencyRatesService.getRubUSDRate();
        yenRate = currencyRatesService.getRubJPYRate();
        fireEvent(new RatesUpdatedEvent(this));
        //todo update totals??
    }
}
