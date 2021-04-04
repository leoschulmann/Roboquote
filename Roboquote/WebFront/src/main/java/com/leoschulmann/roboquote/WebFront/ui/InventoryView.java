package com.leoschulmann.roboquote.WebFront.ui;

import com.leoschulmann.roboquote.WebFront.components.CurrencyFormatService;
import com.leoschulmann.roboquote.WebFront.components.HttpRestService;
import com.leoschulmann.roboquote.WebFront.components.ItemCachingService;
import com.leoschulmann.roboquote.WebFront.events.InventoryCreateItemEvent;
import com.leoschulmann.roboquote.WebFront.events.InventoryDeleteItemEvent;
import com.leoschulmann.roboquote.WebFront.events.InventoryFormCloseEvent;
import com.leoschulmann.roboquote.WebFront.events.InventoryUpdateItemEvent;
import com.leoschulmann.roboquote.itemservice.entities.Item;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import org.apache.commons.lang3.StringUtils;
import org.javamoney.moneta.Money;
import org.vaadin.klaudeta.PaginatedGrid;

import java.math.BigDecimal;
import java.util.ArrayList;

import static com.vaadin.flow.component.grid.GridVariant.*;

@Route(value = "inventory", layout = MainLayout.class)
public class InventoryView extends VerticalLayout {
    private final CurrencyFormatService currencyFormatService;
    private final ItemCachingService cachingService;
    private PaginatedGrid<Item> grid;
    private final InventoryForm form;
    private final Dialog dialog;
    private final ListDataProvider<Item> dataProvider;
    ArrayList<Item> data;
    private final HttpRestService httpService;


    public InventoryView(
            HttpRestService httpService,
            CurrencyFormatService currencyFormatService,
            ItemCachingService cachingService) {

        this.httpService = httpService;
        this.currencyFormatService = currencyFormatService;
        this.cachingService = cachingService;
        data = new ArrayList<>();
        data.addAll(cachingService.getItemsFromCache());
        dataProvider = new ListDataProvider<>(data);
        grid = drawGrid();

        form = new InventoryForm();
        dialog = new Dialog(form);
        dialog.setWidth("66%");

        form.addListener(InventoryFormCloseEvent.class, event -> closeDialog());
        form.addListener(InventoryDeleteItemEvent.class, this::delete);
        form.addListener(InventoryUpdateItemEvent.class, this::update);
        form.addListener(InventoryCreateItemEvent.class, this::create);

        add(createTopControls(), grid);
    }

    private HorizontalLayout createTopControls() {
        HorizontalLayout hl = new HorizontalLayout(createNewItem(), gridLengthSelector());
        hl.setAlignItems(Alignment.BASELINE);
        return hl;
    }

    private PaginatedGrid<Item> drawGrid() {
        grid = new PaginatedGrid<>(Item.class);
        grid.setDataProvider(dataProvider);
        grid.removeAllColumns();
        grid.addThemeVariants(LUMO_ROW_STRIPES, LUMO_WRAP_CELL_CONTENT, LUMO_COLUMN_BORDERS);
        grid.addColumn(Item::getId)
                .setHeader("Id")
                .setSortable(true)
                .setAutoWidth(true)
                .setResizable(true).setFlexGrow(0);
        Column<Item> brandCol = grid.addColumn(Item::getBrand)
                .setHeader("Brand")
                .setKey("brand")
                .setSortable(true)
                .setAutoWidth(true)
                .setResizable(true).setFlexGrow(0);
        Column<Item> partnoCol = grid.addColumn(Item::getPartno)
                .setHeader("Part No")
                .setKey("partno")
                .setSortable(true)
                .setAutoWidth(true)
                .setResizable(true).setFlexGrow(0);
        Column<Item> nameCol = grid.addColumn(item -> trim(item.getNameRus(), 75))
                .setHeader("Name RUS")
                .setSortable(true)
                .setResizable(true).setFlexGrow(1);
//        grid.addColumn(item -> trim(item.getNameEng(), 75)).setHeader("Name ENG").setSortable(true).setFlexGrow(1);
        grid.addColumn(i -> currencyFormatService.formatMoney(
                i.getSellingPrice() == null ? Money.of(0, "EUR") : i.getSellingPrice()))
                .setHeader("Selling price").setSortable(true)
                .setComparator(i -> i.getSellingPrice() == null ? 0. : i.getSellingPrice().getNumber().doubleValue())
                .setAutoWidth(true)
                .setResizable(true).setFlexGrow(0);
        grid.addColumn(Item::getMargin)
                .setHeader("Margin")
                .setSortable(true)
                .setAutoWidth(true)
                .setResizable(true).setFlexGrow(0);
        grid.addColumn(Item::getModified)
                .setHeader("Modified")
                .setSortable(true)
                .setAutoWidth(true)
                .setResizable(true).setFlexGrow(0);
        grid.addComponentColumn(i -> i.isOverridden() ? getIcon(true) : getIcon(false))
                .setHeader("Override")
                .setSortable(true)
                .setAutoWidth(true).setFlexGrow(0);

        HeaderRow filterRow = grid.appendHeaderRow();
        TextField brandFil = new TextField();
        brandFil.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        brandFil.setWidth("5em");
        brandFil.setClearButtonVisible(true);
        brandFil.addValueChangeListener(event -> dataProvider.addFilter(
                item -> StringUtils.containsIgnoreCase(item.getBrand(), brandFil.getValue())));
        brandFil.setValueChangeMode(ValueChangeMode.EAGER);

        TextField partnoFil = new TextField();
        partnoFil.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        partnoFil.setWidth("5em");
        partnoFil.setClearButtonVisible(true);
        partnoFil.addValueChangeListener(event -> dataProvider.addFilter(
                item -> StringUtils.containsIgnoreCase(item.getPartno(), partnoFil.getValue())));
        partnoFil.setValueChangeMode(ValueChangeMode.EAGER);

        TextField nameFil = new TextField();
        nameFil.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        nameFil.setWidth("15em");
        nameFil.setClearButtonVisible(true);
        nameFil.addValueChangeListener(event -> dataProvider.addFilter(
                item -> StringUtils.containsIgnoreCase(item.getNameRus(), nameFil.getValue())));
        nameFil.setValueChangeMode(ValueChangeMode.EAGER);

        filterRow.getCell(brandCol).setComponent(brandFil);
        filterRow.getCell(partnoCol).setComponent(partnoFil);
        filterRow.getCell(nameCol).setComponent(nameFil);

        grid.asSingleSelect().addValueChangeListener(event -> editItem(event.getValue()));
        grid.setPageSize(10);
        grid.setPaginatorSize(5);
        grid.getStyle().set("font-size", "12px");
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

    private ComboBox<Integer> gridLengthSelector() {
        ComboBox<Integer> box = new ComboBox<>("# items");
        box.setItems(5, 10, 15, 20, 25, 30, 50, 100);
        box.addValueChangeListener(l -> grid.setPageSize(l.getValue()));
        box.setValue(10);
        box.setWidth("5em");
        return box;
    }

    private void editItem(Item value) {
        form.mode(true);
        form.setItem(value);
        dialog.open();
    }

    private void updateList() {
        cachingService.updateCache();
        data.clear();
        data.addAll(cachingService.getItemsFromCache());
    }

    private void closeDialog() {
        dialog.close();
    }

    private void create(InventoryCreateItemEvent event) {
        httpService.saveItem(event.getEventItem());
        closeDialog();
        updateList();
    }

    private void update(InventoryUpdateItemEvent event) {
        httpService.updateItem(event.getEventItem());
        updateList();
        closeDialog();
    }

    private void delete(InventoryDeleteItemEvent event) {
        httpService.deleteItem(event.getEventItem().getId());
        updateList();
        closeDialog();
    }
}
