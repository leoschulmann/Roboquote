package com.leoschulmann.roboquote.WebFront.ui.bits;

import com.leoschulmann.roboquote.WebFront.events.*;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
public class SectionButtons extends HorizontalLayout {
    private final BigDecimalField discountField;
    private final Button editNameBtn;
    private final Button wrapButton;
    private final Button moveUpBtn;
    private final Button moveDownBtn;
    private final Button deleteBtn;

    public SectionButtons(SectionGrid grid, BigDecimal discount) {
        editNameBtn = new Button(VaadinIcon.EDIT.create());
        discountField = new BigDecimalField("Discount, %");
        wrapButton = new Button(VaadinIcon.LINES.create());
        moveUpBtn = new Button(VaadinIcon.ARROW_UP.create());
        moveDownBtn = new Button(VaadinIcon.ARROW_DOWN.create());
        deleteBtn = new Button(VaadinIcon.CLOSE_CIRCLE.create());

        editNameBtn.addClickListener(c -> fireEvent(new AccordionEditNameClickedEvent(this)));
        editNameBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);

        discountField.setValue(discount);
        discountField.setClearButtonVisible(true);
        discountField.setValueChangeMode(ValueChangeMode.ON_CHANGE);
        discountField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        discountField.addValueChangeListener(c -> {
            discountField.setLabel(c.getValue().compareTo(BigDecimal.ZERO) < 0 ? "Markup, %" : "Discount, %");
            fireEvent(new AccordionDiscountChangedEvent(this));
        });

        wrapButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
        wrapButton.addClickListener(c -> {
            if (grid.isTextWrap()) grid.removeThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
            else grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
            grid.setTextWrap(!grid.isTextWrap());
        });

        deleteBtn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        deleteBtn.addClickListener(c -> fireEvent(new AccordionDeleteSectionEvent(this)));

        moveUpBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        moveUpBtn.addClickListener(click -> fireEvent(new AccordionMoveUpEvent(this)));


        moveDownBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        moveDownBtn.addClickListener(click -> fireEvent(new AccordionMoveDownEvent(this)));

        Button incr = new Button(VaadinIcon.PLUS.create());
        incr.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);
        incr.addClickListener(c -> discountField.setValue(discountField.getValue()
                .setScale(0, RoundingMode.HALF_UP).add(BigDecimal.ONE)));

        Button decr = new Button(VaadinIcon.MINUS.create());
        decr.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL);
        decr.addClickListener(c -> discountField.setValue(discountField.getValue()
                .setScale(0, RoundingMode.HALF_UP).subtract(BigDecimal.ONE)));

        getStyle().set("margin-left", "auto");
        setAlignItems(Alignment.END);
        add(decr, incr, discountField, editNameBtn, wrapButton, moveUpBtn, moveDownBtn, deleteBtn);
    }

    public void disable() {
        setEnabled(false);
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
