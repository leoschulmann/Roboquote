package com.leoschulmann.roboquote.WebFront.ui;

import com.leoschulmann.roboquote.WebFront.components.CurrencyFormatService;
import com.leoschulmann.roboquote.WebFront.components.ItemService;
import com.leoschulmann.roboquote.itemservice.entities.Item;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("inventory")
public class MainView extends VerticalLayout {
    private final ItemService itemService;
    private Grid<Item> grid = new Grid<>(Item.class);

    public MainView(ItemService itemService, CurrencyFormatService currencyFormatService) {
        this.itemService = itemService;
        setSizeFull();
        grid.removeAllColumns();
        grid.addColumns("brand", "partno", "nameRus", "nameEng");
        grid.addColumn(item -> currencyFormatService.formatMoney(item.getSellingPrice())).setHeader("Selling Price");
        grid.addColumns("margin", "modified");

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        add(grid);
        updateList();
    }

    private void updateList() {
        grid.setItems(itemService.findAll());
    }

}
