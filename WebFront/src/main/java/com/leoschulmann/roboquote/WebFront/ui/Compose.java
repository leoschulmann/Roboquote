package com.leoschulmann.roboquote.WebFront.ui;

import com.leoschulmann.roboquote.WebFront.components.ItemService;
import com.leoschulmann.roboquote.itemservice.entities.Item;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.util.List;

@Route(value = "compose", layout = MainLayout.class)
public class Compose extends VerticalLayout {

    public Compose(ItemService itemService) {
        ComboBox<Item> filteringComboBox = new ComboBox<>();
        filteringComboBox.addClassName("compose-querybox");
        filteringComboBox.setWidthFull();
        List<Item> elementsList = itemService.findAll();


        ComboBox.ItemFilter<Item> filter = (ComboBox.ItemFilter<Item>)
                (element, filterString) -> element.getNameRus().toLowerCase().contains(filterString.toLowerCase()) ||
                element.getNameEng().toLowerCase().contains(filterString.toLowerCase()) ||
                element.getPartno().toLowerCase().contains(filterString.toLowerCase());

        filteringComboBox.setItems(filter, elementsList);
        filteringComboBox.setItemLabelGenerator((ItemLabelGenerator<Item>) item -> item.getPartno() + " "
                + item.getNameRus().substring(0, Math.min(item.getNameRus().length(), 35)) + " | "
                + item.getNameEng().substring(0, Math.min(item.getNameEng().length(), 35))
        );

        filteringComboBox.setClearButtonVisible(true);

        add(filteringComboBox);

    }
}
