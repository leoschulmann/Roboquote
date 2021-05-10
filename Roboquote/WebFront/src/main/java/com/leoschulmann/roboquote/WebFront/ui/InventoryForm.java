package com.leoschulmann.roboquote.WebFront.ui;

import com.leoschulmann.roboquote.WebFront.events.*;
import com.leoschulmann.roboquote.itemservice.entities.Item;
import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.shared.Registration;
import org.javamoney.moneta.Money;

import javax.money.NumberValue;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class InventoryForm extends Dialog implements AfterNavigationObserver {
    private Item item;
    private final IntegerField idField;
    private final DatePicker createdField;
    private final DatePicker modifiedField;
    private final TextField brandField;
    private final TextField partNoField;
    private final TextArea nameRusField;
    private final TextArea nameEngField;
    private final NumberField buyingAmountField;
    private final NumberField sellingMarginField;
    private final NumberField overrideSellingAmountField;
    private final ComboBox<String> currencyComboBox;
    private final ToggleButton overrideToggle;

    private final Button saveBtn;
    private final Button deleteBtn;
    private final Button usageButton;
    private Registration saveButtonListener;

    private final Binder<Item> itemBinder;

    public InventoryForm() {
        idField = new IntegerField("id");
        idField.setEnabled(false);

        createdField = new DatePicker("Created on");
        createdField.setEnabled(false);

        modifiedField = new DatePicker("Modified on");
        modifiedField.setEnabled(false);

        brandField = new TextField("Brand");
        partNoField = new TextField("Part No");

        nameRusField = new TextArea("Name (rus)");
        nameRusField.setMaxHeight("200px");

        nameEngField = new TextArea("Name (eng)");
        nameEngField.setMaxHeight("200px");

        buyingAmountField = new NumberField("Buying price");
        buyingAmountField.setValue(0.);

        sellingMarginField = new NumberField("Selling margin, %");
        sellingMarginField.setValue(0.);

        currencyComboBox = new ComboBox<>("Currency", List.of("EUR", "USD", "RUB", "JPY"));

        overrideSellingAmountField = new NumberField("Override selling price");
        overrideSellingAmountField.setValue(0.);
        overrideSellingAmountField.setEnabled(false);

        overrideToggle = new ToggleButton();
        overrideToggle.getElement().setAttribute("title", "Override selling price");

        saveBtn = new Button();
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

        deleteBtn = new Button("Delete");
        deleteBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        usageButton = new Button("Check usage");
        usageButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        itemBinder = new Binder<>(Item.class);

        FormLayout layout = new FormLayout();
        layout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("25em", 1),
                new FormLayout.ResponsiveStep("32em", 2),
                new FormLayout.ResponsiveStep("40em", 3));

        layout.add(idField, createdField, modifiedField);
        layout.add(brandField, partNoField);
        layout.add(nameRusField, 3);
        layout.add(nameEngField, 3);
        layout.add(buyingAmountField, sellingMarginField, currencyComboBox);
        layout.add(overrideSellingAmountField, overrideToggle);
        layout.add(new HorizontalLayout(saveBtn, deleteBtn, usageButton), 2);
        add(layout);

        buyingAmountField.addValueChangeListener(e -> {
            if (!overrideToggle.getValue()) {
                writeToSellingField();
            }
        });
        sellingMarginField.addValueChangeListener(e -> {
            if (!overrideToggle.getValue()) {
                writeToSellingField();
            }
        });

        deleteBtn.addClickListener(c -> {
            ConfirmDialog dialog = new ConfirmDialog("Delete item?");
            dialog.addListener(DialogConfirmed.class, e -> fireEvent(new InventoryDeleteItemEvent(this, item)));
            dialog.open();
        });

        overrideToggle.addValueChangeListener(event -> {
            if (event.getValue()) {
                buyingAmountField.setValue(0.);
                sellingMarginField.setValue(0.);
            } else {
                overrideSellingAmountField.setValue(0.);
            }
            overrideSellingAmountField.setEnabled(event.getValue());
            buyingAmountField.setEnabled(!event.getValue());
            sellingMarginField.setEnabled(!event.getValue());
        });

        usageButton.addClickListener(e -> fireEvent(new InvetoryUsageClickedEvent(this, idField.getValue())));

        prepBinder();
    }

    private void prepBinder() {
        itemBinder.bind(idField, Item::getId, null);
        itemBinder.bind(createdField, Item::getCreated, null);
        itemBinder.bind(modifiedField, Item::getModified, null);
        itemBinder.bind(brandField, Item::getBrand, Item::setBrand);
        itemBinder.forField(partNoField).asRequired().bind(Item::getPartno, Item::setPartno);
        itemBinder.forField(nameRusField).asRequired().bind(Item::getNameRus, Item::setNameRus);
        itemBinder.forField(nameEngField).asRequired().bind(Item::getNameEng, Item::setNameEng);
        itemBinder.forField(sellingMarginField).withValidator(v -> v >= 0 && v <= 99, "Bad margin value")
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

        itemBinder.forField(overrideSellingAmountField).asRequired().bind(
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

    public void setUp(Item item, boolean editMode) {
        mode(editMode);
        setItem(item);
    }

    private void setItem(Item item) {
        this.item = item;
        itemBinder.readBean(item);
    }

    private void mode(boolean edit) {
        if (saveButtonListener != null) saveButtonListener.remove();
        saveButtonListener = saveBtn.addClickListener(click -> handleSaveUpdateClick(edit));
        usageButton.setEnabled(edit);
        saveBtn.setText(edit ? "Update" : "Create");
        deleteBtn.setEnabled(edit);
    }

    private void handleSaveUpdateClick(boolean edit) {
        BinderValidationStatus<Item> validationStatus = itemBinder.validate();
        if (validationStatus.isOk()) {
            try {
                itemBinder.writeBean(item);
            } catch (ValidationException e) {
                new ErrorDialog("Internal Error").open();
                e.printStackTrace();
            }
            item.setModified(LocalDate.now());
            if (!edit) item.setCreated(LocalDate.now());

            fireEvent(edit ? new InventoryUpdateItemEvent(this, item) : new InventoryCreateItemEvent(this, item));
        } else {
            List<String> errors = validationStatus.getValidationErrors().stream()
                    .map(ValidationResult::getErrorMessage).collect(Collectors.toList());

            new ErrorDialog(errors).open();
        }
    }

    private void writeToSellingField() {
        overrideSellingAmountField.setValue(buyingAmountField.getValue() / ((100 - sellingMarginField.getValue()) / 100.));
    }

    public <T extends ComponentEvent<?>> Registration addListener
            (Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        this.close();
    }
}
