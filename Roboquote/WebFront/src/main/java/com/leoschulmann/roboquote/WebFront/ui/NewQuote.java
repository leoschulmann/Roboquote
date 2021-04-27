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
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValidationResult;
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

    private final CurrencyRatesService currencyRatesService;
    private final QuoteSectionHandler sectionHandler;
    private final DownloadService downloadService;
    private final StringFormattingService stringFormattingService;
    private final MoneyMathService moneyMathService;
    private final CachingService cachingService;
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

    public NewQuote(CurrencyRatesService currencyRatesService,
                    QuoteSectionHandler sectionHandler, DownloadService downloadService,
                    StringFormattingService stringFormattingService,
                    MoneyMathService moneyMathService, CachingService cachingService,
                    HttpRestService httpRestService, ConverterService converterService) {
        this.currencyRatesService = currencyRatesService;
        this.sectionHandler = sectionHandler;
        this.downloadService = downloadService;
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
                        q.setValidThru(LocalDate.now().plus(3, ChronoUnit.MONTHS));
                        q.addSections(new QuoteSection("New quote section"));
                        return q;
                    });

            ui.getSession().setAttribute(Quote.class, null); //delete session payload if any
            quoteBinder.readBean(quote);
            quote.getSections().forEach(this::addNewGrid);
            recalculateAndRedrawTotalsAndSubtotals();
        });
    }

    private InventoryLookup createInventoryLookup() {
        InventoryLookup lookup = new InventoryLookup(gridsBlock.getGridsAsList(), cachingService.getItemsFromCache());
        addListener(DisableClickableComponents.class, lookup::disable);

        lookup.addListener(InventoryLookupAddClickedEvent.class, e -> {
            ItemPosition ip = converterService.convertItemToItemPosition(e.getItem());
            QuoteSection qs = e.getGrid().getQuoteSection();
            sectionHandler.putToSection(qs, ip);
            recalculateSectionSubtotal(currency, qs);
            fireEvent(new RedrawGridAndSubtotalsEvent(this));
            fireEvent(new RecalculateAndRedrawTotalEvent(this));
        });

        lookup.addListener(RefreshButtonEvent.class, e -> {
            fireEvent(new RefreshCachesEvent(this));
        });

        addListener(RefreshCachesEvent.class, e -> {
            cachingService.updateItemCache();
            lookup.setItems(cachingService.getItemsFromCache());
        });

        addListener(UpdateAvailableGridsEvent.class, e -> lookup.updateGrids(gridsBlock.getGridsAsList()));

        return lookup;
    }

    private InfoAccordion createQuoteInfoBlock() {
        InfoAccordion acc = new InfoAccordion();
        cachingService.updateTermsCache();
        acc.getInstallation().setItems(cachingService.getDistinctInstallationTerms());
        acc.getPaymentTerms().setItems(cachingService.getDistinctPaymentTerms());
        acc.getShippingTerms().setItems(cachingService.getDistinctShippingTerms());
        acc.getWarranty().setItems(cachingService.getDistinctwarrantyTerms());

        addListener(DisableClickableComponents.class, acc::disable);

        addListener(RefreshCachesEvent.class, e -> {
            cachingService.updateTermsCache();
            acc.getInstallation().setItems(cachingService.getDistinctInstallationTerms());
            acc.getPaymentTerms().setItems(cachingService.getDistinctPaymentTerms());
            acc.getShippingTerms().setItems(cachingService.getDistinctShippingTerms());
            acc.getWarranty().setItems(cachingService.getDistinctwarrantyTerms());
        });

        acc.addRatesBlock(createRatesBlock());

        quoteBinder.forField(acc.getCustomer())
                .withValidator(s -> s.length() > 2 && s.length() < 255, "Invalid customer name length")
                .asRequired().bind(Quote::getCustomer, Quote::setCustomer);

        quoteBinder.forField(acc.getCustomerInfo())
                .withValidator(s -> s.length() < 255, "Customer info is too long")
                .bind(Quote::getCustomerInfo, Quote::setCustomerInfo);

        quoteBinder.forField(acc.getDealer())
                .withValidator(s -> s.length() < 255, "Dealer name is too long")
                .bind(Quote::getDealer, Quote::setDealer);

        quoteBinder.forField(acc.getDealerInfo())
                .withValidator(s -> s.length() < 255, "Dealer info is too long")
                .bind(Quote::getDealerInfo, Quote::setDealerInfo);

        quoteBinder.forField(acc.getPaymentTerms())
                .withValidator(s -> s.length() > 2 && s.length() < 255, "Invalid payment terms length")
                .asRequired().bind(Quote::getPaymentTerms, Quote::setPaymentTerms);

        quoteBinder.forField(acc.getShippingTerms())
                .withValidator(s -> s.length() > 2 && s.length() < 255, "Invalid shipping terms length")
                .asRequired().bind(Quote::getShippingTerms, Quote::setShippingTerms);

        quoteBinder.forField(acc.getWarranty())
                .withValidator(s -> s.length() > 2 && s.length() < 255, "Invalid warranty length")
                .asRequired().bind(Quote::getWarranty, Quote::setWarranty);

        quoteBinder.forField(acc.getInstallation())
                .withValidator(s -> s.length() > 2 && s.length() < 255, "Invalid installation length")
                .asRequired().bind(Quote::getInstallation, Quote::setInstallation);

        quoteBinder.forField(acc.getComment()).withValidator(s -> s.length() < 255, "Comment is too long")
                .bind(Quote::getComment, Quote::setComment);

        quoteBinder.forField(acc.getValidThru()).asRequired().bind(Quote::getValidThru, Quote::setValidThru);
        return acc;
    }

    private RatesPanel createRatesBlock() {
        RatesPanel ratesPanel = new RatesPanel();

        ratesPanel.addListener(RatesPanelUpdateClickedEvent.class, e ->
                ratesPanel.updateAllCurrencies(currencyRatesService.getRubEurRate(),
                        currencyRatesService.getRubUSDRate(), currencyRatesService.getRubJPYRate()));

        ratesPanel.addListener(EuroFieldChangedEvent.class, e -> {
            euroRate = e.getSource().getValue();
            recalculateAndRedrawTotalsAndSubtotals();
        });

        ratesPanel.addListener(DollarFieldChangedEvent.class, e -> {
            dollarRate = e.getSource().getValue();
            recalculateAndRedrawTotalsAndSubtotals();

        });
        ratesPanel.addListener(YenFieldChangedEvent.class, e -> {
            yenRate = e.getSource().getValue();
            recalculateAndRedrawTotalsAndSubtotals();

        });
        ratesPanel.addListener(ExchangeRateFieldChangedEvent.class, e -> {
            exchangeConversionFee = e.getSource().getValue();
            recalculateAndRedrawTotalsAndSubtotals();
        });

        fireEvent(new RatesUpdatedEvent(this));

        quoteBinder.forField(ratesPanel.getConversionRate())
                .withValidator(value -> value >= -99 && value <= 99, "Bad conversion rate")
                .asRequired().bind(quote -> quote.getConversionRate().doubleValue(),
                (quote1, conversionRate1) -> quote1.setConversionRate(BigDecimal.valueOf(conversionRate1)));

        quoteBinder.forField(ratesPanel.getEuro())
                .withValidator(value -> value.compareTo(BigDecimal.ONE) > 0 && value.compareTo(BigDecimal.valueOf(1000)) < 0,
                        "Bad Euro rate").asRequired().bind(Quote::getEurRate, Quote::setEurRate);

        quoteBinder.forField(ratesPanel.getDollar())
                .withValidator(value -> value.compareTo(BigDecimal.ONE) > 0 && value.compareTo(BigDecimal.valueOf(1000)) < 0,
                        "Bad USD rate").asRequired().bind(Quote::getUsdRate, Quote::setUsdRate);

        quoteBinder.forField(ratesPanel.getYen())
                .withValidator(value -> value.compareTo(BigDecimal.valueOf(0.01)) > 0 && value.compareTo(BigDecimal.valueOf(10)) < 0,
                        "Bad JPY rate").asRequired().bind(Quote::getJpyRate, Quote::setJpyRate);

        return ratesPanel;
    }

    private FinishBlock createFinishBlock() {
        FinishBlock finishBlock = new FinishBlock(currency, discount, vat);
        addListener(DisableClickableComponents.class, finishBlock::disable);

        AddSectionDialog dialog = new AddSectionDialog(httpRestService.getAllBundlesNamesAndIds());

        finishBlock.addListener(GlobalDiscountEvent.class, event -> {
            discount = event.getSource().getDiscountField().getValue();

            if (discount < 0) event.getSource().getDiscountField().setLabel("Markup, %");
            else event.getSource().getDiscountField().setLabel("Discount, %");

            fireEvent(new RecalculateAndRedrawTotalEvent(this));
        });

        finishBlock.addListener(VatEvent.class, e -> {
            vat = e.getSource().getVatField().getValue();
            fireEvent(new RecalculateAndRedrawTotalEvent(this));
        });

        finishBlock.addListener(CurrencyChangedEvent.class, e -> {
            currency = e.getSource().getCurrencyCombo().getValue();
            recalculateAndRedrawTotalsAndSubtotals();
        });

        finishBlock.addListener(FinishBlockAddSectionClickedEvent.class, e -> dialog.open());

        dialog.addListener(SectionDialogAddEmptySectionButtonClicked.class, e -> {
            String name = e.getName();
            QuoteSection quoteSection = new QuoteSection(name);
            addNewGrid(quoteSection);
            dialog.close();
        });

        dialog.addListener(SectionDialogAddBundledSectionButtonClicked.class, e -> {
            int id = e.getId();
            Bundle bundle = httpRestService.getBundleById(id);
            QuoteSection qs = new QuoteSection(bundle.getNameRus()); //todo i8n
            addNewGrid(qs);
            bundle.getPositions().stream()
                    .map(converterService::convertBundledPositionToItemPosition)
                    .forEach(ip -> sectionHandler.putToSection(qs, ip));

            recalculateAndRedrawTotalsAndSubtotals();
            dialog.close();
        });

        finishBlock.addListener(FinishBlockSaveClickedEvent.class, e -> {
            BinderValidationStatus<Quote> validationStatus = quoteBinder.validate();
            boolean noEmptyGrids = gridsBlock.getGridsAsList().stream().noneMatch(s -> s.getQuoteSection()
                    .getPositions().size() == 0);

            if (validationStatus.isOk() && noEmptyGrids) {
                int id = postToDbAndGetID();  //persisting starts here
                fireEvent(new QuotePersistedEvent(this,
                        httpRestService.getFullName(id) + downloadService.getExtension(),
                        downloadService.downloadXlsx(id)));

                fireEvent(new DisableClickableComponents(this));

            } else {
                List<String> msg = new ArrayList<>();
                if (validationStatus.hasErrors()) {
                    List<String> errors = validationStatus.getValidationErrors().stream()
                            .map(ValidationResult::getErrorMessage).collect(Collectors.toList());
                    msg.addAll(errors);
                }
                if (!noEmptyGrids) msg.add("Some sections are empty");

                new ErrorDialog(msg).open();
            }
        });

        addListener(QuotePersistedEvent.class, e -> finishBlock.setDownloadFile(e.getName(), e.getBytes()));

        addListener(RecalculateAndRedrawTotalEvent.class, e -> {
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

        quoteBinder.forField(finishBlock.getDiscountField())
                .withValidator(value -> value >= -99 && value <= 99, "Bad discount value")
                .asRequired().bind(Quote::getDiscount, Quote::setDiscount);
        quoteBinder.forField(finishBlock.getVatField())
                .withValidator(value -> value >= 0 && value <= 99, "Bad VAT value")
                .asRequired().bind(Quote::getVat, Quote::setVat);

        return finishBlock;
    }

    private void recalculateSectionSubtotal(String currency, QuoteSection qs) {
        sectionHandler.updateSubtotalToCurrency(qs, currency,
                euroRate, dollarRate, yenRate, exchangeConversionFee);
    }

    private int postToDbAndGetID() {
        try {
            Quote quote = new Quote(0, 20, BigDecimal.valueOf(100), BigDecimal.valueOf(100),
                    BigDecimal.ONE, BigDecimal.valueOf(2));
            quoteBinder.writeBean(quote);
            quote.setSections(gridsBlock.getGridsAsList().stream().map(SectionGrid::getQuoteSection).collect(Collectors.toList()));
            quote.setFinalPrice((Money) getTotalMoney().multiply((100.0 - discount) / 100));
            return httpRestService.postNew(quote);
        } catch (ValidationException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private void addNewGrid(QuoteSection quoteSection) {
        SectionAccordion sectionAccordion = new SectionAccordion(quoteSection, stringFormattingService);
        addListener(DisableClickableComponents.class, sectionAccordion::disable);

        sectionAccordion.getControl().addListener(AccordionEditNameClickedEvent.class, e -> {
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
                        fireEvent(new RedrawGridAndSubtotalsEvent(this));
                        fireEvent(new UpdateAvailableGridsEvent(this));
                        dialog.close();
                    }
            );
            dialog.open();
        });

        sectionAccordion.getControl().addListener(AccordionDiscountChangedEvent.class, e -> {
            quoteSection.setDiscount(e.getSource().getDiscountField().getValue());
            recalculateSectionSubtotal(currency, quoteSection);
            fireEvent(new RedrawGridAndSubtotalsEvent(this));
            fireEvent(new RecalculateAndRedrawTotalEvent(this));
        });

        sectionAccordion.getControl().addListener(AccordionDeleteSectionEvent.class, e -> {
            gridsBlock.removeAccordeon(sectionAccordion);
            fireEvent(new UpdateAvailableGridsEvent(this));
            fireEvent(new RecalculateAndRedrawTotalEvent(this));
        });

        sectionAccordion.getControl().addListener(AccordionMoveUpEvent.class, e -> {
            int fromIndex = gridsBlock.indexOf(sectionAccordion);
            int toIndex = fromIndex - 1;
            if (toIndex >= 0) {
                gridsBlock.moveAccordion(sectionAccordion, toIndex);
            } else new ErrorDialog("Can't move up").open();
            fireEvent(new UpdateAvailableGridsEvent(this));
            fireEvent(new RedrawGridAndSubtotalsEvent(this));
        });

        sectionAccordion.getControl().addListener(AccordionMoveDownEvent.class, e -> {
            int fromIndex = gridsBlock.indexOf(sectionAccordion);
            int toIndex = fromIndex + 1;
            if (toIndex < gridsBlock.getComponentCount()) {
                gridsBlock.moveAccordion(sectionAccordion, toIndex);
            } else new ErrorDialog("Can't move down").open();
            fireEvent(new UpdateAvailableGridsEvent(this));
            fireEvent(new RedrawGridAndSubtotalsEvent(this));
        });

        sectionAccordion.getGrid().addListener(GridChangedQtyClickedEvent.class, e -> {
            ItemPosition ip = e.getItemPosition();
            ip.setQty(e.getQty());
            ip.setSellingSum(ip.getSellingPrice().multiply(e.getQty()));
            recalculateSectionSubtotal(currency, quoteSection);
            fireEvent(new RedrawGridAndSubtotalsEvent(this));
            fireEvent(new RecalculateAndRedrawTotalEvent(this));
        });

        sectionAccordion.getGrid().addListener(GridDeletedPositionClickedEvent.class, e -> {
            quoteSection.getPositions().remove(e.getItemPosition());
            recalculateSectionSubtotal(currency, quoteSection);
            fireEvent(new RedrawGridAndSubtotalsEvent(this));
            fireEvent(new RecalculateAndRedrawTotalEvent(this));
        });

        addListener(RedrawGridAndSubtotalsEvent.class, event -> sectionAccordion.getGrid().update(event));

        addListener(RecalculateSubtotalTotalEvent.class, e -> gridsBlock.getGridsAsList().stream()
                .map(SectionGrid::getQuoteSection).forEach(qs -> recalculateSectionSubtotal(currency, qs)));

        gridsBlock.add(sectionAccordion);

        fireEvent(new RedrawGridAndSubtotalsEvent(this));
        fireEvent(new UpdateAvailableGridsEvent(this));
    }

    private MonetaryAmount getTotalMoney() {
        return moneyMathService.getSum(gridsBlock.getGridsAsList().stream()
                .map(grid -> grid.getQuoteSection().getTotalDiscounted())
                .collect(Collectors.toList()));
    }

    private void recalculateAndRedrawTotalsAndSubtotals() {
        fireEvent(new RecalculateSubtotalTotalEvent(this));
        fireEvent(new RedrawGridAndSubtotalsEvent(this));
        fireEvent(new RecalculateAndRedrawTotalEvent(this));
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
