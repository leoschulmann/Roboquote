package com.leoschulmann.roboquote.WebFront.ui.bits;

import com.leoschulmann.roboquote.WebFront.events.*;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class RatesPanel extends HorizontalLayout {
    private final Button update;
    private final BigDecimalField euro;
    private final BigDecimalField dollar;
    private final BigDecimalField yen;
    private final NumberField conversionRate;

    public RatesPanel() {
        update = new Button("Get rates");
        update.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        update.addClickListener(c -> fireEvent(new RatesPanelUpdateClickedEvent(this)));

        euro = new BigDecimalField("₽/€");
        dollar = new BigDecimalField("₽/$");
        yen = new BigDecimalField("₽/¥");
        euro.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
        dollar.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
        yen.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);

        conversionRate = new NumberField("Conversion rate");
        conversionRate.setSuffixComponent(new Span("%"));
        conversionRate.setMin(-99.);
        conversionRate.setMax(99.);
        conversionRate.setHasControls(true);
        conversionRate.setStep(0.5);

        euro.addValueChangeListener(e -> fireEvent(new EuroFieldChangedEvent(euro)));
        dollar.addValueChangeListener(e -> fireEvent(new DollarFieldChangedEvent(dollar)));
        yen.addValueChangeListener(e -> fireEvent(new YenFieldChangedEvent(yen)));
        conversionRate.addValueChangeListener(e -> fireEvent(new ExchangeRateFieldChangedEvent(conversionRate)));

        add(update, conversionRate, euro, dollar, yen);

        setAlignItems(Alignment.END);
    }
    public void updateAllCurrencies(BigDecimal euroRate, BigDecimal dollarRate, BigDecimal yenRate) {
        euro.setValue(euroRate);
        dollar.setValue(dollarRate);
        yen.setValue(yenRate);
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

}
