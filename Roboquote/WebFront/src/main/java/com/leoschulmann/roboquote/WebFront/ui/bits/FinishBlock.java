package com.leoschulmann.roboquote.WebFront.ui.bits;

import com.leoschulmann.roboquote.WebFront.events.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.server.StreamResource;
import lombok.Getter;
import org.vaadin.olli.FileDownloadWrapper;

import java.io.ByteArrayInputStream;

@Getter
public class FinishBlock extends VerticalLayout {
    private final IntegerField discountField;
    private final IntegerField vatField;
    private final ComboBox<String> currencyCombo;
    private final Button addNewSectionBtn;
    private final Button saveQuoteBtn;
    private final Button dlButt;
    private final FileDownloadWrapper wrapper;
    private final Span totalString;
    private final Span totalWithDiscountString;
    private final Span includingVatValue;


    public FinishBlock(String currency, Integer discount, Integer vat) {
        super();
        addListener(DisableClickableComponents.class, this::disable);
        addListener(QuotePersistedEvent.class, this::setDownloadFile);

        discountField = new IntegerField();
        discountField.setValue(discount);
        discountField.setHasControls(true);
        discountField.setMin(-99);
        discountField.setMax(99);
        discountField.setLabel("Discount, %");
        discountField.addValueChangeListener(c -> fireEvent(new GlobalDiscountEvent(this)));

        vatField = new IntegerField();
        vatField.setValue(vat);
        vatField.setHasControls(true);
        vatField.setMin(0);
        vatField.setMax(99);
        vatField.setLabel("Vat, %");
        vatField.addValueChangeListener(c -> fireEvent(new VatEvent(this)));

        currencyCombo = new ComboBox<>("Currency", "EUR", "USD", "RUB", "JPY");
        currencyCombo.setValue(currency);
        currencyCombo.addValueChangeListener(event -> fireEvent(new CurrencyChangedEvent(this)));

        addNewSectionBtn = new Button("Add new section");
        addNewSectionBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addNewSectionBtn.addClickListener(c -> fireEvent(new FinishBlockAddSectionClickedEvent(this)));

        saveQuoteBtn = new Button("Save to DB");
        saveQuoteBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
        saveQuoteBtn.addClickListener(click -> fireEvent(new FinishBlockSaveClickedEvent(this)));


        dlButt = new Button("Download");
        wrapper = new FileDownloadWrapper(new StreamResource("error", () -> new ByteArrayInputStream(new byte[]{})));
        dlButt.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_PRIMARY);
        wrapper.wrapComponent(dlButt);


        FormLayout buttons = new FormLayout(discountField, vatField, currencyCombo, addNewSectionBtn,
                saveQuoteBtn, wrapper);
        buttons.setResponsiveSteps(
                new FormLayout.ResponsiveStep("25em", 1),
                new FormLayout.ResponsiveStep("32em", 3),
                new FormLayout.ResponsiveStep("40em", 5));


        totalString = new Span();
        totalString.getElement().getStyle().set("margin-left", "auto");

        totalWithDiscountString = new Span();
        totalWithDiscountString.getElement().getStyle().set("margin-left", "auto").set("font-weight", "bold");

        includingVatValue = new Span();
        includingVatValue.getElement().getStyle().set("margin-left", "auto");

        add(totalString, totalWithDiscountString, includingVatValue, buttons);
    }

    private void disable(DisableClickableComponents e) {
        discountField.setEnabled(false);
        vatField.setEnabled(false);
        currencyCombo.setEnabled(false);
        addNewSectionBtn.setEnabled(false);
        saveQuoteBtn.setEnabled(false);
    }

    private void setDownloadFile(QuotePersistedEvent e) {
        wrapper.setResource(new StreamResource(e.getName(), () -> new ByteArrayInputStream(e.getBytes())));
    }
}
