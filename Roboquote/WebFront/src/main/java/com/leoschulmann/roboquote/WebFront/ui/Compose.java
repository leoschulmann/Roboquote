package com.leoschulmann.roboquote.WebFront.ui;

import com.leoschulmann.roboquote.WebFront.components.CurrencyFormatService;
import com.leoschulmann.roboquote.WebFront.components.InventoryItemToItemPositionConverter;
import com.leoschulmann.roboquote.WebFront.components.ItemService;
import com.leoschulmann.roboquote.WebFront.components.QuoteSectionHandler;
import com.leoschulmann.roboquote.itemservice.entities.Item;
import com.leoschulmann.roboquote.quoteservice.entities.ItemPosition;
import com.leoschulmann.roboquote.quoteservice.entities.QuoteSection;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.util.List;

@Route(value = "compose", layout = MainLayout.class)
public class Compose extends VerticalLayout {
    private ItemService itemService;
    private CurrencyFormatService currencyFormatter;
    private InventoryItemToItemPositionConverter converter;
    private QuoteSectionHandler sectionHandler;

    public Compose(ItemService itemService, CurrencyFormatService currencyFormatter,
                   InventoryItemToItemPositionConverter converter, QuoteSectionHandler sectionHandler) {

        this.itemService = itemService;
        this.currencyFormatter = currencyFormatter;
        this.converter = converter;
        this.sectionHandler = sectionHandler;

        ComboBox<Item> searchBox = getItemComboBox();

        QuoteSection defaultSection = new QuoteSection("default");

        SectionGrid<ItemPosition> defaultGrid = new SectionGrid<>(ItemPosition.class);
        defaultGrid.setContent(defaultSection.getPositions());
        defaultGrid.removeAllColumns();

        defaultGrid.addColumn("name").setHeader("Item name").setAutoWidth(true);
        defaultGrid.addColumn("qty").setHeader("Quantity");
        defaultGrid.addColumn("partNo").setHeader("Part No");
        defaultGrid.addColumn(ip -> currencyFormatter.formatMoney(ip.getSellingPrice())).setHeader("Price");
        defaultGrid.addColumn(ip -> currencyFormatter.formatMoney(ip.getSellingSum())).setHeader("Sum");

        searchBox.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                ItemPosition ip = converter.convert(e.getValue());
                sectionHandler.putToSection(defaultSection, ip);
                defaultGrid.renderItems();
            }
        });
        add(searchBox);
        add(defaultGrid);
    }

    private ComboBox<Item> getItemComboBox() {
        ComboBox<Item> filteringComboBox = new ComboBox<>();
        filteringComboBox.addClassName("compose-querybox");
        filteringComboBox.setWidthFull();
        List<Item> elementsList = itemService.findAll(); //todo make 'prepare elements' mechanism to reduce DB load


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
        return filteringComboBox;
    }
}
