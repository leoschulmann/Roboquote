package com.leoschulmann.roboquote.WebFront.ui;

import com.leoschulmann.roboquote.WebFront.components.CurrencyFormatService;
import com.leoschulmann.roboquote.WebFront.components.InventoryItemToItemPositionConverter;
import com.leoschulmann.roboquote.WebFront.components.ItemService;
import com.leoschulmann.roboquote.WebFront.components.QuoteSectionHandler;
import com.leoschulmann.roboquote.itemservice.entities.Item;
import com.leoschulmann.roboquote.quoteservice.entities.ItemPosition;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Span;
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
    private List<SectionGrid> gridList;
    private ComboBox<SectionGrid> avaiableGridsBox;

    static final String DEFAULT_SECTION_NAME = "New quote section";

    public Compose(ItemService itemService, CurrencyFormatService currencyFormatter,
                   InventoryItemToItemPositionConverter converter, QuoteSectionHandler sectionHandler) {

        this.itemService = itemService;
        this.currencyFormatter = currencyFormatter;
        this.converter = converter;
        this.sectionHandler = sectionHandler;

        Accordion accordion = new Accordion();
        accordion.setWidthFull();

        gridList = new ArrayList<>();
        accordion.add("Inventory lookup", prepareControlBlock());
        add(accordion);
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
        avaiableGridsBox.setItems(gridList);
        Button addToGridBtn = new Button(VaadinIcon.PLUS.create());
        addToGridBtn.addClickListener(click -> {
            if (searchBox.getValue() != null && avaiableGridsBox.getValue() != null) {
                ItemPosition ip = converter.convert(searchBox.getValue());
                sectionHandler.putToSection(avaiableGridsBox.getValue().getQuoteSection(), ip);
                avaiableGridsBox.getValue().renderItems();
            }
        });
        searchBox.setWidthFull();
        searchBox.setPlaceholder("Inventory lookup");
        avaiableGridsBox.setWidthFull();
        avaiableGridsBox.setPlaceholder("Quote section");
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
        TextField gridNameInputField = new TextField();  //todo add validation (non-empty, non-duplicating, etc)
        gridNameInputField.setPlaceholder("Add new section ");
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
        Accordion accordion = new Accordion();
        accordion.setWidthFull();
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        HorizontalLayout head = new HorizontalLayout();
        head.setWidthFull();
        SectionGrid sg = new SectionGrid(name);
        sg.removeAllColumns();
        sg.addColumn("name").setHeader("Item name").setAutoWidth(true);
        sg.addColumn("qty").setHeader("Quantity");
        sg.addColumn("partNo").setHeader("Part No");
        sg.addColumn(ip -> currencyFormatter.formatMoney(ip.getSellingPrice())).setHeader("Price");
        sg.addColumn(ip -> currencyFormatter.formatMoney(ip.getSellingSum())).setHeader("Sum");
        sg.setHeightByRows(true);
        gridList.add(sg);
        avaiableGridsBox.setItems(gridList);
        TextField nameField = new TextField();
        nameField.setWidth("50%");
        nameField.setValue(name);
        nameField.setVisible(false);

        nameField.addValueChangeListener(event -> {
            sg.setName(event.getValue());
            avaiableGridsBox.setItems(gridList);
            accordion.getOpenedPanel().ifPresent(panel -> panel.setSummary(new Span(event.getValue())));
        });

        Button editNameBtn = new Button(VaadinIcon.EDIT.create());
        editNameBtn.addClickListener(c -> nameField.setVisible(!nameField.isVisible()));

        Button deleteBtn = new Button(VaadinIcon.TRASH.create());
        deleteBtn.addClickListener(c -> {
            gridsBlock.remove(head, sg);
            gridList.remove(sg);
            avaiableGridsBox.setItems(gridList); //todo refactor as method
        });

        head.add(nameField,editNameBtn, deleteBtn);
        layout.add(head, sg);
        accordion.add(name, layout);
        gridsBlock.add(accordion);
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
