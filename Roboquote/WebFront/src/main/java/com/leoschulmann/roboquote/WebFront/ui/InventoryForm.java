package com.leoschulmann.roboquote.WebFront.ui;

import com.leoschulmann.roboquote.WebFront.events.*;
import com.leoschulmann.roboquote.itemservice.entities.Item;
import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import org.javamoney.moneta.Money;

import javax.money.NumberValue;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class InventoryForm extends FormLayout {
    private Item item;
    private final IntegerField idField = new IntegerField("id");
    private final DatePicker createdField = new DatePicker("Created on");
    private final DatePicker modifiedField = new DatePicker("Modified on");
    private final TextField brandField = new TextField("Brand");
    private final TextField partNoField = new TextField("Part No");
    private final TextArea nameRusField = new TextArea("Name (rus)");
    private final TextArea nameEngField = new TextArea("Name (eng)");
    private final NumberField buyingAmountField = new NumberField("Buying price");
    private final NumberField marginField = new NumberField("Selling margin, %");
    private final NumberField sellingAmountField = new NumberField("Override selling price");
    private final ComboBox<String> currencyComboBox = createBuyingCurrencyComboBox();
    private final ToggleButton overrideToggle;

    private Button saveBtn;
    private Button deleteBtn;

    private final Button usageButton;
    private Registration saveButtonListener;

    private final Binder<Item> itemBinder = new Binder<>(Item.class);

    public InventoryForm() {
        buyingAmountField.setValue(0.);
        sellingAmountField.setValue(0.);
        sellingAmountField.setEnabled(false);
        marginField.setValue(0.);
        buyingAmountField.addValueChangeListener(e -> writeToSellingField());
        marginField.addValueChangeListener(e -> writeToSellingField());
        overrideToggle = createToggle();

        nameRusField.setMaxHeight("200px");
        nameEngField.setMaxHeight("200px");

        idField.setEnabled(false);
        createdField.setEnabled(false);
        modifiedField.setEnabled(false);

        usageButton = new Button("Check usage");
        usageButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        usageButton.addClickListener(e -> fireEvent(new InvetoryUsageClickedEvent(this, idField.getValue())));

        setResponsiveSteps(
                new ResponsiveStep("25em", 1),
                new ResponsiveStep("32em", 2),
                new ResponsiveStep("40em", 3));

        add(idField, createdField, modifiedField);
        add(brandField, partNoField);
        add(nameRusField, 3);
        add(nameEngField, 3);
        add(buyingAmountField, marginField, currencyComboBox);
        add(sellingAmountField, overrideToggle);
        add(new HorizontalLayout(createSaveButton(), createDeleteButton(), usageButton, createCloseButton()), 2);


        prepBinder();
        itemBinder.bindInstanceFields(this);
    }

    private Button createSaveButton() {
        saveBtn = new Button();
        saveBtn.setText("You shouldn't see this");
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        return saveBtn;
    }

    private ComboBox<String> createBuyingCurrencyComboBox() {
        return new ComboBox<>("Currency", List.of("EUR", "USD", "RUB", "JPY"));
    }

    private Component createDeleteButton() {
        deleteBtn = new Button("Delete");
        deleteBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Div text = new Div(new Span("Delete item?"));
        Button ok = new Button("Confirm");
        ok.addThemeVariants(ButtonVariant.LUMO_ERROR);
        ok.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancel = new Button("Cancel");
        Dialog confirmDialog = new Dialog(text, new HorizontalLayout(ok, cancel));
        confirmDialog.setDraggable(true);
        confirmDialog.setModal(true);
        confirmDialog.setResizable(false);

        deleteBtn.addClickListener(event -> confirmDialog.open());
        cancel.addClickListener(event -> confirmDialog.close());
        ok.addClickListener(event -> {
            confirmDialog.close();
            fireEvent(new InventoryDeleteItemEvent(this, item));
        });
        return deleteBtn;
    }

    private Component createCloseButton() {
        Button b = new Button("Close");
        b.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        b.addClickListener(event -> fireEvent(new InventoryFormCloseEvent(this, false)));
        return b;
    }

    private ToggleButton createToggle() {
        ToggleButton toggle = new ToggleButton();
        toggle.getElement().setAttribute("title", "Override selling price");
        toggle.addValueChangeListener(event -> {
            if (event.getValue()) {
                buyingAmountField.setValue(0.);
            } else {
                sellingAmountField.setValue(0.);
            }

            sellingAmountField.setEnabled(event.getValue());
            buyingAmountField.setEnabled(!event.getValue());
            marginField.setEnabled(!event.getValue());
        });
        toggle.setValue(false);
        return toggle;
    }

    private void prepBinder() {
        itemBinder.bind(idField, Item::getId, null);
        itemBinder.bind(createdField, Item::getCreated, null);
        itemBinder.bind(modifiedField, Item::getModified, null);
        itemBinder.bind(brandField, Item::getBrand, Item::setBrand);
        itemBinder.forField(partNoField).asRequired().bind(Item::getPartno, Item::setPartno);
        itemBinder.forField(nameRusField).asRequired().bind(Item::getNameRus, Item::setNameRus);
        itemBinder.forField(nameEngField).asRequired().bind(Item::getNameEng, Item::setNameEng);
        itemBinder.forField(marginField).withValidator(v -> v >= 0 && v <= 99, "Bad margin value")
                .asRequired().bind(Item::getMargin, Item::setMargin);

        itemBinder.forField(buyingAmountField).asRequired().bind(
                item -> {
                    if (item.getBuyingPrice() != null) return item.getBuyingPrice().getNumber().doubleValueExact();
                    else return null;
                },
                (item, aDouble) -> {
                    if (item.getBuyingPrice() != null) {
                        String cur = item.getBuyingPrice().getCurrency().getCurrencyCode();
                        item.setBuyingPrice(Money.of(aDouble, cur));
                    } else item.setBuyingPrice(Money.of(aDouble, "EUR"));
                }
        );

        itemBinder.forField(currencyComboBox).bind(
                item -> item.getSellingPrice().getCurrency().getCurrencyCode(),
                (item, cur) -> {
                    NumberValue buyingAmt = item.getBuyingPrice().getNumber();
                    NumberValue sellingAmt = item.getSellingPrice().getNumber();

                    Money buyingMoney = Money.of(buyingAmt, cur);
                    Money sellingMoney = Money.of(sellingAmt, cur);

                    item.setBuyingPrice(buyingMoney);
                    item.setSellingPrice(sellingMoney);
                }
        );

        itemBinder.forField(overrideToggle).bind(Item::isOverridden, Item::setOverridden);

        itemBinder.forField(sellingAmountField).asRequired().bind(
                item -> {
                    if (item.isOverridden())
                        return item.getSellingPrice().getNumber().doubleValueExact();
                    else return 0.;
                },
                (item, aDouble) -> {
                    if (item.isOverridden()) {
                        String cur = item.getSellingPrice().getCurrency().getCurrencyCode();
                        item.setSellingPrice(Money.of(aDouble, cur));
                    } else item.setSellingPrice(Money.of(BigDecimal.ZERO, "EUR"));
                }
        );
    }

    public void setItem(Item item) {
        this.item = item;
        itemBinder.readBean(item);
    }

    public <T extends ComponentEvent<?>> Registration addListener
            (Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

    public void mode(boolean edit) {
        if (saveButtonListener != null) saveButtonListener.remove();
        if (edit) saveButtonListener = saveBtn.addClickListener(click -> {
                    try {
                        itemBinder.writeBean(item);
                        item.setModified(LocalDate.now());
                        fireEvent(new InventoryUpdateItemEvent(this, item));
                    } catch (ValidationException e) {
                        showValidationErrorDialog();
                    }
                }
        );
        else saveButtonListener = saveBtn.addClickListener(click -> {
                    try {
                        itemBinder.writeBean(item);
                        item.setCreated(LocalDate.now());
                        item.setModified(LocalDate.now());
                        fireEvent(new InventoryCreateItemEvent(this, item));
                    } catch (ValidationException e) {
                        showValidationErrorDialog();
                    }
                }
        );
        usageButton.setEnabled(edit);
        saveBtn.setText(edit ? "Update" : "Create");
        deleteBtn.setEnabled(edit);

    }

    private void showValidationErrorDialog() {
        Icon i = VaadinIcon.WARNING.create();
        i.setColor("Red");
        i.setSize("50px");
        VerticalLayout vl = new VerticalLayout(i);
        vl.add(new Span("Please fill marked fields"));
        vl.setAlignItems(FlexComponent.Alignment.CENTER);
        new Dialog(vl).open();
    }

    private void writeToSellingField() {
        if (!overrideToggle.getValue()) {
            sellingAmountField.setValue(buyingAmountField.getValue() / ((100 - marginField.getValue()) / 100.));
        }
    }
}
