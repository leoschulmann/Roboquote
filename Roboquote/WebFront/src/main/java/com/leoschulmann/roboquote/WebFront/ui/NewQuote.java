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

    private final CurrencyRatesService currencyRatesService;
    private final QuoteSectionHandler sectionHandler;
    private final DownloadService downloadService;
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

    public NewQuote(CurrencyRatesService currencyRatesService,
                    QuoteSectionHandler sectionHandler, DownloadService downloadService,
                    StringFormattingService stringFormattingService,
                    MoneyMathService moneyMathService, ItemCachingService cachingService,
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

        lookup.addListener(InventoryLookupRefreshButtonEvent.class, e -> {
            cachingService.updateCache();
            lookup.setItems(cachingService.getItemsFromCache());
        });

        addListener(UpdateAvailableGridsEvent.class, e -> lookup.updateGrids(gridsBlock.getGridsAsList()));

        return lookup;
    }

    private InfoAccordion createQuoteInfoBlock() {
        InfoAccordion acc = new InfoAccordion();
        addListener(DisableClickableComponents.class, acc::disable);

        acc.addRatesBlock(createRatesBlock());

        quoteBinder.forField(acc.getCustomer()).asRequired().bind(Quote::getCustomer, Quote::setCustomer);
        quoteBinder.bind(acc.getCustomerInfo(), Quote::getCustomerInfo, Quote::setCustomerInfo);
        quoteBinder.bind(acc.getDealer(), Quote::getDealer, Quote::setDealer);
        quoteBinder.bind(acc.getDealerInfo(), Quote::getDealerInfo, Quote::setDealerInfo);
        quoteBinder.bind(acc.getPaymentTerms(), Quote::getPaymentTerms, Quote::setPaymentTerms);
        quoteBinder.bind(acc.getShippingTerms(), Quote::getShippingTerms, Quote::setShippingTerms);
        quoteBinder.bind(acc.getWarranty(), Quote::getWarranty, Quote::setWarranty);
        quoteBinder.bind(acc.getInstallation(), Quote::getInstallation, Quote::setInstallation);
        quoteBinder.bind(acc.getComment(), Quote::getComment, Quote::setComment);
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

        quoteBinder.forField(ratesPanel.getConversionRate()).asRequired().bind(quote -> quote.getConversionRate().doubleValue(),
                (quote1, conversionRate1) -> quote1.setConversionRate(BigDecimal.valueOf(conversionRate1)));
        quoteBinder.forField(ratesPanel.getEuro()).asRequired().bind(Quote::getEurRate, Quote::setEurRate);
        quoteBinder.forField(ratesPanel.getDollar()).asRequired().bind(Quote::getUsdRate, Quote::setUsdRate);
        quoteBinder.forField(ratesPanel.getYen()).asRequired().bind(Quote::getJpyRate, Quote::setJpyRate);

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
            boolean noEmptyGrids = gridsBlock.getGridsAsList().stream().noneMatch(s -> s.getQuoteSection()
                    .getPositions().size() == 0);

            if (quoteBinder.validate().isOk() && noEmptyGrids) {
                int id = postToDbAndGetID();  //persisting starts here
                fireEvent(new QuotePersistedEvent(this,
                        httpRestService.getFullName(id) + downloadService.getExtension(),
                        downloadService.downloadXlsx(id)));

                fireEvent(new DisableClickableComponents(this));

            } else {
                List<String> msg = new ArrayList<>();
                if (quoteBinder.validate().hasErrors()) msg.add("Please fill marked fields");
                if (!noEmptyGrids) msg.add("Some sections are empty");
                String[] arr = new String[msg.size()];

                new ErrorDialog(msg.toArray(arr)).open();
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

        quoteBinder.forField(finishBlock.getDiscountField()).asRequired().bind(Quote::getDiscount, Quote::setDiscount);
        quoteBinder.forField(finishBlock.getVatField()).asRequired().bind(Quote::getVat, Quote::setVat);

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
