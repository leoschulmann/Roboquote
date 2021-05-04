package com.leoschulmann.roboquote.WebFront.ui;

import com.leoschulmann.roboquote.WebFront.components.CachingService;
import com.leoschulmann.roboquote.WebFront.components.CurrencyFormatService;
import com.leoschulmann.roboquote.WebFront.components.HttpRestService;
import com.leoschulmann.roboquote.WebFront.events.InventoryCreateItemEvent;
import com.leoschulmann.roboquote.WebFront.events.InventoryDeleteItemEvent;
import com.leoschulmann.roboquote.WebFront.events.InventoryFormCloseEvent;
import com.leoschulmann.roboquote.WebFront.events.InventoryUpdateItemEvent;
import com.leoschulmann.roboquote.WebFront.ui.bits.GridSizeCombobox;
import com.leoschulmann.roboquote.WebFront.ui.bits.ZoomButtons;
import com.leoschulmann.roboquote.itemservice.entities.Item;
import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import org.apache.commons.lang3.StringUtils;
import org.javamoney.moneta.Money;
import org.vaadin.klaudeta.PaginatedGrid;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.vaadin.flow.component.grid.GridVariant.*;

@Route(value = "inventory", layout = MainLayout.class)
public class InventoryView extends VerticalLayout {
    private final CurrencyFormatService currencyFormatService;
    private final CachingService cachingService;
    private PaginatedGrid<Item> grid;
    private final InventoryForm form;
    private final Dialog dialog;
    private final ListDataProvider<Item> dataProvider;
    private final ArrayList<Item> data;
    private final HttpRestService httpService;

    public InventoryView(
            HttpRestService httpService,
            CurrencyFormatService currencyFormatService,
            CachingService cachingService) {

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
        HorizontalLayout layout = new HorizontalLayout();
        layout.setAlignItems(Alignment.CENTER);
        layout.setWidthFull();

        Button newItem = createNewItem();
        newItem.setWidth("20em");

        ComboBox<String> sizeCombo = new GridSizeCombobox(grid, data);
        ComboBox<String> brandCombo = new ComboBox<>();
        brandCombo.setItems(getDistinctBrands());
        brandCombo.setPlaceholder("Brand");
        brandCombo.getElement().setProperty("title", "Brand");
        brandCombo.setClearButtonVisible(true);

        brandCombo.addValueChangeListener(e -> {
            if (e.getValue() == null) {
                dataProvider.clearFilters();
            } else {
                dataProvider.setFilter(i -> i.getBrand().equals(e.getValue()));
            }
        });

        TextField search = new TextField();
        search.setPlaceholder("Search name or part no.");
        search.setClearButtonVisible(true);
        search.addValueChangeListener(event -> dataProvider.addFilter(
                item -> StringUtils.containsIgnoreCase(item.getNameRus(), search.getValue()) ||
                        StringUtils.containsIgnoreCase(item.getPartno(), search.getValue())
        ));
        search.setValueChangeMode(ValueChangeMode.EAGER);
        search.setWidthFull();

        ToggleButton wrap = new ToggleButton(false);
        wrap.getElement().setAttribute("title", "Wrap cell contents");
        wrap.addValueChangeListener(e -> {
            if (e.getValue()) {
                grid.addThemeVariants(LUMO_WRAP_CELL_CONTENT);
            } else {
                grid.removeThemeVariants(LUMO_WRAP_CELL_CONTENT);
            }
            grid.refreshPaginator();
        });

        ZoomButtons zoomButtons = new ZoomButtons(grid);

        layout.add(newItem, search, brandCombo, sizeCombo, zoomButtons.getZoomOut(), zoomButtons.getZoomIn(), wrap);
        return layout;
    }

    private PaginatedGrid<Item> drawGrid() {
        grid = new PaginatedGrid<>(Item.class);
        grid.setDataProvider(dataProvider);
        grid.removeAllColumns();
        grid.addThemeVariants(LUMO_COMPACT, LUMO_ROW_STRIPES, LUMO_COLUMN_BORDERS);
        grid.addColumn(Item::getId)
                .setHeader("Id")
                .setSortable(true)
                .setAutoWidth(true)
                .setResizable(true).setFlexGrow(0);
        grid.addColumn(Item::getBrand)
                .setHeader("Brand")
                .setKey("brand")
                .setSortable(true)
                .setAutoWidth(true)
                .setResizable(true).setFlexGrow(0);
        grid.addColumn(Item::getPartno)
                .setHeader("Part No")
                .setKey("partno")
                .setSortable(true)
                .setAutoWidth(true)
                .setResizable(true).setFlexGrow(0);
        grid.addColumn(Item::getNameRus)
                .setHeader("Name RUS")
                .setSortable(true)
                .setResizable(true).setFlexGrow(1);
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
                .setAutoWidth(true).setFlexGrow(0);

        grid.asSingleSelect().addValueChangeListener(event -> editItem(event.getValue()));
        grid.setPaginatorSize(5);
        return grid;
    }

    private static Icon getIcon(boolean boo) {
        Icon i;
        if (boo) {
            i = VaadinIcon.CIRCLE.create();
            i.setSize("10px");
            i.getElement().setAttribute("title", "Overridden price");
        } else {
            i = VaadinIcon.CIRCLE_THIN.create();
            i.setSize("10px");
            i.getElement().setAttribute("title", "Calculated price");
        }
        return i;
    }

    private Button createNewItem() {
        Button newItemBtn = new Button("Create new item");
        newItemBtn.getElement().setAttribute("tile", "Create new item");
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
        cachingService.updateItemCache();
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

    private List<String> getDistinctBrands() {
        return data.stream().map(Item::getBrand).distinct().sorted().collect(Collectors.toList());
    }
}
