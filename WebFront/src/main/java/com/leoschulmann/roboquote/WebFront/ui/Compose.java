package com.leoschulmann.roboquote.WebFront.ui;

import com.leoschulmann.roboquote.WebFront.components.CurrencyFormatService;
import com.leoschulmann.roboquote.WebFront.components.InventoryItemHelper;
import com.leoschulmann.roboquote.WebFront.components.ItemService;
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
    private CurrencyFormatService currencyFormatService;
    private InventoryItemHelper converter;
    private InventoryItemHelper itemConverter;
    public Compose(ItemService itemService, CurrencyFormatService currencyFormatService,
                   InventoryItemHelper converter) {

        this.itemService = itemService;
        this.currencyFormatService = currencyFormatService;
        this.converter = converter;

        ComboBox<Item> searchBox = getItemComboBox();

        QuoteSection defaultSection = new QuoteSection("default");

        SectionGrid<ItemPosition> defaultGrid = new SectionGrid<>(ItemPosition.class);
        defaultGrid.setContent(defaultSection.getPositions());
        defaultGrid.removeAllColumns();
//        defaultGrid.addColumns("brand", "partno", "nameRus", "nameEng");
//        defaultGrid.addColumn(item -> this.currencyFormatService.formatMoney(item.getSellingPrice())).setHeader("Selling Price");
//        defaultGrid.addColumns("margin", "modified");
//
//        defaultGrid.getColumns().forEach(col -> col.setAutoWidth(true));


        //    private Integer id;
        //
        //    private String name;
        //
        //    @Columns(columns = {
        //            @Column(name = "selling_currency", nullable = false),
        //            @Column(name = "selling_amount", nullable = false)})
        //    @Type(type = "org.jadira.usertype.moneyandcurrency.moneta.PersistentMoneyAmountAndCurrency")
        //    private Money sellingPrice;
        //
        //    private Integer qty;
        //
        //    private Quote quote;
        //
        //    private Integer itemId;

        defaultGrid.addColumns("name", "qty", "partNo");


        searchBox.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                converter.append(defaultSection, e.getValue());
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
