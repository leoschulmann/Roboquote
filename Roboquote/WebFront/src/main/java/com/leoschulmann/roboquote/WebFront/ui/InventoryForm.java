package com.leoschulmann.roboquote.WebFront.ui;

import com.leoschulmann.roboquote.WebFront.components.ItemService;
import com.leoschulmann.roboquote.itemservice.entities.Item;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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
    private final ComboBox<String> buyingCurrencyCombo = getBuyingCurrencyComboBox();
    private final NumberField marginField = new NumberField("Selling margin, %");

    private final NumberField sellingAmountField = new NumberField("Override selling price");
    private final ComboBox<String> sellingCurrencyCombo = getSellingCurrencyComboBox();
    private Checkbox overrideSellPriceCheckbox = createCheckbox();

    private final Binder<Item> itemBinder = new Binder<>(Item.class);
    ItemService itemService;

    public InventoryForm(ItemService itemService) {
        this.itemService = itemService;

        setResponsiveSteps(
                new ResponsiveStep("25em", 1),
                new ResponsiveStep("32em", 2),
                new ResponsiveStep("40em", 3));


        add(idField, createdField, modifiedField);
        add(brandField, partNoField);
        add(nameRusField, 3);
        add(nameEngField, 3);
        add(buyingAmountField, buyingCurrencyCombo, marginField);
        add(sellingAmountField, sellingCurrencyCombo, overrideSellPriceCheckbox);
        add(new HorizontalLayout(createSaveButton(), createDeleteButton(), createCloseButton()), 2);

        nameRusField.setMaxHeight("200px");
        nameEngField.setMaxHeight("200px");

        idField.setEnabled(false);
        createdField.setEnabled(false);
        modifiedField.setEnabled(false);

        prepBinder();
        itemBinder.bindInstanceFields(this);
    }

    private Component createSaveButton() {
        Button saveBtn = new Button("Save");
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        saveBtn.addClickListener(click ->
                {
                    try {
                        itemBinder.writeBean(item);
                        item.setCreated(LocalDate.now());
                        itemService.saveItem(item);
                        fireEvent(new CloseEvent(this, false));
                    } catch (ValidationException e) {
                        e.printStackTrace();
                    }
                }
        );
        return saveBtn;
    }

    private ComboBox<String> getBuyingCurrencyComboBox() {
        return new ComboBox<>("Currency", List.of("EUR", "USD", "RUB", "JPY"));
    }

    private ComboBox<String> getSellingCurrencyComboBox() {
        return new ComboBox<>("Override selling currency", List.of("EUR", "USD", "RUB", "JPY"));
    }

    private Component createDeleteButton() {
        Button b = new Button("Delete");
        b.addThemeVariants(ButtonVariant.LUMO_ERROR);
        b.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        return b;
    }

    private Component createCloseButton() {
        Button b = new Button("Close");
        b.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        return b;
    }


    private Checkbox createCheckbox() {
        overrideSellPriceCheckbox = new Checkbox("Override selling price");
        overrideSellPriceCheckbox.setValue(true);

        overrideSellPriceCheckbox.addValueChangeListener(event -> {
            sellingAmountField.setEnabled(event.getValue());
            sellingCurrencyCombo.setEnabled(event.getValue());
            buyingCurrencyCombo.setEnabled(!event.getValue());
            buyingAmountField.setEnabled(!event.getValue());
            marginField.setEnabled(!event.getValue());
        });

        return overrideSellPriceCheckbox;
    }

    private void prepBinder() {
        itemBinder.bind(idField, Item::getId, null);
        itemBinder.bind(createdField, Item::getCreated, null);
        itemBinder.bind(modifiedField, Item::getModified, null);
        itemBinder.bind(brandField, Item::getBrand, Item::setBrand);
        itemBinder.bind(partNoField, Item::getPartno, Item::setPartno);
        itemBinder.bind(nameRusField, Item::getNameRus, Item::setNameRus);
        itemBinder.bind(nameEngField, Item::getNameEng, Item::setNameEng);
        itemBinder.bind(marginField, Item::getMargin, Item::setMargin);

        itemBinder.forField(buyingAmountField).bind(
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

        itemBinder.forField(buyingCurrencyCombo).bind(
                item -> {
                    if (item.getBuyingPrice() != null) {
                        return item.getBuyingPrice().getCurrency().getCurrencyCode();
                    } else return null;
                },
                (item, cur) -> {
                    if (item.getBuyingPrice() != null) {
                        NumberValue nv = item.getBuyingPrice().getNumber();
                        item.setBuyingPrice(Money.of(nv, cur));
                    } else item.setBuyingPrice(Money.of(BigDecimal.ZERO, cur));
                }
        );

        itemBinder.forField(overrideSellPriceCheckbox).bind(Item::isOverridden, Item::setOverridden);

        itemBinder.forField(sellingAmountField).bind(
                item -> {
                    if (item.isOverridden())
                        return item.getSellingPrice().getNumber().doubleValueExact();
                    else return 0.;
                },
                (item, aDouble) -> {
                    if (item.isOverridden()) {
                        String cur = item.getSellingPrice().getCurrency().getCurrencyCode(); //todo check for bugs
                        item.setSellingPrice(Money.of(aDouble, cur));
                    } else item.setSellingPrice(Money.of(BigDecimal.ZERO, "EUR"));
                }
        );

        itemBinder.forField(sellingCurrencyCombo).bind(
                item -> {
                    if (item.isOverridden()) {
                        return item.getSellingPrice().getCurrency().getCurrencyCode();
                    } else return "EUR";
                },
                (item, cur) -> {
                    if (item.isOverridden()) {
                        NumberValue nv = item.getSellingPrice().getNumber();  //todo check for bugs
                        item.setSellingPrice(Money.of(nv, cur));
                    } else item.setSellingPrice(Money.of(BigDecimal.ZERO, cur));
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
}

class CloseEvent extends ComponentEvent<Component> {
    public CloseEvent(Component source, boolean fromClient) {
        super(source, fromClient);
    }
}
