package com.leoschulmann.roboquote.WebFront.ui;

import com.leoschulmann.roboquote.WebFront.components.ItemService;
import com.leoschulmann.roboquote.itemservice.entities.Item;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import org.javamoney.moneta.Money;

import javax.money.NumberValue;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class InventoryForm extends FormLayout {
    private Item item;
    private IntegerField idField = new IntegerField("id");
    private TextField brandField = new TextField("Brand");
    private TextField partNoField = new TextField("Part No");
    private TextField nameRusField = new TextField("Название");
    private TextField nameEngField = new TextField("Name");
    private NumberField buyingAmountField = new NumberField("Buying price");
    private ComboBox<String> buyingCurrencyCombo = new ComboBox<>("Currency", List.of("EUR", "USD", "RUB", "JPY"));
    private NumberField marginField = new NumberField("Selling margin, %");

    private NumberField sellingAmountField = new NumberField("Override selling price");
    private ComboBox<String> sellingCurrencyCombo = new ComboBox<>("Override selling currency", List.of("EUR", "USD", "RUB", "JPY"));
    private Checkbox overrideSellPriceCheckbox;

    private Button saveBtn;
    private Button delete;
    private Button close;

    private Binder<Item> itemBinder = new Binder<>(Item.class);

    ItemService itemService;

    public InventoryForm(ItemService itemService) {
        this.itemService = itemService;

        add(idField, brandField,
                partNoField,
                nameRusField,
                nameEngField,
                buyingAmountField,
                buyingCurrencyCombo,
                marginField,

                sellingAmountField,
                sellingCurrencyCombo,
                createCheckbox(),
                createSaveButton()
//                delete,
//                close
        );

        idField.setEnabled(false);
        sellingAmountField.setEnabled(false);
        sellingCurrencyCombo.setEnabled(false);
        overrideSellPriceCheckbox.setValue(false);


        prepBinder();
        itemBinder.bindInstanceFields(this);

    }

    private Component createSaveButton() {
        saveBtn = new Button("Save");
        saveBtn.addClickListener(click ->
                {
                    try {

                        itemBinder.writeBean(item);
                        item.setCreated(LocalDate.now());
                        itemService.saveItem(item);
                    } catch (ValidationException e) {
                        e.printStackTrace();
                    }
                }
        );
        return saveBtn;
    }


    private Checkbox createCheckbox() {
        overrideSellPriceCheckbox = new Checkbox("Override selling price");


        overrideSellPriceCheckbox.addValueChangeListener(event -> {
            sellingAmountField.setEnabled(event.getValue());
            sellingCurrencyCombo.setEnabled(event.getValue());
            if (!event.getValue()) {
                sellingAmountField.clear();
                sellingCurrencyCombo.clear();
            }
        });

        return overrideSellPriceCheckbox;
    }

    private void prepBinder() {
        itemBinder.bind(idField, Item::getId, null);
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

//todo make binding magic for checkbox


        itemBinder.forField(sellingAmountField).bind(
                item -> {
                    if (item.getSellingPriceAsOverriddenValue() != null)
                        item.getSellingPriceAsOverriddenValue().getNumber().doubleValueExact();
                    return null;
                },
                (item, aDouble) -> {
                    if (item.getSellingPriceAsOverriddenValue() != null) {
                        String cur = item.getSellingPriceAsOverriddenValue().getCurrency().getCurrencyCode();
                        item.setSellingPrice(Money.of(aDouble, cur));
                    } else item.setSellingPrice(Money.of(aDouble, "EUR"));
                }
        );

        itemBinder.forField(sellingCurrencyCombo).bind(
                item -> {
                    if (item.getSellingPriceAsOverriddenValue() != null) {
                        return item.getSellingPriceAsOverriddenValue().getCurrency().getCurrencyCode();
                    } else return null;
                },
                (item, cur) -> {
                    if (item.getSellingPriceAsOverriddenValue() != null) {
                        NumberValue nv = item.getSellingPriceAsOverriddenValue().getNumber();
                        item.setSellingPrice(Money.of(nv, cur));
                    } else item.setSellingPrice(Money.of(BigDecimal.ZERO, cur));
                }
        );
    }

    public void setItem(Item item) {
        this.item = item;
        itemBinder.readBean(item);
    }
}
