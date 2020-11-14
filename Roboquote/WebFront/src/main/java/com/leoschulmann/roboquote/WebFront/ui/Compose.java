package com.leoschulmann.roboquote.WebFront.ui;

import com.leoschulmann.roboquote.WebFront.components.CurrencyFormatService;
import com.leoschulmann.roboquote.WebFront.components.InventoryItemToItemPositionConverter;
import com.leoschulmann.roboquote.WebFront.components.ItemService;
import com.leoschulmann.roboquote.WebFront.components.QuoteSectionHandler;
import com.leoschulmann.roboquote.itemservice.entities.Item;
import com.leoschulmann.roboquote.quoteservice.entities.ItemPosition;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;

@Route(value = "compose", layout = MainLayout.class)
public class Compose extends VerticalLayout {
    private ItemService itemService;
    private CurrencyFormatService currencyFormatter;
    private InventoryItemToItemPositionConverter converter;
    private QuoteSectionHandler sectionHandler;

    private VerticalLayout gridsBlock;
    private List<SectionGrid> grids;
    private ComboBox<SectionGrid> avaiableGridsBox;

    static final String DEFAULT_SECTION_NAME = "New quote section";

    public Compose(ItemService itemService, CurrencyFormatService currencyFormatter,
                   InventoryItemToItemPositionConverter converter, QuoteSectionHandler sectionHandler) {

        this.itemService = itemService;
        this.currencyFormatter = currencyFormatter;
        this.converter = converter;
        this.sectionHandler = sectionHandler;

        grids = new ArrayList<>();
        add(prepareControlBlock());
        gridsBlock = createGridsBlock();
        addNewGrid(DEFAULT_SECTION_NAME);

        //add(quoteinfoBlock);
        add(gridsBlock);
        add(createFinishBlock());
    }

    private HorizontalLayout prepareControlBlock() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        ComboBox<Item> searchBox = getItemComboBox();
        avaiableGridsBox = new ComboBox<>();
        avaiableGridsBox.setItems(grids);
        Button addToGridBtn = new Button(VaadinIcon.PLUS.create());
        addToGridBtn.addClickListener(click -> {
            if (searchBox.getValue() != null && avaiableGridsBox.getValue() != null) {
                ItemPosition ip = converter.convert(searchBox.getValue());
                sectionHandler.putToSection(avaiableGridsBox.getValue().getQuoteSection(), ip);
                avaiableGridsBox.getValue().renderItems();
            }
        });
        searchBox.setWidthFull();
        avaiableGridsBox.setWidthFull();
        addToGridBtn.setWidth("15%");
        layout.add(searchBox, avaiableGridsBox, addToGridBtn);
        return layout;
    }

    private VerticalLayout createGridsBlock() {
        VerticalLayout layout = new VerticalLayout();
        return layout;
    }

    private VerticalLayout createFinishBlock() {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        HorizontalLayout controlSublayout = new HorizontalLayout();
        controlSublayout.setWidthFull();
        TextField gridNameInputField = new TextField("Section ");
        Button addNewGridButton = new Button(VaadinIcon.PLUS.create());
        addNewGridButton.addClickListener(click -> {
            addNewGrid(gridNameInputField.getValue());
            gridNameInputField.clear();
        });
        gridNameInputField.setWidth("50%");
        addNewGridButton.setWidth("10%");
        controlSublayout.add(gridNameInputField, addNewGridButton);
        controlSublayout.setAlignItems(Alignment.END);
        add(controlSublayout);
        return layout;
    }

    private void addNewGrid(String name) {
        SectionGrid sg = new SectionGrid(name);
        sg.removeAllColumns();
        sg.addColumn("name").setHeader("Item name").setAutoWidth(true);
        sg.addColumn("qty").setHeader("Quantity");
        sg.addColumn("partNo").setHeader("Part No");
        sg.addColumn(ip -> currencyFormatter.formatMoney(ip.getSellingPrice())).setHeader("Price");
        sg.addColumn(ip -> currencyFormatter.formatMoney(ip.getSellingSum())).setHeader("Sum");
        grids.add(sg);
        avaiableGridsBox.setItems(grids);
        gridsBlock.add(new H5(name), sg);
    }


    private ComboBox<Item> getItemComboBox() {
        ComboBox<Item> filteringComboBox = new ComboBox<>();
        filteringComboBox.addClassName("compose-querybox");
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
