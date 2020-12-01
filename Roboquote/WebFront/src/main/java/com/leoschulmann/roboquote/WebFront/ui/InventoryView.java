package com.leoschulmann.roboquote.WebFront.ui;

import com.leoschulmann.roboquote.WebFront.components.CurrencyFormatService;
import com.leoschulmann.roboquote.WebFront.components.ItemService;
import com.leoschulmann.roboquote.WebFront.events.InventoryCreateItemEvent;
import com.leoschulmann.roboquote.WebFront.events.InventoryDeleteItemEvent;
import com.leoschulmann.roboquote.WebFront.events.InventoryFormCloseEvent;
import com.leoschulmann.roboquote.WebFront.events.InventoryUpdateItemEvent;
import com.leoschulmann.roboquote.itemservice.entities.Item;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.javamoney.moneta.Money;

import java.math.BigDecimal;

@Route(value = "inventory", layout = MainLayout.class)
public class InventoryView extends VerticalLayout {
    private final ItemService itemService;
    private final CurrencyFormatService currencyFormatService;
    private Grid<Item> grid;
    private InventoryForm form;
    private Dialog dialog;

    public InventoryView(ItemService itemService, CurrencyFormatService currencyFormatService) {
        this.itemService = itemService;
        this.currencyFormatService = currencyFormatService;
        drawGrid();

        form = new InventoryForm();
        dialog = new Dialog(form);
        dialog.setWidth("66%");

        form.addListener(InventoryFormCloseEvent.class, event -> closeDialog());
        form.addListener(InventoryDeleteItemEvent.class, this::delete);
        form.addListener(InventoryUpdateItemEvent.class, this::update);
        form.addListener(InventoryCreateItemEvent.class, this::create);

        add(createNewItem(), drawGrid());

        updateList();
    }

    private Grid<Item> drawGrid() {
        grid = new Grid<>(Item.class);
        setSizeFull();
        grid.removeAllColumns();
        grid.addColumns("brand", "partno", "nameRus", "nameEng");
        grid.addColumn(item -> currencyFormatService.formatMoney(item.getSellingPrice())).setHeader("Selling Price");
        grid.addColumns("margin", "modified");
        grid.addComponentColumn(item -> item.isOverridden() ?
                VaadinIcon.CHECK_SQUARE_O.create() : VaadinIcon.THIN_SQUARE.create()).setHeader("Override");

        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(event -> editItem(event.getValue()));
        return grid;
    }

    private Button createNewItem() {
        Button newItemBtn = new Button("Create new item");
        newItemBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newItemBtn.addClickListener(c -> {
            grid.asSingleSelect().clear();
            Item i = new Item();
            i.setOverridden(false);
            i.setBuyingPrice(Money.of(BigDecimal.ZERO, "EUR"));
            i.setSellingPrice(Money.of(BigDecimal.ZERO, "EUR"));
            form.mode(false);
            form.setItem(i);
            dialog.open();
        });
        return newItemBtn;
    }

    private void editItem(Item value) {
        form.mode(true);
        form.setItem(value);
        dialog.open();
    }

    private void updateList() {
        grid.setItems(itemService.findAll());
    }

    private void closeDialog() {
        dialog.close();
    }

    private void create(InventoryCreateItemEvent event) {
        itemService.saveItem(event.getEventItem());
        closeDialog();
        updateList();
    }

    private void update(InventoryUpdateItemEvent event) {
        itemService.updateItem(event.getEventItem());
        updateList();
        closeDialog();
    }

    private void delete(InventoryDeleteItemEvent event) { //todo add confirmation dialog
        itemService.deleteItem(event.getEventItem());
        updateList();
        closeDialog();
    }
}
