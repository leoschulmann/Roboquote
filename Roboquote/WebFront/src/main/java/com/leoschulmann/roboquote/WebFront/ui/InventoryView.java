package com.leoschulmann.roboquote.WebFront.ui;

import com.leoschulmann.roboquote.WebFront.components.CurrencyFormatService;
import com.leoschulmann.roboquote.WebFront.components.ItemCachingService;
import com.leoschulmann.roboquote.WebFront.components.ItemService;
import com.leoschulmann.roboquote.WebFront.events.InventoryCreateItemEvent;
import com.leoschulmann.roboquote.WebFront.events.InventoryDeleteItemEvent;
import com.leoschulmann.roboquote.WebFront.events.InventoryFormCloseEvent;
import com.leoschulmann.roboquote.WebFront.events.InventoryUpdateItemEvent;
import com.leoschulmann.roboquote.itemservice.entities.Item;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.javamoney.moneta.Money;
import org.vaadin.klaudeta.PaginatedGrid;

import java.math.BigDecimal;

import static com.vaadin.flow.component.grid.GridVariant.*;

@Route(value = "inventory", layout = MainLayout.class)
public class InventoryView extends VerticalLayout {
    private final ItemService itemService;
    private final CurrencyFormatService currencyFormatService;
    private ItemCachingService cachingService;
    private PaginatedGrid<Item> grid;
    private InventoryForm form;
    private Dialog dialog;

    public InventoryView(ItemService itemService, CurrencyFormatService currencyFormatService,
                        ItemCachingService cachingService) {
        this.itemService = itemService;
        this.currencyFormatService = currencyFormatService;
        this.cachingService = cachingService;
        grid = drawGrid();

        form = new InventoryForm();
        dialog = new Dialog(form);
        dialog.setWidth("66%");

        form.addListener(InventoryFormCloseEvent.class, event -> closeDialog());
        form.addListener(InventoryDeleteItemEvent.class, this::delete);
        form.addListener(InventoryUpdateItemEvent.class, this::update);
        form.addListener(InventoryCreateItemEvent.class, this::create);

        add(createNewItem(), drawGrid());

        grid.setItems(cachingService.getItemsFromCache());
    }

    private PaginatedGrid<Item> drawGrid() {
        grid = new PaginatedGrid<>(Item.class);
        grid.removeAllColumns();
        grid.addThemeVariants(LUMO_ROW_STRIPES, LUMO_WRAP_CELL_CONTENT, LUMO_COLUMN_BORDERS);
        grid.addColumn(Item::getId).setHeader("Id").setSortable(true).setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(Item::getBrand).setHeader("Brand").setKey("brand").setSortable(true).setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(Item::getPartno).setHeader("Part No").setKey("partno").setSortable(true).setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(item -> trim(item.getNameRus(), 75)).setHeader("Name RUS").setSortable(true).setResizable(true).setFlexGrow(1);
        grid.addColumn(item -> trim(item.getNameEng(), 75)).setHeader("Name ENG").setSortable(true).setFlexGrow(1);
        grid.addColumn(i -> currencyFormatService.formatMoney(
                i.getSellingPrice() == null ? Money.of(0, "EUR") : i.getSellingPrice()))
                .setHeader("Selling price").setSortable(true)
                .setComparator(i -> i.getSellingPrice() == null ? 0. : i.getSellingPrice().getNumber().doubleValue())
                .setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(Item::getMargin).setHeader("Margin").setSortable(true).setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(Item::getModified).setHeader("Modified").setSortable(true).setAutoWidth(true).setFlexGrow(0);
        grid.addComponentColumn(i -> i.isOverridden() ? getIcon(true) : getIcon(false)).setHeader("Override").setSortable(true)
                .setAutoWidth(true).setFlexGrow(0);

        grid.asSingleSelect().addValueChangeListener(event -> editItem(event.getValue()));
        grid.setPageSize(10);
        grid.setPaginatorSize(5);
        return grid;
    }

    private String trim(String str, int limit) {
        return str.length() > limit ? str.substring(0, limit) + "[...]" : str;
    }

    private Icon getIcon(boolean boo) {
        String size = "15px";
        Icon i = boo ? VaadinIcon.CHECK_SQUARE_O.create() : VaadinIcon.THIN_SQUARE.create();
        i.setSize(size);
        return i;
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
        cachingService.updateCache();
        grid.setItems(cachingService.getItemsFromCache());
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

    private void delete(InventoryDeleteItemEvent event) {
        itemService.deleteItem(event.getEventItem());
        updateList();
        closeDialog();
    }
}
