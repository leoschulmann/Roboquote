package com.leoschulmann.roboquote.WebFront.ui;

import com.leoschulmann.roboquote.WebFront.components.ItemService;
import com.leoschulmann.roboquote.itemservice.entities.Item;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.springframework.beans.factory.annotation.Autowired;

public class MainView extends VerticalLayout {
    @Autowired
    ItemService itemService;

    private Grid<Item> grid = new Grid<>(Item.class);


    public MainView() {
        add(grid);
        updateList();
    }

    private void updateList() {
        grid.setItems(itemService.findAll());
    }

}
