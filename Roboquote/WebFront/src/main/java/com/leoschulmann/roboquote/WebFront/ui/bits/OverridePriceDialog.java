package com.leoschulmann.roboquote.WebFront.ui.bits;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class OverridePriceDialog extends Dialog {
    private final BigDecimalField price;
    private final Button override;

    public OverridePriceDialog(BigDecimal amount, String currency) {
        price = new BigDecimalField();
        price.setValue(amount);
        price.setSuffixComponent(new Span(currency));
        price.setWidthFull();
        override = new Button("OK");
        override.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
        override.setWidthFull();
        setWidth("25%");
        add(new VerticalLayout(price, override));
    }
}
