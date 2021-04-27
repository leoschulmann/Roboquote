package com.leoschulmann.roboquote.WebFront.ui.bits;

import com.leoschulmann.roboquote.WebFront.events.*;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;

@Getter
public class SectionButtons extends HorizontalLayout {
    private final IntegerField discountField;
    private final Button editNameBtn;
    private final Button wrapButton;
    private final Button moveUpBtn;
    private final Button moveDownBtn;
    private final Button deleteBtn;

    public SectionButtons(SectionGrid grid, Integer discount) {
        editNameBtn = new Button(VaadinIcon.EDIT.create());
        discountField = new IntegerField("Discount, %");
        wrapButton = new Button(VaadinIcon.LINES.create());
        moveUpBtn = new Button(VaadinIcon.ARROW_UP.create());
        moveDownBtn = new Button(VaadinIcon.ARROW_DOWN.create());
        deleteBtn = new Button(VaadinIcon.CLOSE_CIRCLE.create());

        editNameBtn.addClickListener(c -> fireEvent(new AccordionEditNameClickedEvent(this)));
        editNameBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);

        discountField.setValue(discount);
        discountField.setHasControls(true);
        discountField.setMin(-300);
        discountField.setMax(100);
        discountField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        discountField.addValueChangeListener(c -> {
            discountField.setLabel(c.getValue() < 0 ? "Markup, %" : "Discount, %");
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

        getStyle().set("margin-left", "auto");
        setAlignItems(Alignment.END);
        add(discountField, editNameBtn, wrapButton, moveUpBtn, moveDownBtn, deleteBtn);
    }

    public void disable() {
        setEnabled(false);
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
